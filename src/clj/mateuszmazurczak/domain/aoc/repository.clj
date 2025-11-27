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
   [:part [:enum 1 2]]
   [:author-name [:string {:min 1}]]
   [:github-profile {:optional true}
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
   - :part (int)
   - :author-name (string)
   - :github-profile (optional string)
   - :content-type (string or keyword: code-snippet or repo-link)
   - :content (string)
   
   Returns tuple of [solution-id tx-data] where:
   - solution-id is the generated UUID
   - tx-data is Datalevin transaction data (vector of maps)
   
   Generates UUID and timestamp automatically.
   Vote counts are initialized to 0."
  [{:keys [year challenge part author-name github-profile content-type content]}]
  (let [solution-id (UUID/randomUUID)
        now (Date.)
        normalized-content-type (normalize-content-type content-type)
        base-tx {:aoc-solution/id solution-id
                 :aoc-solution/year year
                 :aoc-solution/challenge challenge
                 :aoc-solution/part part
                 :aoc-solution/author-name author-name
                 :aoc-solution/content-type normalized-content-type
                 :aoc-solution/content content
                 :aoc-solution/created-at now
                 :aoc-solution/best-practices-count 0
                 :aoc-solution/clever-count 0}
        tx (if (and github-profile (string? github-profile) (not (str/blank? github-profile)))
             (assoc base-tx :aoc-solution/github-profile github-profile)
             base-tx)]
    [solution-id [tx]]))

(defn build-get-solutions-query
  "Build Datalog query for fetching solutions by year, challenge, and part.
   
   Returns a Datalog query that finds all solutions matching the criteria,
   sorted by creation date (newest first)."
  []
  '[:find
    [(pull ?e [*]) ...]
    :in
    $
    ?year
    ?challenge
    ?part
    :where
    [?e :aoc-solution/year ?year]
    [?e :aoc-solution/challenge ?challenge]
    [?e :aoc-solution/part ?part]])

(defn solution-tuple->map
  "Convert query result tuple to solution map.
   
   Takes a tuple from the query result and returns a properly formatted
   solution map with string ID (for frontend compatibility)."
  [{:aoc-solution/keys [id
                        year
                        challenge
                        part
                        author-name
                        github-profile
                        content-type
                        content
                        created-at
                        best-practices-count
                        clever-count]}]
  (cond-> {:id (str id)
           :year year
           :challenge challenge
           :part part
           :author-name author-name
           :content-type content-type
           :content content
           :created-at (str created-at)
           :best-practices-count (or best-practices-count 0)
           :clever-count (or clever-count 0)}
    github-profile (assoc :github-profile github-profile)))

(defn sort-solutions-by-created-at
  "Sort solutions by created-at timestamp, newest first."
  [solutions]
  (sort-by :created-at #(compare %2 %1) solutions))

(defn vote-type->attribute
  "Map vote type to its corresponding database attribute.
   
   Takes:
   - vote-type: :best-practices or :clever
   
   Returns the corresponding database attribute keyword."
  [vote-type]
  (case vote-type
    :best-practices :aoc-solution/best-practices-count
    :clever :aoc-solution/clever-count))

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
