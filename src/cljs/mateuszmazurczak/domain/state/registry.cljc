(ns mateuszmazurczak.domain.state.registry)

;; =============================================================================
;; Frontend Database Structure
;; =============================================================================
;;
;; The frontend database (re-frame app-db) has the following structure:
;;
;; {:name "mateuszmazurczak"           ; Application name
;;  :lang :en                           ; Current language (:en | :pl)
;;  :theme :light                       ; Current theme (:light | :dark)
;;  :current-route {...}                ; Current route data from reitit
;;  :translator <translator-instance>   ; i18n translator service
;;  :logger <logger-instance>           ; Logging service
;;  :pages                              ; Page-specific state
;;    {:home                            ; Home page state
;;       {:loading? false
;;        :about-me-section {...}       ; About me content with i18n markers
;;        :navigation {...}             ; Navigation data with i18n markers
;;        :articles [...]}}}            ; Article cards
;;
;; Design principles:
;; - Top-level services (logger, translator) for easy access
;; - Pages store both raw data (with i18n markers) and loading states
;; - Subscriptions transform raw data (translate i18n markers, convert [:dispatch] markers)
;; - Top-level :lang and :current-route for easy access


(def ^:dynamic *lang-path* [:lang])
(def ^:dynamic *theme-path* [:theme])
(def ^:dynamic *current-route-path* [:current-route])
(def ^:dynamic *translator-path* [:translator])
(def ^:dynamic *logger-path* [:logger])

(def ^:dynamic *pages-path* [:pages])
(def ^:dynamic *home-page-path* [:pages :home])

(def watch-reg
  "Registry of all application watch.
   
   This is the single source of truth for what can be queried from the state."
  {:nav/current-route {:description "Returns the current route map with page-id, path-params, etc."
                       :input-schema [:cat [:= :nav/current-route]]
                       :output-schema [:maybe
                                       [:map
                                        [:page-id keyword?]
                                        [:route-name {:optional true}
                                         keyword?]
                                        [:path-parameters {:optional true}
                                         map?]
                                        [:query-parameters {:optional true}
                                         map?]
                                        [:fragment {:optional true}
                                         [:maybe string?]]]]}
   :nav/current-page {:description "Returns the current page-id keyword"
                      :input-schema [:cat [:= :nav/current-page]]
                      :output-schema [:maybe keyword?]}
   :nav/path-params {:description "Returns path parameters map for current route"
                     :input-schema [:cat [:= :nav/path-params]]
                     :output-schema [:maybe map?]}
   :nav/query-params {:description "Returns query parameters map for current route"
                      :input-schema [:cat [:= :nav/query-params]]
                      :output-schema [:maybe map?]}
   :nav/active-route? {:description
                       "Returns true if given route-name matches current route, false otherwise"
                       :input-schema [:cat [:= :nav/active-route?] keyword?] ; [watch-id route-name]
                       :output-schema boolean?}
   :pages/home {:description
                "Returns processed home page data with translation and validation metadata"
                :input-schema [:cat [:= :pages/home]]
                :output-schema [:maybe
                                [:map
                                 [:data map?]
                                 [:valid? boolean?]
                                 [:error {:optional true}
                                  map?]]]}
   :home/raw-data {:description "Returns raw (untranslated) home page data from state"
                   :input-schema [:cat [:= :home/raw-data]]
                   :output-schema [:maybe map?]}
   :logger {:description "Returns the logger instance from app state"
            :input-schema [:cat [:= :logger]]
            :output-schema [:maybe any?]} ; Logger protocol/type would be better
   :i18n/lang {:description "Returns current language keyword (:pl or :en)"
               :input-schema [:cat [:= :i18n/lang]]
               :output-schema [:maybe keyword?]}
   :i18n/translator {:description "Returns the i18n translator instance"
                     :input-schema [:cat [:= :i18n/translator]]
                     :output-schema [:maybe any?]} ; Translator protocol would be better
   :i18n/lang-str {:description "Returns current language as UI string (\"PL\" or \"EN\")"
                   :input-schema [:cat [:= :i18n/lang-str]]
                   :output-schema [:maybe string?]}})
