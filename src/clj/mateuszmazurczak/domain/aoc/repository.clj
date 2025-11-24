(ns mateuszmazurczak.domain.aoc.repository
  "Domain logic for AOC solution repository operations.
   
   Pure functions for building queries and transaction data.
   Actual database operations delegated to ports/database."
  (:require
   [clojure.string :as str])
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
   
   Returns Datalevin transaction data (vector of maps).
   Generates UUID and timestamp automatically."
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
                 :aoc-solution/created-at now}]
    [(if (and github-profile (string? github-profile) (not (str/blank? github-profile)))
       (assoc base-tx :aoc-solution/github-profile github-profile)
       base-tx)]))

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
  [{:aoc-solution/keys
    [id year challenge part author-name github-profile content-type content created-at]}]
  (cond-> {:id (str id)
           :year year
           :challenge challenge
           :part part
           :author-name author-name
           :content-type content-type
           :content content
           :created-at (str created-at)}
    github-profile (assoc :github-profile github-profile)))

(defn sort-solutions-by-created-at
  "Sort solutions by created-at timestamp, newest first."
  [solutions]
  (sort-by :created-at #(compare %2 %1) solutions))
