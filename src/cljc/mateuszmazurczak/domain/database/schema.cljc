(ns mateuszmazurczak.domain.database.schema
  (:refer-clojure :exclude [comment]))

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
   :aoc-solution/author-name {:doc "Name of the solution author"
                              :attr :string}
   :aoc-solution/github-username {:doc "Optional GitHub username (without @ or URL)"
                                  :attr :string}
   :aoc-solution/content-type {:doc "Type of content: :code-snippet or :repo-link"
                               :enum #{:code-snippet :repo-link}}
   :aoc-solution/content {:doc
                          "The solution content (code or URL). Can be large text for code snippets."
                          :attr :string}
   :aoc-solution/created-at {:doc "Timestamp when the solution was submitted"
                             :attr :instant
                             :index true}})

(def aoc-vote
  {:aoc-vote/id {:doc "Unique ID for the vote"
                 :attr :uuid
                 :unique :identity}
   :aoc-vote/solution-id {:doc "Reference to the solution being voted on"
                          :ref :aoc-solution/id}
   :aoc-vote/vote-type {:doc "Type of vote: :best-practices or :clever"
                        :enum #{:best-practices :clever}}
   :aoc-vote/voted-at {:doc "Timestamp when the vote was cast"
                       :attr :instant
                       :index true}})

(def migration-schema
  "Schema for tracking applied migrations in the database"
  {:migration/id {:doc "Unique migration identifier (timestamp + description)"
                  :attr :string
                  :unique :identity}
   :migration/applied-at {:doc "Timestamp when migration was applied"
                          :attr :instant}
   :migration/checksum {:doc "MD5 hash of migration content for integrity check"
                        :attr :string}})

(def entities [article author comment aoc-solution aoc-vote migration-schema])
