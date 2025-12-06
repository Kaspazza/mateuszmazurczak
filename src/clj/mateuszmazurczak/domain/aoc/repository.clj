(ns mateuszmazurczak.domain.aoc.repository
  "Domain logic for AOC solution repository operations.
   
   Functions for building queries and transaction data."
  (:require
   [clojure.string                  :as str]
   [mateuszmazurczak.ports.database :as db])
  (:import [java.util Date UUID]))

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

(defn normalize-content-type
  "Normalize content-type from string or keyword to keyword.
   
   Accepts: \"code-snippet\", \"repo-link\", :code-snippet, :repo-link
   Returns: :code-snippet or :repo-link"
  [content-type]
  (if (keyword? content-type) content-type (keyword content-type)))

(defn build-save-solution-tx
  "Build transaction data for saving a new AOC solution.
   
   Takes a solution map with keys:
   - :year (int)
   - :challenge (int)
   - :author-name (string)
   - :github-username (optional string) - Just the GitHub username
   - :content-type (string or keyword: code-snippet or repo-link)
   - :content (string)
   
   Returns tuple of [solution-id tx-data] where:
   - solution-id is the generated UUID
   - tx-data is Datalevin transaction data (vector of maps)
   
   Generates UUID and timestamp automatically."
  [{:keys [year challenge author-name github-username content-type content]}]
  (let [solution-id (UUID/randomUUID)
        now (Date.)
        normalized-content-type (normalize-content-type content-type)
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
    [solution-id [tx]]))

(defn build-get-solutions-query
  "Build Datalog query for fetching solutions by year and challenge.
   
   Returns a Datalog query that finds all solutions matching the criteria,
   sorted by creation date (newest first)."
  []
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
   
   Joins through the solution entity's UUID to find votes.
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

(defn count-votes
  "Count votes for a solution by type.
   
   Takes:
   - db: Database connection
   - solution-id: UUID of the solution
   - vote-type: :best-practices or :clever
   
   Returns count as integer (0 if no votes)."
  [db solution-id vote-type]
  (count (db/query db find-votes-query solution-id vote-type)))

(defn solution-tuple->map
  "Convert query result tuple to solution map.
   
   Takes a tuple from the query result and returns a properly formatted
   solution map with string ID (for frontend compatibility)."
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

(defn sort-solutions-by-created-at
  "Sort solutions by created-at timestamp, newest first."
  [solutions]
  (sort-by :created-at #(compare %2 %1) solutions))

(def solution-votes-query
  "Build query to find all vote entities for a given solution.
   
   Returns Datalog query that finds vote entity IDs."
  '[:find [?vote ...] :in $ ?solution-id :where [?vote :aoc-vote/solution-id ?solution-id]])

(defn solution-query
  [{:keys [db]} solution-uid]
  (db/pull-entity db '[*] [:aoc-solution/id solution-uid]))


(defn delete-solution-tx
  [{:keys [db]} solution-uid]
  (let [vote-eids (db/query db solution-votes-query solution-uid)
        delete-votes-tx (when (seq vote-eids) (mapv (fn [eid] [:db/retractEntity eid]) vote-eids))
        delete-solution-tx [[:db/retractEntity [:aoc-solution/id solution-uid]]]]
    (if delete-votes-tx (concat delete-votes-tx delete-solution-tx) delete-solution-tx)))
