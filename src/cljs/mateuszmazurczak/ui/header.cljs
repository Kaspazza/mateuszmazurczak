(ns mateuszmazurczak.ui.header
  (:require
   [automaton-web.components.fe-language-select :as web-fe-language-select]
   [automaton-web.components.header             :as web-header]
   [automaton-web.components.logo               :as web-logo]
   [mateuszmazurczak.fe.events                           :as mateuszmazurczak-fe-events]
   [mateuszmazurczak.i18n.fe.translate                   :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.i18n.language                       :as mateuszmazurczak-language]
   [mateuszmazurczak.routes                              :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.fe-navigation                    :as mateuszmazurczak-fe-nav]))

(defn hephaistox-logo
  []
  [:a {:href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/home)}
   [web-logo/hephaistox]])

(def lang-select
  [web-fe-language-select/language-select
   ::mateuszmazurczak-fe-events/change-lang
   (mateuszmazurczak-language/create-ui-languages)
   mateuszmazurczak-language/id-to-str])

(defn transparent-header
  [{:keys [size border? sticky?]}]
  [web-header/transparent-header {:size size
                                  :sticky? sticky?
                                  :border? border?
                                  :logo [:div {:class ["flex" "lg:flex-1"]}
                                         [hephaistox-logo]]
                                  :right-section lang-select}])

(defn header
  [{:keys [size border? sticky?]}]
  [web-header/header {:size size
                      :sticky? sticky?
                      :border? border?
                      :logo [hephaistox-logo]
                      :right-section lang-select}
   {:title (mateuszmazurczak-fe-translate/tr :simulation)
    :href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/simulation-pitch)}
   {:title (mateuszmazurczak-fe-translate/tr :about-us)
    :href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/about-us)}
   {:title (mateuszmazurczak-fe-translate/tr :our-offers)
    :href (str (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/home) "#offer")}])
