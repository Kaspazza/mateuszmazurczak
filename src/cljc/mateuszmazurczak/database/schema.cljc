(ns mateuszmazurczak.database.schema 
  (:refer-clojure :exclude [comment])
  (:require [mateuszmazurczak.database.migrations :as migrations]))

(def article
  {:article/id
   {:doc "Unique identificator for the article which main SOT is articles.edn"
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

(def entities [article author comment migrations/migration-schema])
