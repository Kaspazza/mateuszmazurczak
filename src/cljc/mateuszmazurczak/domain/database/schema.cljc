(ns mateuszmazurczak.domain.database.schema
  (:refer-clojure :exclude [comment])
  (:require
   [mateuszmazurczak.domain.database.migrations :as migrations]))

(def article
  {:article/id {:doc "Unique identificator for the article which main SOT is articles.edn"
                :attr :keyword
                :unique :identity}})

(def author
  {:author/id {:doc "Unique ID for the author"
               :attr :uuid
               :unique :identity}
   :author/name {:doc "Display name for the author"
                 :attr :string}
   :author/type {:doc "Either :owner or :guest"
                 :enum #{:owner :guest}}})

(def comment
  {:comment/id {:doc "Unique ID for the comment"
                :attr :uuid
                :unique :identity}
   :comment/article {:doc "Reference to the article this comment belongs to"
                     :ref :article/id}
   :comment/parent {:doc "Optional reference to parent comment"
                    :ref :comment/id}
   :comment/author {:doc "Reference to the author entity"
                    :ref :author/id}
   :comment/content {:doc "The comment text"
                     :attr :string}
   :comment/created-at {:doc "Timestamp when the comment was created"
                        :attr :instant}})

(def aoc-solution
  {:aoc-solution/id {:doc "Unique ID for the AOC solution"
                     :attr :uuid
                     :unique :identity}
   :aoc-solution/year {:doc "AOC year (2015-2025)"
                       :attr :int
                       :index true}
   :aoc-solution/challenge {:doc "Challenge day (1-24)"
                            :attr :int
                            :index true}
   :aoc-solution/part {:doc "Challenge part (1 or 2)"
                       :attr :int
                       :index true}
   :aoc-solution/author-name {:doc "Name of the solution author"
                              :attr :string}
   :aoc-solution/github-profile {:doc "Optional GitHub profile URL"
                                 :attr :string}
   :aoc-solution/content-type {:doc "Type of content: :code-snippet or :repo-link"
                               :enum #{:code-snippet :repo-link}}
   :aoc-solution/content {:doc
                          "The solution content (code or URL). Can be large text for code snippets."
                          :attr :string}
   :aoc-solution/created-at {:doc "Timestamp when the solution was submitted"
                             :attr :instant
                             :index true}
   :aoc-solution/best-practices-count {:doc "Number of best practices votes"
                                       :attr :int}
   :aoc-solution/clever-count {:doc "Number of clever votes"
                               :attr :int}})

(def entities [article author comment aoc-solution migrations/migration-schema])
