#!/usr/bin/env clojure
(require '[datalevin.core :as d])

(def db-path (or (first *command-line-args*) "/home/db"))

(println "Checking database at:" db-path)
(println "==========================================")

(try
  (def conn (d/get-conn db-path))
  (def db @conn)
  
  ;; Count all entities
  (def all-entities 
    (d/q '[:find (count ?e)
           :where [?e _ _]]
         db))
  
  (println "Total entities:" (ffirst all-entities))
  
  ;; Count AOC solutions
  (def aoc-solutions
    (d/q '[:find (count ?e)
           :where [?e :aoc-solution/id _]]
         db))
  
  (println "AOC solutions:" (ffirst aoc-solutions))
  
  ;; List first 5 solutions with details
  (def solutions
    (d/q '[:find ?id ?year ?challenge ?author
           :where 
           [?e :aoc-solution/id ?id]
           [?e :aoc-solution/year ?year]
           [?e :aoc-solution/challenge ?challenge]
           [?e :aoc-solution/author-name ?author]]
         db))
  
  (println "\nFirst 5 solutions:")
  (doseq [[id year challenge author] (take 5 solutions)]
    (println (format "  %s - %d/%d by %s" id year challenge author)))
  
  (d/close conn)
  (println "\n✓ Done")
  
  (catch Exception e
    (println "✗ Error:" (.getMessage e))
    (.printStackTrace e)))
