(ns mateuszmazurczak.ui.header
  (:require
   [automaton-web.components.fe-language-select :as web-fe-language-select]
   [automaton-web.components.header             :as web-header]
   [mateuszmazurczak.i18n.events                :as mm-i18n-evts]
   [mateuszmazurczak.i18n.language              :as mm-i18n-lang]
   [mateuszmazurczak.i18n.translate             :as mm-i18n-translate]
   [mateuszmazurczak.navigation.utils           :as mm-nav]
   [mateuszmazurczak.routes                     :as mm-routes]))



(def lang-select
  [web-fe-language-select/language-select
   ::mm-i18n-evts/change-lang
   (mm-i18n-lang/create-ui-languages)
   mm-i18n-lang/id-to-str])

(defn transparent-header
  [{:keys [size border? sticky?]}]
  [web-header/transparent-header {:size size
                                  :sticky? sticky?
                                  :border? border?
                                  :right-section lang-select}])

(defn header
  [{:keys [size border? sticky?]}]
  [web-header/header {:size size
                      :sticky? sticky?
                      :border? border?
                      :right-section lang-select}
   {:title (mm-i18n-translate/tr :simulation)
    :href (mm-nav/href-delta ::mm-routes/simulation-pitch)}
   {:title (mm-i18n-translate/tr :about-us)
    :href (mm-nav/href-delta ::mm-routes/about-us)}
   {:title (mm-i18n-translate/tr :our-offers)
    :href (str (mm-nav/href-delta ::mm-routes/home) "#offer")}])
