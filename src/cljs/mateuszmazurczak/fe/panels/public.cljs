(ns mateuszmazurczak.fe.panels.public
  "Describes the link between panel names and contents"
  (:require
   [automaton-web.components.errors    :as web-comp-errors]
   [mateuszmazurczak.fe.history        :as mateuszmazurczak-fe-history]
   [mateuszmazurczak.fe.panels         :as mateuszmazurczak-fe-panels]
   [mateuszmazurczak.i18n.fe.translate :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes            :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.home           :as mateuszmazurczak-home]
   [mateuszmazurczak.ui.structure      :as mateuszmazurczak-ui-structure]))

(defmethod mateuszmazurczak-fe-panels/panels :panels/home
  []
  [mateuszmazurczak-ui-structure/mateuszmazurczak-page-structure
   [mateuszmazurczak-home/home]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/not-found
  []
  (web-comp-errors/not-found
   {:title (mateuszmazurczak-fe-translate/tr :not-found-page)
    :description (mateuszmazurczak-fe-translate/tr :not-found-description)
    :back-home-text (mateuszmazurczak-fe-translate/tr :back-home)
    :back-link (mateuszmazurczak-fe-history/href
                ::mateuszmazurczak-routes/home)}))
