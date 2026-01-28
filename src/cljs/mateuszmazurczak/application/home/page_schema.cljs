(ns mateuszmazurczak.application.home.page-schema
  "Home page data schemas and validation."
  (:require
   [malli.core                          :as m]
   [malli.error                         :as me]
   [mateuszmazurczak.domain.i18n.schema :as i18n-schema]))

;; =============================================================================
;; Schemas
;; =============================================================================

(def ArticleCard
  "Schema for a single article card displayed on home page."
  [:map
   [:id :keyword]
   [:title :string]
   [:description :string]
   [:date :string]
   [:tags [:vector :string]]
   [:img :string]
   [:on-click [:maybe fn?]]])

(def RawAboutMeSection
  "Schema for raw about me section (i18n markers)."
  [:map {:closed true}
   [:welcome-text i18n-schema/I18nMarker]
   [:description i18n-schema/I18nMarker]
   [:contact-info i18n-schema/I18nMarker]])

(def AboutMeSection
  "Schema for translated about me section."
  [:map {:closed true}
   [:welcome-text :string]
   [:description :string]
   [:contact-info :string]])

(def RawNavigation
  "Schema for raw navigation section (i18n markers)."
  [:map {:closed true}
   [:text i18n-schema/I18nMarker]
   [:dark-mode :boolean]])

(def Navigation
  "Schema for translated navigation section."
  [:map {:closed true}
   [:text :string]
   [:href :string]
   [:dark-mode :boolean]])

(def RawHomePageData
  "Schema for raw home page data (stored in app-db)."
  [:map {:closed true}
   [:loading? :boolean]
   [:about-me-section {:optional true}
    RawAboutMeSection]
   [:navigation {:optional true}
    RawNavigation]
   [:articles {:optional true}
    [:vector ArticleCard]]])

(def HomePageData
  "Complete schema for translated home page data (from subscription)."
  [:or
   [:map {:closed false}
    [:loading? [:= true]]]
   [:map {:closed true}
    [:loading? [:= false]]
    [:about-me-section AboutMeSection]
    [:navigation Navigation]
    [:articles [:vector ArticleCard]]]])

;; =============================================================================
;; Validation
;; =============================================================================

(defn valid-raw-home-page-data?
  "Validate raw home page data against schema."
  [data]
  (m/validate RawHomePageData data))

(defn explain-raw-home-page-data
  "Explain validation errors for raw home page data."
  [data]
  (m/explain RawHomePageData data))

(defn valid-home-page-data?
  "Validate translated home page data against schema."
  [data]
  (m/validate HomePageData data))

(defn explain-home-page-data
  "Explain validation errors for translated home page data."
  [data]
  (me/humanize (m/explain HomePageData data)))

;; =============================================================================
;; Initial Data
;; =============================================================================

(defn initial-home-data
  "Initial home page data structure."
  []
  {:loading? true
   :about-me-section {:welcome-text [:i18n :hi-mati]
                      :description [:i18n :i-like-simplicity]
                      :contact-info [:i18n :contact-me]}
   :navigation {:text [:i18n :articles]
                :dark-mode true}
   :articles []})
