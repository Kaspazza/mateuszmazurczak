(ns mateuszmazurczak.fe.panels
  "Describe panels dispatch mechanism for the mateuszmazurczak frontend"
  (:require
   [automaton-core.log                 :as core-log]
   [automaton-web.components.errors    :as web-comp-errors]
   [automaton-web.components.spinner   :as web-spinner]
   [mateuszmazurczak.fe.router         :as mateuszmazurczak-fe-router]
   [mateuszmazurczak.i18n.fe.translate :as mateuszmazurczak-fe-translate]))

(defn- match-to-panel-id
  "Transform a match coming from routing into a panel id that will be displayed
  Params:
  * `match`"
  [match]
  (cond
    (nil? match) (let [default-panel-id :panels/not-found]
                   ;; This should not happen, but it does during dev,
                   ;; it is preferrable to display a page and a message
                   ;; than displaying a white page
                   (core-log/warn "Render panel default to `"
                                  default-panel-id
                                  "`, as match `"
                                  match
                                  "` was empty")
                   default-panel-id)
    (= match :pending) :panels/pending
    :else (let [panel-id (mateuszmazurczak-fe-router/panel-id match)]
            (core-log/info "Render panel `" panel-id "`")
            panel-id)))

(defmulti panels match-to-panel-id)

(defmethod panels :default
  []
  (web-comp-errors/not-found
   {:title (mateuszmazurczak-fe-translate/tr :not-found-page)
    :description (mateuszmazurczak-fe-translate/tr :not-found-description)
    :back-home-text (mateuszmazurczak-fe-translate/tr :back-home)}))

(defmethod panels :panels/pending [] [:div [web-spinner/spinner]])
