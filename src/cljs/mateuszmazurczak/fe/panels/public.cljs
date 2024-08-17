(ns mateuszmazurczak.fe.panels.public
  "Describes the link between panel names and contents"
  (:require
   [automaton-web.components.errors :as web-comp-errors]
   [mateuszmazurczak.fe.history              :as mateuszmazurczak-fe-history]
   [mateuszmazurczak.fe.panels               :as mateuszmazurczak-fe-panels]
   [mateuszmazurczak.i18n.fe.translate       :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes                  :as mateuszmazurczak-routes]
   [mateuszmazurczak.simulation-toy-example  :as mateuszmazurczak-toy-example]
   [mateuszmazurczak.ui.additional-outcomes  :as mateuszmazurczak-ui-additional-outcomes]
   [mateuszmazurczak.ui.brand                :as mateuszmazurczak-ui-brand]
   [mateuszmazurczak.ui.commitment           :as mateuszmazurczak-ui-commitment]
   [mateuszmazurczak.ui.home                 :as mateuszmazurczak-home]
   [mateuszmazurczak.ui.legal                :as mateuszmazurczak-ui-legal]
   [mateuszmazurczak.ui.simulation           :as mateuszmazurczak-ui-simulation]
   [mateuszmazurczak.ui.structure            :as mateuszmazurczak-ui-structure]
   [mateuszmazurczak.ui.team                 :as mateuszmazurczak-ui-team]
   [mateuszmazurczak.ui.us                   :as mateuszmazurczak-ui-us]
   [mateuszmazurczak.ui.values               :as mateuszmazurczak-ui-values]))

(defmethod mateuszmazurczak-fe-panels/panels :panels/home
  []
  [mateuszmazurczak-ui-structure/mateuszmazurczak-page-structure [mateuszmazurczak-home/home]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/disclaimer
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-legal/disclaimer]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/privacy
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-legal/privacy]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/simulation
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-simulation/simulation-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/simulation-pitch
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-simulation/simulation-pitch-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/simulation-interactive
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-toy-example/sample]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/about-us
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-us/about-us-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/our-brand
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-brand/our-brand-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/network
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-team/network-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/commitment
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-commitment/commitment-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/additional-outcomes
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-additional-outcomes/additional-outcomes-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/our-values
  []
  [:div {:class ["bg-theme-light"]}
   [mateuszmazurczak-ui-structure/structure
    [:div {:class ["pb-8"]}
     [mateuszmazurczak-ui-values/our-values-detailed]]]])

(defmethod mateuszmazurczak-fe-panels/panels :panels/not-found
  []
  (web-comp-errors/not-found
   {:title (mateuszmazurczak-fe-translate/tr :not-found-page)
    :description (mateuszmazurczak-fe-translate/tr :not-found-description)
    :back-home-text (mateuszmazurczak-fe-translate/tr :back-home)
    :back-link (mateuszmazurczak-fe-history/href ::mateuszmazurczak-routes/home)}))
