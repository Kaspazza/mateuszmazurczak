(ns mateuszmazurczak.navigation.panels
  "Describes the link between panel names and contents"
  (:require
   [mateuszmazurczak.i18n.translate    :as mm-i18n-translate]
   [mateuszmazurczak.navigation.core   :as navigation]
   [mateuszmazurczak.navigation.routes :as mm-routes]
   [mateuszmazurczak.ui.errors         :as mm-ui-errors]
   [mateuszmazurczak.ui.home           :as mm-home]
   [mateuszmazurczak.ui.navigation     :as mm-ui-navigation]
   [mateuszmazurczak.ui.spinner        :as mm-ui-spinner]
   [mateuszmazurczak.ui.structure      :as mm-ui-structure]))

(defmulti panels identity)

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
    (mm-ui-navigation/navigation {:href (navigation/href ::mm-routes/home)
                                  :text "Back home"
                                  :dark? true})]])
