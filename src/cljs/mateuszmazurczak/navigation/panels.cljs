(ns mateuszmazurczak.navigation.panels
  "Describes the link between panel names and contents"
  (:require
   [mateuszmazurczak.i18n.translate     :as mm-i18n-translate]
   [mateuszmazurczak.navigation.history :as mm-nav-hist]
   [mateuszmazurczak.navigation.router  :as mm-nav-router]
   [mateuszmazurczak.navigation.routes  :as mm-routes]
   [mateuszmazurczak.ui.errors          :as mm-ui-errors]
   [mateuszmazurczak.ui.home            :as mm-home]
   [mateuszmazurczak.ui.navigation      :as mm-ui-navigation]
   [mateuszmazurczak.ui.spinner         :as mm-ui-spinner]
   [mateuszmazurczak.ui.structure       :as mm-ui-structure]))


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
                   default-panel-id)
    (= match :pending) :panels/pending
    :else (let [panel-id (mm-nav-router/panel-id match)] panel-id)))

(defmulti panels match-to-panel-id)

(defmethod panels :default
  []
  (mm-ui-errors/not-found {:title (mm-i18n-translate/tr :not-found-page)
                           :description (mm-i18n-translate/tr
                                         :not-found-description)
                           :back-home-text (mm-i18n-translate/tr :back-home)}))

(defmethod panels :panels/pending [] [:div [mm-ui-spinner/spinner]])

(defmethod panels :panels/home
  []
  [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home]])

(defmethod panels :panels/articles
  []
  [mm-ui-structure/mateuszmazurczak-page-structure
   [:div {:class ["mt-12"]}
    (mm-ui-navigation/navigation {:href (mm-nav-hist/href-delta
                                         ::mm-routes/home)
                                  :text "Back home"
                                  :dark? true})]])
