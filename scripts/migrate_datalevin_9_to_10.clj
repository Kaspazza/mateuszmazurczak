#!/usr/bin/env bb
(ns migrate-datalevin-9-to-10
  "Migration script to copy data from Datalevin 9.x to 10.x.
   
   Datalevin 10.x has breaking changes in the underlying storage format.
   This script reads all entities from the old DB and writes them to the new DB.
   
   Usage:
     bb scripts/migrate_datalevin_9_to_10.clj
   
   Or with custom paths:
     bb scripts/migrate_datalevin_9_to_10.clj /home/db /home/mateusz-data
   
   Prerequisites:
   - Old DB must be at /home/db (or first arg)
   - New DB must be at /home/mateusz-data (or second arg)
   - New DB should be empty or freshly initialized
   - Application should be stopped during migration"
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint]
   [datalevin.core :as d]))

;; =============================================================================
;; Schema Definition (from your application)
;; =============================================================================

(def schema
  "Datalevin schema definition for AoC solutions.
   This must match your current application schema."
  {:aoc-solution/id {:db/valueType :db.type/uuid
                     :db/unique :db.unique/identity
                     :db/doc "Unique solution identifier"}
   :aoc-solution/year {:db/valueType :db.type/long
                       :db/index true
                       :db/doc "AoC year (e.g., 2024)"}
   :aoc-solution/challenge {:db/valueType :db.type/long
                            :db/index true
                            :db/doc "Challenge day number (1-25)"}
   :aoc-solution/author-name {:db/valueType :db.type/string
                              :db/doc "Solution author's display name"}
   :aoc-solution/github-username {:db/valueType :db.type/string
                                  :db/doc "GitHub username (parsed from URL or direct input)"}
   :aoc-solution/content-type {:db/valueType :db.type/keyword
                               :db/doc "Type of content (:code-snippet or :repo-link)"}
   :aoc-solution/content {:db/valueType :db.type/string
                          :db/doc "Solution content (code or repo URL)"}
   :aoc-solution/created-at {:db/valueType :db.type/instant
                             :db/index true
                             :db/doc "Timestamp when solution was created"}
   :aoc-solution/best-practices-count {:db/valueType :db.type/long
                                       :db/doc "Number of best practices votes"}
   :aoc-solution/clever-count {:db/valueType :db.type/long
                               :db/doc "Number of clever votes"}
   ;; Migration tracking schema
   :migration/id {:db/valueType :db.type/string
                  :db/unique :db.unique/identity
                  :db/doc "Unique migration identifier"}
   :migration/applied-at {:db/valueType :db.type/instant
                          :db/doc "Timestamp when migration was applied"}
   :migration/checksum {:db/valueType :db.type/string
                        :db/doc "MD5 hash of migration content"}})

;; =============================================================================
;; Helper Functions
;; =============================================================================

(defn print-step [step-num total-steps message]
  (println (format "[%d/%d] %s" step-num total-steps message)))

(defn print-success [message]
  (println (str "✓ " message)))

(defn print-error [message]
  (println (str "✗ " message)))

(defn print-warning [message]
  (println (str "⚠ " message)))

(defn entity-to-map
  "Convert a Datalevin entity to a plain map, excluding internal attrs."
  [entity]
  (when entity
    (into {}
          (remove (fn [[k _v]]
                    (or (= k :db/id)
                        (nil? k)))
                  entity))))

(defn pull-all-entities
  "Pull all entities from a database, grouped by entity type."
  [db]
  (let [;; Get all entity IDs
        all-eids (d/q '[:find [?e ...]
                        :where [?e _ _]]
                      db)
        ;; Pull full entities
        entities (map #(d/pull db '[*] %) all-eids)
        ;; Convert to plain maps
        entity-maps (map entity-to-map entities)
        ;; Filter out empty/internal entities
        valid-entities (remove empty? entity-maps)]
    valid-entities))

