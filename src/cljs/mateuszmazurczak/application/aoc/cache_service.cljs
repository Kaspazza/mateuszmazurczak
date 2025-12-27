(ns mateuszmazurczak.application.aoc.cache-service
  "AoC-specific cache operations.
   
   Application service that orchestrates cache port + domain logic.
   Provides high-level operations for AoC vote tracking and consent management."
  (:require
   [mateuszmazurczak.domain.aoc.vote       :as vote]
   [mateuszmazurczak.domain.cache.registry :as cache-registry]
   [mateuszmazurczak.ports.cache           :as cache]))

;; =============================================================================
;; Vote Tracking
;; =============================================================================

(defn get-votes
  "Get all votes from cache.
   
   Returns a set of vote keys (\"solution-id::vote-type\")."
  []
  (or (cache/get-item (get-in cache-registry/registry [:user-data :domains :aoc-votes :key])) #{}))

(defn add-vote!
  "Record a vote in cache.
   
   Args:
   - solution-id: Solution ID string
   - vote-type: :best-practices or :clever
   
   Returns true if successful."
  [solution-id vote-type]
  (let [votes (get-votes)
        updated-votes (vote/add-vote votes solution-id vote-type)]
    (cache/set-item! (get-in cache-registry/registry [:user-data :domains :aoc-votes :key])
                     updated-votes)))

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
  "Create a composite key for year/challenge.
   
   Format: year::challenge
   Example: \"2024::1\""
  [year challenge]
  (str year "::" challenge))

(defn get-consents
  "Get all consents from cache.
   
   Returns a set of consent keys (\"year::challenge\")."
  []
  (or (cache/get-item (get-in cache-registry/registry [:user-data :domains :aoc-consents :key]))
      #{}))

(defn add-consent!
  "Record user consent (\"I've solved it\") in cache.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   
   Returns true if successful."
  [year challenge]
  (let [consents (get-consents)
        updated-consents (conj consents (make-challenge-key year challenge))]
    (cache/set-item! (get-in cache-registry/registry [:user-data :domains :aoc-consents :key])
                     updated-consents)))

(defn has-consented?
  "Check if user has given consent for year/challenge.
   
   Args:
   - year: Year number
   - challenge: Challenge number"
  [year challenge]
  (contains? (get-consents) (make-challenge-key year challenge)))

;; =============================================================================
;; Solution ID Tracking 
;; =============================================================================

(defn get-solution-ids
  "Get all user solution IDs from cache.
   
   Returns a map of challenge-key -> vector of solution-ids."
  []
  (or (cache/get-item (get-in cache-registry/registry [:user-data :domains :aoc-solution-ids :key]))
      {}))

(defn add-solution-id!
  "Record a solution ID in cache.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   - solution-id: Solution ID string
   
   Returns true if successful."
  [year challenge solution-id]
  (let [solution-ids (get-solution-ids)
        challenge-key (make-challenge-key year challenge)
        current-ids (get solution-ids challenge-key [])
        updated-ids (conj current-ids solution-id)
        updated-map (assoc solution-ids challenge-key updated-ids)]
    (cache/set-item! (get-in cache-registry/registry [:user-data :domains :aoc-solution-ids :key])
                     updated-map)))

(defn get-user-solution-ids
  "Get user's solution IDs for year/challenge.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   
   Returns vector of solution-id strings or empty vector."
  [year challenge]
  (get (get-solution-ids) (make-challenge-key year challenge) []))

(defn get-upload-count
  "Get number of solutions user has uploaded for year/challenge.
   
   Args:
   - year: Year number
   - challenge: Challenge number
   
   Returns count of user's solutions."
  [year challenge]
  (count (get-user-solution-ids year challenge)))

(defn can-upload?
  "Check if user can upload another solution (max 5 per challenge).
   
   Args:
   - year: Year number
   - challenge: Challenge number"
  [year challenge]
  (< (get-upload-count year challenge) 5))

;; =============================================================================
;; Admin Key Management
;; =============================================================================

(defn set-admin-key!
  "Store admin key in localStorage.
   
   Args:
   - admin-key: The admin API key string"
  [admin-key]
  (cache/set-item! (get-in cache-registry/registry [:user-data :domains :admin-key :key])
                   admin-key))

(defn get-admin-key
  "Get admin key from localStorage.
   
   Returns admin key string or nil."
  []
  (cache/get-item (get-in cache-registry/registry [:user-data :domains :admin-key :key])))

(defn clear-admin-key!
  "Remove admin key from localStorage (logout)."
  []
  (cache/remove-item! (get-in cache-registry/registry [:user-data :domains :admin-key :key])))

(defn is-admin?
  "Check if user has admin key stored.
   
   Returns true if admin key is present in localStorage."
  []
  (some? (get-admin-key)))
