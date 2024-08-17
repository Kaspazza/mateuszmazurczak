(ns mateuszmazurczak.ui.header
  (:require
   [automaton-web.components.fe-language-select :as web-fe-language-select]
   [automaton-web.components.header             :as web-header]
   [automaton-web.components.logo               :as web-logo]
   [landing.fe.events                           :as landing-fe-events]
   [landing.i18n.fe.translate                   :as landing-fe-translate]
   [landing.i18n.language                       :as landing-language]
   [landing.routes                              :as landing-routes]
   [landing.ui.fe-navigation                    :as landing-fe-nav]))

(defn hephaistox-logo
  []
  [:a {:href (landing-fe-nav/href-delta ::landing-routes/home)}
   [web-logo/hephaistox]])

(def lang-select
  [web-fe-language-select/language-select
   ::landing-fe-events/change-lang
   (landing-language/create-ui-languages)
   landing-language/id-to-str])

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
   {:title (landing-fe-translate/tr :simulation)
    :href (landing-fe-nav/href-delta ::landing-routes/simulation-pitch)}
   {:title (landing-fe-translate/tr :about-us)
    :href (landing-fe-nav/href-delta ::landing-routes/about-us)}
   {:title (landing-fe-translate/tr :our-offers)
    :href (str (landing-fe-nav/href-delta ::landing-routes/home) "#offer")}])