(defn group-entities-by-type
  "Group entities by their primary attribute namespace."
  [entities]
  (group-by (fn [entity]
              (let [keys (keys entity)
                    namespaces (map namespace (filter namespace keys))]
                (first (filter #{"aoc-solution" "migration"} namespaces))))
            entities))

(defn backup-to-edn
  "Backup entities to EDN file as safety measure."
  [entities backup-path]
  (with-open [w (io/writer backup-path)]
    (binding [*out* w]
      (pprint/pprint entities))))

(defn read-backup-from-edn
  "Read entities from EDN backup file."
  [backup-path]
  (edn/read-string (slurp backup-path)))

;; =============================================================================
;; Migration Functions
;; =============================================================================

(defn validate-paths
  "Validate that source and destination paths exist and are valid."
  [source-path dest-path]
  (let [source-dir (io/file source-path)
        dest-dir (io/file dest-path)]
    (cond
      (not (.exists source-dir))
      (throw (ex-info "Source DB directory does not exist"
                      {:source-path source-path}))
      
      (not (.isDirectory source-dir))
      (throw (ex-info "Source path is not a directory"
                      {:source-path source-path}))
      
      (not (.exists dest-dir))
      (throw (ex-info "Destination DB directory does not exist"
                      {:dest-path dest-path}))
      
      (not (.isDirectory dest-dir))
      (throw (ex-info "Destination path is not a directory"
                      {:dest-path dest-path}))
      
      :else true)))

(defn migrate-datalevin-9-to-10
  "Main migration function.
   
   Steps:
   1. Validate paths
   2. Open both databases
   3. Read all entities from old DB
   4. Backup to EDN
   5. Write entities to new DB
   6. Verify migration"
  [source-path dest-path]
  (println "\n╔════════════════════════════════════════════════════════════╗")
  (println "║  Datalevin 9.x → 10.x Migration Script                    ║")
  (println "╚════════════════════════════════════════════════════════════╝\n")
  
  (let [total-steps 7
        backup-file "datalevin-backup.edn"]
    
    ;; Step 1: Validate paths
    (print-step 1 total-steps "Validating paths...")
    (try
      (validate-paths source-path dest-path)
      (print-success (format "Source: %s" source-path))
      (print-success (format "Destination: %s" dest-path))
      (catch Exception e
        (print-error (.getMessage e))
        (System/exit 1)))
    
    ;; Step 2: Connect to source DB (Datalevin 9.x)
    (print-step 2 total-steps "Connecting to source DB (Datalevin 9.x)...")
    (def source-conn
      (try
        (d/get-conn source-path schema)
        (catch Exception e
          (print-error "Failed to connect to source DB")
          (println "Error:" (.getMessage e))
          (print-warning "The source DB might be corrupted or using incompatible format")
          (System/exit 1))))
    (print-success "Connected to source DB")
    
    ;; Step 3: Read all entities from source
    (print-step 3 total-steps "Reading entities from source DB...")
    (def source-entities
      (try
        (pull-all-entities @source-conn)
        (catch Exception e
          (print-error "Failed to read entities from source DB")
          (println "Error:" (.getMessage e))
          (d/close source-conn)
          (System/exit 1))))
    
    (def grouped-entities (group-entities-by-type source-entities))
    (print-success (format "Found %d total entities:" (count source-entities)))
    (doseq [[entity-type entities] grouped-entities]
      (when entity-type
        (println (format "  - %d %s entities" (count entities) entity-type))))
    
    ;; Step 4: Backup to EDN
    (print-step 4 total-steps (format "Creating backup at %s..." backup-file))
    (try
      (backup-to-edn source-entities backup-file)
      (print-success (format "Backup saved to %s" backup-file))
      (catch Exception e
        (print-error "Failed to create backup")
        (println "Error:" (.getMessage e))
        (d/close source-conn)
        (System/exit 1)))
    
    ;; Close source connection
    (d/close source-conn)
    (print-success "Closed source DB connection")
    
    ;; Step 5: Connect to destination DB (Datalevin 10.x)
    (print-step 5 total-steps "Connecting to destination DB (Datalevin 10.x)...")
    (def dest-conn
      (try
        (d/get-conn dest-path schema)
        (catch Exception e
          (print-error "Failed to connect to destination DB")
          (println "Error:" (.getMessage e))
          (System/exit 1))))
    (print-success "Connected to destination DB")
    
    ;; Step 6: Write entities to destination
    (print-step 6 total-steps "Writing entities to destination DB...")
    (try
      (when (seq source-entities)
        (d/transact! dest-conn source-entities)
        (print-success (format "Wrote %d entities to destination DB" (count source-entities))))
      (catch Exception e
        (print-error "Failed to write entities to destination DB")
        (println "Error:" (.getMessage e))
        (print-warning (format "You can restore from backup: %s" backup-file))
        (d/close dest-conn)
        (System/exit 1)))
    
    ;; Step 7: Verify migration
    (print-step 7 total-steps "Verifying migration...")
    (def dest-entities (pull-all-entities @dest-conn))
    (def dest-grouped (group-entities-by-type dest-entities))
    
    (if (= (count source-entities) (count dest-entities))
      (do
        (print-success (format "Verified: %d entities migrated successfully" (count dest-entities)))
        (doseq [[entity-type entities] dest-grouped]
          (when entity-type
            (println (format "  - %d %s entities" (count entities) entity-type)))))
      (do
        (print-warning "Entity count mismatch!")
        (println (format "  Source: %d entities" (count source-entities)))
        (println (format "  Destination: %d entities" (count dest-entities)))))
    
    ;; Close destination connection
    (d/close dest-conn)
    (print-success "Closed destination DB connection")
    
    ;; Final summary
    (println "\n╔════════════════════════════════════════════════════════════╗")
    (println "║  Migration Complete!                                      ║")
    (println "╚════════════════════════════════════════════════════════════╝\n")
    (println "Next steps:")
    (println "1. Review the backup file:" backup-file)
    (println "2. Test your application with the new DB")
    (println "3. Clear frontend cache version (see instructions below)")
    (println "\nTo force cache clear for users, increment user-data-version in:")
    (println "  src/cljc/mateuszmazurczak/domain/cache/registry.cljc")
    (println "  Change: (let [user-data-version 2] ...")
    (println "  To:     (let [user-data-version 3] ...)")
    (println)))

;; =============================================================================
;; Main Entry Point
;; =============================================================================

(defn -main [& args]
  (let [source-path (or (first args) "/home/db")
        dest-path (or (second args) "/home/mateusz-data")]
    (try
      (migrate-datalevin-9-to-10 source-path dest-path)
      (System/exit 0)
      (catch Exception e
        (print-error "Migration failed!")
        (println "Error:" (.getMessage e))
        (.printStackTrace e)
        (System/exit 1)))))

;; Run if executed as script
(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
