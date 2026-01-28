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
;;  :entities                           ; Normalized entities store
;;    {:aoc-solutions                   ; AoC solutions by ID
;;       {"solution-id-1" {...}         ; Full solution entity
;;        "solution-id-2" {...}}}
;;  :pages                              ; Page-specific state
;;    {:home                            ; Home page state
;;       {:loading? false
;;        :about-me-section {...}       ; About me content with i18n markers
;;        :navigation {...}             ; Navigation data with i18n markers
;;        :articles [...]}              ; Article cards
;;     :aoc                             ; AoC page state
;;       {:solution-ids [...]           ; References to entities by ID
;;        :selected-year 2025
;;        :loading? false
;;        ...}}}
;;
;; Design principles:
;; - Top-level services (logger, translator) for easy access
;; - Pages store both raw data (with i18n markers) and loading states
;; - Normalized entities in :entities for shared data 
;; - Pages reference entities by ID, subscriptions denormalize for UI
;; - Subscriptions transform raw data (translate i18n markers, convert [:dispatch] markers)
;; - Top-level :lang and :current-route for easy access


(def ^:dynamic *lang-path* [:lang])
(def ^:dynamic *theme-path* [:theme])
(def ^:dynamic *current-route-path* [:current-route])
(def ^:dynamic *translator-path* [:translator])
(def ^:dynamic *logger-path* [:logger])
(def ^:dynamic *admin-logged-in-path* [:admin :logged-in?])

(def ^:dynamic *entities-path* [:entities])
(def ^:dynamic *aoc-solutions-path* [:entities :aoc-solutions])

(def ^:dynamic *pages-path* [:pages])
(def ^:dynamic *home-page-path* [:pages :home])
(def ^:dynamic *aoc-page-path* [:pages :aoc])
(def ^:dynamic *admin-page-path* [:pages :admin])
(def ^:dynamic *qr-codes-page-path* [:pages :qr-codes])

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
                   :output-schema [:maybe string?]}
   :pages/aoc {:description "Returns processed AoC page data with validation metadata"
               :input-schema [:cat [:= :pages/aoc]]
               :output-schema [:maybe
                               [:map
                                [:data map?]
                                [:valid? boolean?]
                                [:error {:optional true}
                                 map?]]]}
   :aoc/raw-data {:description "Returns raw AoC page data from state"
                  :input-schema [:cat [:= :aoc/raw-data]]
                  :output-schema [:maybe map?]}
   :aoc/solutions-entities {:description "Returns AoC solutions entities map (id -> solution)"
                            :input-schema [:cat [:= :aoc/solutions-entities]]
                            :output-schema [:maybe map?]}
   :theme/current {:description "Returns current theme keyword (:light | :dark)"
                   :input-schema [:cat [:= :theme/current]]
                   :output-schema [:maybe [:enum :light :dark]]}
   :admin/logged-in? {:description "Returns true if admin is logged in, false otherwise"
                      :input-schema [:cat [:= :admin/logged-in?]]
                      :output-schema [:maybe boolean?]}
   :pages/admin {:description "Returns processed admin page data with validation metadata"
                 :input-schema [:cat [:= :pages/admin]]
                 :output-schema [:maybe
                                 [:map
                                  [:data map?]
                                  [:valid? boolean?]
                                  [:error {:optional true}
                                   map?]]]}
   :admin/raw-data {:description "Returns raw admin page data from state"
                    :input-schema [:cat [:= :admin/raw-data]]
                    :output-schema [:maybe map?]}
   :pages/qr-codes {:description "Returns processed QR codes page data with validation metadata"
                    :input-schema [:cat [:= :pages/qr-codes]]
                    :output-schema [:maybe
                                    [:map
                                     [:data map?]
                                     [:valid? boolean?]
                                     [:error {:optional true}
                                      map?]]]}
   :qr-codes/raw-data {:description "Returns raw QR codes page data from state"
                       :input-schema [:cat [:= :qr-codes/raw-data]]
                       :output-schema [:maybe map?]}})
