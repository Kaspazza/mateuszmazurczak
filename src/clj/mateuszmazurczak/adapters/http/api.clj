(ns mateuszmazurczak.adapters.http.api
  "API handlers for JSON endpoints."
  (:require
   [malli.core                             :as m]
   [malli.error                            :as me]
   [mateuszmazurczak.domain.aoc.repository :as aoc-repo]
   [mateuszmazurczak.ports.database        :as db]
   [mateuszmazurczak.ports.logging         :as log]
   [ring.util.http-response                :as http-response]))

(defn validate-query-params
  "Validate and parse query parameters for fetching solutions.
   
   Returns map with :valid? and either :data or :errors."
  [params]
  (let [year (parse-long (:year params))
        challenge (parse-long (:challenge params))
        part (parse-long (:part params))]
    (cond
      (nil? year) {:valid? false
                   :errors {:year "Year is required and must be an integer"}}
      (nil? challenge) {:valid? false
                        :errors {:challenge "Challenge is required and must be an integer"}}
      (nil? part) {:valid? false
                   :errors {:part "Part is required and must be an integer"}}
      :else {:valid? true
             :data {:year year
                    :challenge challenge
                    :part part}})))

(defn get-solutions
  "GET /api/aoc/solutions - Fetch solutions for a specific year/challenge/part.
   
   Query params:
   - year (required): Year of the challenge (2015-2025)
   - challenge (required): Challenge day (1-24)
   - part (required): Part number (1 or 2)
   
   Returns:
   - 200 with array of solutions
   - 400 if parameters are invalid
   - 500 if database query fails"
  [{:keys [database logger params]}]
  (let [validation (validate-query-params params)]
    (if-not (:valid? validation)
      (http-response/bad-request {:error "Invalid parameters"
                                  :details (:errors validation)})
      (try (let [{:keys [year challenge part]} (:data validation)
                 query (aoc-repo/build-get-solutions-query)
                 results (db/query database query year challenge part)
                 solutions (->> results
                                (map aoc-repo/solution-tuple->map)
                                (aoc-repo/sort-solutions-by-created-at))]
             (http-response/ok solutions))
           (catch Exception e
             (log/error! logger
                         {:error e
                          :id ::get-solutions-failed
                          :data {:params params}})
             (http-response/internal-server-error {:error "Failed to fetch solutions"}))))))

(defn post-solution
  "POST /api/aoc/solutions - Submit a new solution.
   
   Expected body:
   - year (int): Year of the challenge (2015-2025)
   - challenge (int): Challenge day (1-24)
   - part (int): Part number (1 or 2)
   - author-name (string): Name of the author
   - github-profile (optional string): GitHub profile URL
   - content-type (string): Either 'code-snippet' or 'repo-link'
   - content (string): The solution code or repository URL
   
   Returns:
   - 200 with success message
   - 400 if request body is invalid
   - 500 if database transaction fails"
  [{:keys [database logger body-params]}]
  (let [validation-result (m/explain aoc-repo/SaveSolutionRequest body-params)]
    (if validation-result
      (let [humanized-errors (me/humanize validation-result)]
        (log/log! logger
                  {:level :warn
                   :id ::post-solution-validation-failed
                   :msg "Solution validation failed"
                   :data {:body-params body-params
                          :errors humanized-errors}})
        (http-response/bad-request {:error "Invalid solution data"
                                    :details humanized-errors}))
      (try (let [tx-data (aoc-repo/build-save-solution-tx body-params)]
             (db/transact! database tx-data)
             (log/log! logger
                       {:id ::solution-saved
                        :level :info
                        :msg "AOC solution saved successfully"
                        :data {:year (:year body-params)
                               :challenge (:challenge body-params)
                               :part (:part body-params)}})
             (http-response/ok {:success true
                                :message "Solution submitted successfully"}))
           (catch Exception e
             (log/error! logger
                         {:error e
                          :id ::post-solution-failed
                          :data {:body-params body-params}})
             (http-response/internal-server-error {:error "Failed to save solution"}))))))
