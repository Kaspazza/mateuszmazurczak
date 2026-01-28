(ns mateuszmazurczak.domain.aoc.solution
  "Solution enrichment and normalization."
  (:require
   [clojure.string :as str]))

;; =============================================================================
;; GitHub Username Handling
;; =============================================================================

(defn parse-github-username
  "Parse and normalize GitHub username from various input formats.
   
   Accepts:
   - Just username: \"octocat\" -> \"octocat\"
   - With @: \"@octocat\" -> \"octocat\"
   - Full URL: \"https://github.com/octocat\" -> \"octocat\"
   - Full URL: \"http://github.com/octocat\" -> \"octocat\"
   - Full URL with www: \"https://www.github.com/octocat\" -> \"octocat\"
   - Full URL with trailing slash: \"https://github.com/octocat/\" -> \"octocat\"
   - URL without protocol: \"github.com/octocat\" -> \"octocat\"
   - URL without protocol with www: \"www.github.com/octocat\" -> \"octocat\"
   
   Returns nil for empty/invalid input."
  [input]
  (when (and input (not (str/blank? input)))
    (let [trimmed (str/trim input)
          without-at (if (str/starts-with? trimmed "@") (subs trimmed 1) trimmed)
          username (last (remove str/blank? (str/split without-at #"/")))]
      (when (and username (not (str/blank? username))) username))))

(defn build-github-url
  "Build GitHub profile URL from username."
  [username]
  (when (and username (not (str/blank? username))) (str "https://github.com/" username)))

(defn format-github-display
  "Format GitHub username for display with @ prefix."
  [username]
  (when (and username (not (str/blank? username))) (str "@" username)))

;; =============================================================================
;; Solution Enrichment
;; =============================================================================

(defn enrich-solution-with-github-data
  "Enrich solution with GitHub URL and display username.
   
   Takes solution with :github-username field (can be username or full URL for backward compatibility),
   normalizes it to just username, then adds :github-profile (full URL) and :github-username-display (formatted with @)."
  [solution]
  (if-let [raw-username (:github-username solution)]
    (let [normalized-username (parse-github-username raw-username)]
      (assoc solution
             :github-username normalized-username
             :github-profile (build-github-url normalized-username)
             :github-username-display (format-github-display normalized-username)))
    solution))



(defn enrich-solution-voting-state
  "Enrich solution with voting state from cache.
   
   Transforms solution by:
   - Converting :content-type string to keyword (backend returns string)
   - Adding :voted-best-practices? flag (from cache)
   - Adding :voted-clever? flag (from cache)"
  [solution has-voted-fn]
  (let [solution-id (:id solution)]
    (-> solution
        (update :content-type keyword)
        (assoc :voted-best-practices? (has-voted-fn solution-id :best-practices))
        (assoc :voted-clever? (has-voted-fn solution-id :clever)))))

;; =============================================================================
;; Solution Normalization
;; =============================================================================

(defn normalize-solutions
  "Normalize a collection of solutions into entities map and IDs vector."
  [solutions]
  {:entities (into {} (map (fn [solution] [(:id solution) solution]) solutions))
   :ids (mapv :id solutions)})

(defn denormalize-solutions
  "Denormalize solution IDs back into vector of full solution objects with GitHub data enrichment."
  [solution-ids entities]
  (into []
        (comp (map #(get entities %)) (filter some?) (map enrich-solution-with-github-data))
        solution-ids))

;; =============================================================================
;; Solution Sorting
;; =============================================================================

(defn sort-solutions-by-votes
  "Sort solutions by total vote count (best-practices + clever) in descending order."
  [solutions]
  (->> solutions
       (sort-by #(+ (or (:best-practices-count %) 0) (or (:clever-count %) 0)) >)
       vec))



;; =============================================================================
;; Solution Form
;; =============================================================================

(defn prepare-solution-payload
  "Prepare form data for submission by normalizing GitHub username."
  [form]
  (-> form
      (update :github-username parse-github-username)))

(defn should-show-playground?
  "Determine if playground menu should be shown for a solution."
  [content-type]
  (= content-type :code-snippet))

;; =============================================================================
;; Vote Logic
;; =============================================================================

(defn vote-type->key
  "Convert vote type to the voted flag key."
  [vote-type]
  (if (= vote-type :best-practices) :voted-best-practices? :voted-clever?))

(defn update-solution-votes
  "Update solution vote counts and voted flag."
  [solution vote-type best-practices-count clever-count]
  (let [voted-key (vote-type->key vote-type)]
    (assoc solution
           :best-practices-count
           best-practices-count
           :clever-count
           clever-count
           voted-key
           true)))

;; =============================================================================
;; Content Type Normalization
;; =============================================================================

(defn normalize-content-type
  "Normalize content-type from string or keyword to keyword.
   
   Accepts: \"code-snippet\", \"repo-link\", :code-snippet, :repo-link
   Returns: :code-snippet or :repo-link"
  [content-type]
  (if (keyword? content-type) content-type (keyword content-type)))

;; =============================================================================
;; Transaction Building
;; =============================================================================

(defn build-save-solution-tx
  "Build transaction data for saving a new AOC solution.
   
   Takes:
   - solution-id: UUID for the solution
   - now: Timestamp (Date or inst)
   - solution map with keys:
     - :year (int)
     - :challenge (int)
     - :author-name (string)
     - :github-username (optional string) - Just the GitHub username
     - :content-type (string or keyword: code-snippet or repo-link)
     - :content (string)
   
   Returns vector of transaction maps for Datalevin.
   
   This is a pure function - no side effects, UUID and timestamp are injected."
  [solution-id now {:keys [year challenge author-name github-username content-type content]}]
  (let [normalized-content-type (normalize-content-type content-type)
        base-tx {:aoc-solution/id solution-id
                 :aoc-solution/year year
                 :aoc-solution/challenge challenge
                 :aoc-solution/author-name author-name
                 :aoc-solution/content-type normalized-content-type
                 :aoc-solution/content content
                 :aoc-solution/created-at now}
        tx (if (and github-username (string? github-username) (not (str/blank? github-username)))
             (assoc base-tx :aoc-solution/github-username github-username)
             base-tx)]
    [tx]))

;; =============================================================================
;; Solution Sorting
;; =============================================================================

(defn sort-solutions-by-created-at
  "Sort solutions by created-at timestamp, newest first."
  [solutions]
  (sort-by :created-at #(compare %2 %1) solutions))
