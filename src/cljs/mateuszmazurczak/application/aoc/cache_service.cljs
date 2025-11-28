(ns mateuszmazurczak.application.aoc.cache-service
  "AoC-specific cache operations.
   
   Application service that orchestrates cache port + domain logic.
   Provides high-level operations for AoC vote tracking and consent management."
  (:require
   [mateuszmazurczak.domain.aoc.vote :as vote]
   [mateuszmazurczak.ports.cache     :as cache]))

;; =============================================================================
;; Cache Keys
;; =============================================================================

;;TODO connect name to cache registry in some way
(def ^:private aoc-votes-key "aoc-votes")
(def ^:private aoc-consents-key "aoc-consents")
(def ^:private aoc-solution-ids-key "aoc-solution-ids")
(def ^:private admin-key-storage-key "admin-key")

;; =============================================================================
;; Vote Tracking
;; =============================================================================

(defn get-votes
  "Get all votes from cache.
   
   Returns a set of vote keys (\"solution-id::vote-type\")."
  []
  (or (cache/get-item aoc-votes-key) #{}))

(defn add-vote!
  "Record a vote in cache.
   
   Args:
   - solution-id: Solution ID string
   - vote-type: :best-practices or :clever
   
   Returns true if successful."
  [solution-id vote-type]
  (let [votes (get-votes)
        updated-votes (vote/add-vote votes solution-id vote-type)]
    (cache/set-item! aoc-votes-key updated-votes)))

(defn has-voted?
  "Check if user has voted for a solution.
   
   Args:
   - solution-id: Solution ID string
   - vote-type: :best-practices or :clever"
  [solution-id vote-type]
  (vote/has-voted? (get-votes) solution-id vote-type))

;; =============================================================================
;; Consent Tracking 
;; =============================================================================

(defn make-challenge-key
  "Create a composite key for year/challenge/part.
   
   Format: year::challenge::part
   Example: \"2024::1::1\""
  [year challenge part]
  (str year "::" challenge "::" part))

(defn get-consents
  "Get all consents from cache.
   
   Returns a set of consent keys (\"year::challenge::part\")."
  []
  (or (cache/get-item aoc-consents-key) #{}))

(defn add-consent!
  "Record user consent (\"I've solved it\") in cache.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number
   
   Returns true if successful."
  [year challenge part]
  (let [consents (get-consents)
        updated-consents (conj consents (make-challenge-key year challenge part))]
    (cache/set-item! aoc-consents-key updated-consents)))

(defn has-consented?
  "Check if user has given consent for year/challenge/part.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number"
  [year challenge part]
  (contains? (get-consents) (make-challenge-key year challenge part)))

;; =============================================================================
;; Solution ID Tracking 
;; =============================================================================

(defn get-solution-ids
  "Get all user solution IDs from cache.
   
   Returns a map of challenge-key -> vector of solution-ids."
  []
  (or (cache/get-item aoc-solution-ids-key) {}))

(defn add-solution-id!
  "Record a solution ID in cache.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number
   - solution-id: Solution ID string
   
   Returns true if successful."
  [year challenge part solution-id]
  (let [solution-ids (get-solution-ids)
        challenge-key (make-challenge-key year challenge part)
        current-ids (get solution-ids challenge-key [])
        updated-ids (conj current-ids solution-id)
        updated-map (assoc solution-ids challenge-key updated-ids)]
    (cache/set-item! aoc-solution-ids-key updated-map)))

(defn get-user-solution-ids
  "Get user's solution IDs for year/challenge/part.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number
   
   Returns vector of solution-id strings or empty vector."
  [year challenge part]
  (get (get-solution-ids) (make-challenge-key year challenge part) []))

(defn get-upload-count
  "Get number of solutions user has uploaded for year/challenge/part.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number
   
   Returns count of user's solutions."
  [year challenge part]
  (count (get-user-solution-ids year challenge part)))

(defn can-upload?
  "Check if user can upload another solution (max 5 per challenge).
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - part: Part number"
  [year challenge part]
  (< (get-upload-count year challenge part) 5))

;; =============================================================================
;; Admin Key Management
;; =============================================================================

(defn set-admin-key!
  "Store admin key in localStorage.
   
   Args:
   - admin-key: The admin API key string"
  [admin-key]
  (cache/set-item! admin-key-storage-key admin-key))

(defn get-admin-key
  "Get admin key from localStorage.
   
   Returns admin key string or nil."
  []
  (cache/get-item admin-key-storage-key))

(defn clear-admin-key!
  "Remove admin key from localStorage (logout)."
  []
  (cache/remove-item! admin-key-storage-key))

(defn is-admin?
  "Check if user has admin key stored.
   
   Returns true if admin key is present in localStorage."
  []
  (some? (get-admin-key)))
