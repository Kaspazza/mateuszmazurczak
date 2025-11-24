(ns mateuszmazurczak.domain.pages.home
  "Home page domain: data structures, schemas, and builders"
  (:require
   [malli.core                          :as m]
   [malli.error                         :as me]
   [mateuszmazurczak.domain.i18n.schema :as i18n-schema]))

(def ArticleCard
  "Schema for a single article card displayed on home page"
  [:map
   [:id :keyword]
   [:title :string]
   [:description :string]
   [:date :string]
   [:tags [:vector :string]]
   [:img :string]
   [:on-click [:maybe fn?]]])

(def RawAboutMeSection
  "Schema for raw about me section (i18n markers)"
  [:map {:closed true}
   [:welcome-text i18n-schema/I18nMarker]
   [:description i18n-schema/I18nMarker]
   [:contact-info i18n-schema/I18nMarker]])

(def AboutMeSection
  "Schema for translated about me section"
  [:map {:closed true}
   [:welcome-text :string]
   [:description :string]
   [:contact-info :string]])

(def RawNavigation
  "Schema for raw navigation section (i18n markers)"
  [:map {:closed true}
   [:text i18n-schema/I18nMarker]
   [:dark-mode :boolean]])

(def Navigation
  "Schema for translated navigation section"
  [:map {:closed true}
   [:text :string]
   [:href :string]
   [:dark-mode :boolean]])

(def RawHomePageData
  "Schema for raw home page data (stored in app-db)"
  [:map {:closed true}
   [:loading? :boolean]
   [:about-me-section {:optional true}
    RawAboutMeSection]
   [:navigation {:optional true}
    RawNavigation]
   [:articles {:optional true}
    [:vector ArticleCard]]])

(def HomePageData
  "Complete schema for translated home page data (from subscription)"
  [:or
   ;; if it's loading we don't care about what's there
   [:map {:closed false}
    [:loading? [:= true]]]
   ;; if it's not loading the data should be there
   [:map {:closed true}
    [:loading? [:= false]]
    [:about-me-section AboutMeSection]
    [:navigation Navigation]
    [:articles [:vector ArticleCard]]]])

(defn valid-raw-home-page-data?
  "Validate raw home page data against schema"
  [data]
  (m/validate RawHomePageData data))

(defn explain-raw-home-page-data
  "Explain validation errors for raw home page data"
  [data]
  (m/explain RawHomePageData data))

(defn valid-home-page-data?
  "Validate translated home page data against schema"
  [data]
  (m/validate HomePageData data))

(defn explain-home-page-data
  "Explain validation errors for translated home page data"
  [data]
  (me/humanize (m/explain HomePageData data)))


(defn initial-home-data
  "Returns initial home page data structure for app-db initialization.
   
   Starts with loading state. Actual data is loaded via :home/on-route-enter event.
   This avoids dependency on router during system initialization."
  []
  {:loading? true
   :about-me-section {:welcome-text [:i18n :hi-mati]
                      :description [:i18n :i-like-simplicity]
                      :contact-info [:i18n :contact-me]}
   :navigation {:text [:i18n :articles]
                :dark-mode true}
   :articles []})

(defn build-raw-home-data
  "Build raw home page data structure (without handlers/navigation).
   
   Returns pure data that can be enhanced at the application layer
   with infrastructure concerns (navigation, event handlers)."
  [articles-data]
  {:about-me-section {:welcome-text [:i18n :hi-mati]
                      :description [:i18n :i-like-simplicity]
                      :contact-info [:i18n :contact-me]}
   :navigation {:text [:i18n :articles]
                :dark-mode true}
   :articles articles-data})

(defn add-navigation-click-handlers
  "Add navigation click handlers to articles.
   
   Takes articles and a route-id+params builder function,
   returns articles with :on-click dispatch markers."
  [articles-data]
  (mapv (fn [article]
          (assoc article
                 :on-click
                 [:dispatch
                  [:nav/navigate
                   :mateuszmazurczak.adapters.navigation.routes/article
                   {:article-id (name (:id article))}]]))
        articles-data))

(defn build-home-page-data
  "Build complete home page data with navigation and handlers.
   
   Takes:
   - articles-data: collection of article entities
   - navigation-href: href string for articles navigation
   - route-builder-fn: fn that takes article and returns [event-id params] vector
   
   Returns complete home page data ready for app-db."
  [articles-data navigation-href]
  (let [raw-data (build-raw-home-data articles-data)]
    (-> raw-data
        (assoc-in [:navigation :href] navigation-href)
        (update :articles add-navigation-click-handlers))))
