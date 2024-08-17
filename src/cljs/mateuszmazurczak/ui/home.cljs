(ns mateuszmazurczak.ui.home
  "Customer app home page assembly"
  (:require
   [automaton-web.components.init-components :as web-init-components]
   #_[mateuszmazurczak.ui.downloadable-materials :as lcdm]
   [mateuszmazurczak.ui.elevator-pitch                :as lcep]
   [mateuszmazurczak.ui.hero                          :as lch]
   [mateuszmazurczak.ui.simulation                    :as mateuszmazurczak-ui-simulation]
   [mateuszmazurczak.ui.team                          :as mateuszmazurczak-ui-team]
   [mateuszmazurczak.ui.us                            :as mateuszmazurczak-ui-us]))

(defn mateuszmazurczak-page
  [{:keys [_document]}]
  [:div {:class ["flex flex-col"]}
   [lch/hero-section]
   [mateuszmazurczak-ui-simulation/simulation-section {:dark? true}]
   [:div {:id "offer"}
    [lcep/elevator-pitch-section {:dark? false}]]
   [:div {:id "about-us"}
    [mateuszmazurczak-ui-us/about-us-section {:dark? true}]]
   [mateuszmazurczak-ui-team/our-team-section {:dark? false}]
   #_[lcdm/downloadable-info {:dark? true
                              :document document}]])

(defn home
  "Functional component for displaying mateuszmazurczak page sections."
  []
  [:f> #(let [_ (web-init-components/init-rendering)] [mateuszmazurczak-page])])
