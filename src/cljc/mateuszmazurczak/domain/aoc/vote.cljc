(ns mateuszmazurczak.domain.aoc.vote
  "Domain logic for AOC solution voting and tracking.
   
   Pure functions for vote validation, tracking, and rate limiting."
  #?(:clj (:import [java.util Date UUID])))

;; =============================================================================
;; Vote Tracking Schema
;; =============================================================================

(def VoteType "Vote types for solutions." [:enum :best-practices :clever])

(def VoteRecord
  "Record of a vote for a solution."
  [:map [:solution-id :string] [:vote-type VoteType] #?@(:clj [[:voted-at :inst]])])

;; =============================================================================
;; Vote Validation
;; =============================================================================

(defn valid-vote-type?
  "Check if vote type is valid."
  [vote-type]
  (contains? #{:best-practices :clever} vote-type))

(defn normalize-vote-type
  "Normalize vote type from string or keyword to keyword."
  [vote-type]
  (if (keyword? vote-type) vote-type (keyword vote-type)))

;; =============================================================================
;; Vote Tracking (Frontend - localStorage)
;; =============================================================================

(defn make-vote-key
  "Create a composite key for vote tracking.
   
   Format: solution-id::vote-type
   Example: \"abc-123::best-practices\""
  [solution-id vote-type]
  (str solution-id "::" (name vote-type)))

(defn parse-vote-key
  "Parse a composite vote key into solution-id and vote-type.
   
   Returns map with :solution-id and :vote-type, or nil if invalid."
  [vote-key]
  (when-let [[solution-id vote-type-str] (and vote-key (re-matches #"(.+)::(.+)" vote-key))]
    {:solution-id solution-id
     :vote-type (keyword vote-type-str)}))

(defn add-vote
  "Add a vote to the votes set.
   
   votes: Set of vote keys (\"solution-id::vote-type\")
   solution-id: Solution ID string
   vote-type: :best-practices or :clever
   
   Returns updated votes set."
  [votes solution-id vote-type]
  (conj (or votes #{}) (make-vote-key solution-id vote-type)))

(defn has-voted?
  "Check if user has already voted for this solution with this vote type.
   
   votes: Set of vote keys
   solution-id: Solution ID string
   vote-type: :best-practices or :clever"
  [votes solution-id vote-type]
  (contains? votes (make-vote-key solution-id vote-type)))

;; =============================================================================
;; Database Vote Tracking (Backend)
;; =============================================================================

#?(:clj
     (defn save-vote-tx
       "Build transaction data for saving a vote record.
      
      Args:
      - solution-id: UUID string of the solution
      - vote-type: :best-practices or :clever"
       [solution-id vote-type]
       (let [vote-id (UUID/randomUUID)
             now (Date.)
             solution-uuid (UUID/fromString solution-id)]
         [{:aoc-vote/id vote-id
           :aoc-vote/solution-id solution-uuid
           :aoc-vote/vote-type vote-type
           :aoc-vote/voted-at now}])))




