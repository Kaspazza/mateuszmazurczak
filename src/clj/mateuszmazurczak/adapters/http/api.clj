(ns mateuszmazurczak.adapters.http.api
  (:require
   [clojure.string                            :as str]
   [malli.core                                :as m]
   [malli.error                               :as me]
   [mateuszmazurczak.application.aoc.solution :as aoc-app]
   [mateuszmazurczak.domain.aoc.vote          :as vote]
   [mateuszmazurczak.ports.database           :as db]
   [mateuszmazurczak.ports.logging            :as log]
   [ring.util.http-response                   :as http-response]))

(defn validate-query-params
  "Validate and parse query parameters for fetching solutions.
   
   Returns map with :valid? and either :data or :errors."
  [params]
  (let [year (parse-long (:year params))
        challenge (parse-long (:challenge params))]
    (cond
      (nil? year) {:valid? false
                   :errors {:year "Year is required and must be an integer"}}
      (nil? challenge) {:valid? false
                        :errors {:challenge "Challenge is required and must be an integer"}}
      :else {:valid? true
             :data {:year year
                    :challenge challenge}})))

(defn get-solutions
  "GET /api/aoc/solutions - Fetch solutions for a specific year/challenge.
   
   Query params:
   - year (required): Year of the challenge (2015-2025)
   - challenge (required): Challenge day (1-24)
   
   Returns:
   - 200 with array of solutions (with vote counts computed from vote refs)
   - 400 if parameters are invalid
   - 500 if database query fails"
  [{:keys [database logger params]}]
  (let [validation (validate-query-params params)]
    (if-not (:valid? validation)
      (http-response/bad-request {:error "Invalid parameters"
                                  :details (:errors validation)})
      (try (let [{:keys [year challenge]} (:data validation)
                 solutions (aoc-app/fetch-solutions database year challenge)]
             (http-response/ok {:solutions solutions}))
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
   - author-name (string): Name of the author
   - github-username (optional string): GitHub username (can be just username, @username, or full URL - will be normalized)
   - content-type (string): Either 'code-snippet' or 'repo-link'
   - content (string): The solution code or repository URL
   
   Returns:
   - 200 with success message and solution-id
   - 400 if request body is invalid
   - 500 if database transaction fails"
  [{:keys [database logger body-params]}]
  (let [validation-result (m/explain aoc-app/SaveSolutionRequest body-params)]
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
      (try (let [[solution-id tx-data] (aoc-app/generate-save-solution-tx body-params)]
             (db/transact! database tx-data)
             (log/log! logger
                       {:id ::solution-saved
                        :level :info
                        :msg "AOC solution saved successfully"
                        :data {:year (:year body-params)
                               :challenge (:challenge body-params)
                               :solution-id (str solution-id)}})
             (http-response/ok {:success true
                                :message "Solution submitted successfully"
                                :solution-id (str solution-id)}))
           (catch Exception e
             (log/error! logger
                         {:error e
                          :id ::post-solution-failed
                          :data {:body-params body-params}})
             (http-response/internal-server-error {:error "Failed to save solution"}))))))

(defn post-vote!
  [{:keys [database logger]
    :as _ctx}
   solution-uuid
   solution-id
   vote-type-kw]
  (let [vote-tx (vote/save-vote-tx solution-id vote-type-kw)
        best-practices-count (aoc-app/count-votes database solution-uuid :best-practices)
        clever-count (aoc-app/count-votes database solution-uuid :clever)
        response-data {:success true
                       :solution-id solution-id
                       :best-practices-count best-practices-count
                       :clever-count clever-count}]
    (db/transact! database vote-tx)
    (log/log! logger
              {:id ::vote-recorded
               :level :info
               :msg "Vote recorded for AOC solution"
               :data {:solution-id solution-id
                      :vote-type vote-type-kw}})
    (http-response/ok response-data)))

(defn post-vote
  "POST /api/aoc/solutions/vote - Vote for a solution.
   
   Expected body:
   - solution-id (string): UUID of the solution as string
   - vote-type (string): Either 'best-practices' or 'clever'
   
   Returns:
   - 200 with success message and updated vote counts (computed from vote refs)
   - 400 if request body is invalid or solution doesn't exist
   - 500 if database transaction fails"
  [{:keys [database logger body-params]}]
  (let [{:keys [solution-id vote-type]} body-params]
    (cond
      (or (nil? solution-id) (str/blank? solution-id)) (http-response/bad-request
                                                        {:error "solution-id is required"})
      (not (contains? #{"best-practices" "clever" :best-practices :clever} vote-type))
      (http-response/bad-request {:error "vote-type must be 'best-practices' or 'clever'"})
      :else (try (let [uuid-id (java.util.UUID/fromString solution-id)
                       vote-type-kw (vote/normalize-vote-type vote-type)]
                   (post-vote! {:database database
                                :logger logger}
                               uuid-id
                               solution-id
                               vote-type-kw))
                 (catch IllegalArgumentException _
                   (http-response/bad-request {:error "Invalid solution-id format"}))
                 (catch clojure.lang.ExceptionInfo e
                   (if (= (:type (ex-data e))
                          :mateuszmazurczak.adapters.database.datalevin/entity-not-found)
                     (http-response/bad-request {:error "Solution not found"})
                     (do (log/error! logger
                                     {:error e
                                      :id ::post-vote-failed
                                      :data {:body-params body-params}})
                         (http-response/internal-server-error {:error "Failed to record vote"}))))
                 (catch Exception e
                   (log/error! logger
                               {:error e
                                :id ::post-vote-failed
                                :data {:body-params body-params}})
                   (http-response/internal-server-error {:error "Failed to record vote"}))))))



(defn delete-solution
  "DELETE /api/aoc/solutions/:solution-id - Delete a solution (admin only).
   
   Path params:
   - solution-id (string): UUID of the solution to delete
   
   Headers:
   - X-Admin-Key (required): Admin API key
   
   Admin Authentication:
   - Requires valid X-Admin-Key header
   - Key must match ADMIN_API_KEY environment variable
   - Key must be at least 32 characters
   
   Returns:
   - 200 with success message
   - 401 if admin authentication fails
   - 400 if solution-id is invalid
   - 404 if solution doesn't exist
   - 500 if database transaction fails"
  [{:keys [database logger path-params admin-authenticated?]}]
  (if-not admin-authenticated?
    (do (log/log! logger
                  {:level :warn
                   :id ::delete-solution-unauthorized
                   :msg "Unauthorized delete attempt"})
        (http-response/unauthorized {:error "Admin authentication required"
                                     :details "Provide valid X-Admin-Key header"}))
    (let [solution-id (:solution-id path-params)]
      (cond
        (or (nil? solution-id) (str/blank? solution-id)) (http-response/bad-request
                                                          {:error "solution-id is required"})
        :else (try
                (let [uuid-id (java.util.UUID/fromString solution-id)
                      solution (aoc-app/query-solution database uuid-id)]
                  (if-not solution
                    (do (log/log! logger
                                  {:level :warn
                                   :id ::delete-solution-not-found
                                   :msg "Solution not found for deletion"
                                   :data {:solution-id solution-id}})
                        (http-response/not-found {:error "Solution not found"}))
                    (do (db/transact! database (aoc-app/build-delete-solution-tx database uuid-id))
                        (log/log! logger
                                  {:id ::solution-deleted
                                   :level :info
                                   :msg "AOC solution deleted"
                                   :data {:solution-id solution-id
                                          :year (:aoc-solution/year solution)
                                          :challenge (:aoc-solution/challenge solution)}})
                        (http-response/ok {:success true
                                           :message "Solution deleted successfully"
                                           :solution-id solution-id}))))
                (catch IllegalArgumentException _
                  (http-response/bad-request {:error "Invalid solution-id format"}))
                (catch Exception e
                  (log/error! logger
                              {:error e
                               :id ::delete-solution-failed
                               :data {:solution-id solution-id}})
                  (http-response/internal-server-error {:error "Failed to delete solution"})))))))
