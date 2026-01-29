(ns mateuszmazurczak.application.aoc.solution
  "Application-layer orchestration for AOC solutions.
   
   Contains schemas, database orchestration, impure operations, and database-specific
   queries/mappers (Datalevin). Imports domain/ and ports/ only."
  (:require
   [clojure.string                       :as str]
   [mateuszmazurczak.domain.aoc.solution :as solution]
   [mateuszmazurczak.ports.database      :as db])
  (:import [java.util Date UUID]))

;; =============================================================================
;; Schemas (Malli)
;; =============================================================================

(def SaveSolutionRequest
  "Schema for saving a new AOC solution."
  [:map
   [:year [:and :int [:>= 2015] [:<= 2025]]]
   [:challenge [:and :int [:>= 1] [:<= 24]]]
   [:author-name [:string {:min 1}]]
   [:github-username {:optional true}
    [:maybe :string]]
   [:content-type [:enum "code-snippet" "repo-link" :code-snippet :repo-link]]
   [:content [:string {:min 1}]]])

;; =============================================================================
;; Database Queries & Mappers (Datalevin-specific)
;; =============================================================================

(def build-get-solutions-query
  "Datalog query for fetching solutions by year and challenge.
   
   Datalevin-specific. Returns a Datalog query that finds all solutions matching the criteria."
  '[:find
    [(pull ?e [*]) ...]
    :in
    $
    ?year
    ?challenge
    :where
    [?e :aoc-solution/year ?year]
    [?e :aoc-solution/challenge ?challenge]])

(def find-votes-query
  "Query to find all votes by type for a given solution.
   
   Datalevin-specific. Joins through the solution entity's UUID to find votes.
   Returns collection of vote entity IDs."
  '[:find
    [?vote ...]
    :in
    $
    ?solution-uuid
    ?vote-type
    :where
    [?solution :aoc-solution/id ?solution-uuid]
    [?vote :aoc-vote/solution-id ?solution]
    [?vote :aoc-vote/vote-type ?vote-type]])

(def solution-votes-query
  "Query to find all vote entities for a given solution.
   
   Datalevin-specific. Returns Datalog query that finds vote entity IDs."
  '[:find [?vote ...] :in $ ?solution-id :where [?vote :aoc-vote/solution-id ?solution-id]])

(defn solution-tuple->map
  "Convert Datalevin query result tuple to domain solution map.
   
   Maps from database representation (namespaced keys) to domain representation.
   Returns solution map with string ID (for frontend compatibility)."
  [{:aoc-solution/keys
    [id year challenge author-name github-username content-type content created-at]}]
  (cond-> {:id (str id)
           :year year
           :challenge challenge
           :author-name author-name
           :content-type content-type
           :content content
           :created-at (str created-at)}
    github-username (assoc :github-username github-username)))

;; =============================================================================
;; Content Type Normalization
;; =============================================================================

(defn- normalize-content-type
  "Normalize content-type from string or keyword to keyword.
   
   Accepts: \"code-snippet\", \"repo-link\", :code-snippet, :repo-link
   Returns: :code-snippet or :repo-link"
  [content-type]
  (if (keyword? content-type) content-type (keyword content-type)))

;; =============================================================================
;; Transaction Building (Datalevin-specific)
;; =============================================================================

(defn- build-save-solution-tx
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
;; Transaction Generation (Impure - generates UUID and timestamp)
;; =============================================================================

(defn generate-save-solution-tx
  "Generate transaction data for saving a new AOC solution.
   
   IMPURE: Generates UUID and timestamp.
   
   Takes a solution map (see SaveSolutionRequest schema).
   Returns tuple of [solution-id tx-data] where:
   - solution-id is the generated UUID
   - tx-data is Datalevin transaction data (vector of maps)"
  [solution-data]
  (let [solution-id (UUID/randomUUID)
        now (Date.)
        tx-data (build-save-solution-tx solution-id now solution-data)]
    [solution-id tx-data]))

;; =============================================================================
;; Database Orchestration (calls ports + domain)
;; =============================================================================

(defn count-votes
  "Count votes for a solution by type.
   
   Takes:
   - db: Database connection
   - solution-id: UUID of the solution
   - vote-type: :best-practices or :clever
   
   Returns count as integer (0 if no votes)."
  [db solution-id vote-type]
  (count (db/query db find-votes-query solution-id vote-type)))

(defn enrich-with-vote-counts
  "Enrich solution map with vote counts by querying the database.
   
   Takes:
   - db: Database connection
   - solution: Solution map with :id (string UUID)
   
   Returns solution map with :best-practices-count and :clever-count added."
  [db solution]
  (let [solution-uuid (UUID/fromString (:id solution))
        best-practices-count (count-votes db solution-uuid :best-practices)
        clever-count (count-votes db solution-uuid :clever)]
    (assoc solution :best-practices-count best-practices-count :clever-count clever-count)))

(defn fetch-solutions
  "Fetch solutions for a given year and challenge, enriched with vote counts.
   
   Takes:
   - db: Database connection
   - year: Year of challenge (int)
   - challenge: Challenge day (int)
   
   Returns vector of solution maps, sorted by created-at (newest first)."
  [db year challenge]
  (let [results (db/query db build-get-solutions-query year challenge)]
    (->> results
         (map solution-tuple->map)
         (map (partial enrich-with-vote-counts db))
         (solution/sort-solutions-by-created-at))))

(defn query-solution
  "Query a solution by UUID.
   
   Takes:
   - db: Database connection
   - solution-uid: UUID of the solution
   
   Returns solution entity map or nil if not found."
  [db solution-uid]
  (db/pull-entity db '[*] [:aoc-solution/id solution-uid]))

(defn build-delete-solution-tx
  "Build transaction data to delete a solution and all its votes.
   
   Takes:
   - db: Database connection
   - solution-uid: UUID of the solution
   
   Returns transaction data (vector of retract operations)."
  [db solution-uid]
  (let [vote-eids (db/query db solution-votes-query solution-uid)
        delete-votes-tx (when (seq vote-eids) (mapv (fn [eid] [:db/retractEntity eid]) vote-eids))
        delete-solution-tx [[:db/retractEntity [:aoc-solution/id solution-uid]]]]
    (if delete-votes-tx (concat delete-votes-tx delete-solution-tx) delete-solution-tx)))
