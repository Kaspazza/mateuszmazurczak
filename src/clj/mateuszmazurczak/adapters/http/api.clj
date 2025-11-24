(ns mateuszmazurczak.adapters.http.api
  "API handlers for JSON endpoints."
  (:require
   [ring.util.http-response :as http-response]))

;; =============================================================================
;; Mock Data
;; =============================================================================

(def mock-solutions
  "Mock solutions for AOC challenges."
  [{:id "1"
    :year 2025
    :challenge 1
    :part 1
    :author-name "Mateusz Mazurczak"
    :github-profile "https://github.com/kaspazza"
    :content-type :code-snippet
    :content "(defn solve [input]\n  (reduce + (map parse-long input)))"
    :created-at "2025-01-15T10:30:00Z"}
   {:id "2"
    :year 2025
    :challenge 1
    :part 1
    :author-name "Kaspazza Anonymous"
    :github-profile "https://github.com/kaspazza"
    :content-type :repo-link
    :content "https://github.com/bob/aoc-2025"
    :created-at "2025-01-15T14:22:00Z"}
   {:id "3"
    :year 2025
    :challenge 2
    :part 2
    :author-name "Charlie Day"
    :content-type :code-snippet
    :content
    "(defn solve-part-2 [input]\n  (->> input\n       (partition 2)\n       (map (fn [[a b]] (* a b)))\n       (reduce +)))"
    :created-at "2025-01-16T08:15:00Z"}])

;; =============================================================================
;; Handlers
;; =============================================================================

(defn get-solutions
  "GET /api/aoc/solutions - Fetch solutions for a specific year/challenge/part."
  [request]
  (let [year (some-> (get-in request [:params :year])
                     Integer/parseInt)
        challenge (some-> (get-in request [:params :challenge])
                          Integer/parseInt)
        part (some-> (get-in request [:params :part])
                     Integer/parseInt)
        filtered-solutions (filter #(and (= (:year %) year)
                                         (= (:challenge %) challenge)
                                         (= (:part %) part))
                                   mock-solutions)]
    (http-response/ok filtered-solutions)))

(defn post-solution
  "POST /api/aoc/solutions - Submit a new solution (mock - just returns success)."
  [request]
  (let [_solution (:body-params request)]
    ;; In a real app, you'd save to database here
    (http-response/ok {:success true
                       :message "Solution submitted successfully"})))
