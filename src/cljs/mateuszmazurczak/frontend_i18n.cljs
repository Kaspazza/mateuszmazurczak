(ns mateuszmazurczak.frontend-i18n
  "Frontend i18n port - public API for internationalization in the browser.
  
  This is the ONLY namespace that UI components should require for i18n.
  It extends core i18n with browser-specific functionality.
  
  This is a hybrid port - provides API and directly uses state/events ports."
  (:require
   [clojure.walk                          :as walk]
   [mateuszmazurczak.domain.i18n.language :as i18n-lang]
   [mateuszmazurczak.ports.events         :as events]
   [mateuszmazurczak.ports.i18n           :as i18n]
   [mateuszmazurczak.ports.state          :as state]
   [mateuszmazurczak.utils.cookies        :as mm-cookies]
   [mateuszmazurczak.utils.url            :as utils-url]))

;; Language Strategy (initialization)

(defn- cookies-language
  "Get language from browser cookies"
  []
  (-> "lang"
      mm-cookies/get-cookie-val
      i18n-lang/ui-str-to-id))

(defn language-strategy
  "Determine initial language for frontend app.
  
  Priority:
  1. URL query parameter (?lang=en)
  2. Browser cookie
  3. Default main language
  
  Returns: language keyword (e.g., :en, :pl)"
  []
  (let [par-lang (-> (utils-url/current-url)
                     utils-url/lang-in-url-par
                     i18n-lang/ui-str-to-id)]
    (or par-lang (cookies-language) (first i18n-lang/main-langs))))

;; Public API - Translation Functions (used by UI components)

(defn tr
  "Translate a key to text in the current language.
  
  This is a reactive function that watches the current language and translator
  from the application state. Use this in Reagent components for automatic re-rendering
  when language changes.
  
  Params:
  * `tr-id` - translation key (keyword)
  * `params` - optional map of parameters for interpolation
  
  Returns: translated string (reactive)
  
  Examples:
    (tr :articles) ;; => \"Articles\" or \"Artykuły\" depending on current lang
    (tr :greeting {:name \"John\"}) ;; => \"Hello, John!\" with interpolation"
  ([tr-id] (tr tr-id nil))
  ([tr-id params]
   (let [lang @(state/watch [:i18n/lang])
         translator @(state/watch [:i18n/translator])]
     (when translator
       (if params (i18n/tr translator lang tr-id params) (i18n/tr translator lang tr-id))))))

(defn- i18n-marker?
  "Check if a value is an i18n translation marker.
   
   An i18n marker is a vector where:
   - First element is :i18n keyword
   - Second element is the translation key
   - Optional third element is a params map"
  [v]
  (and (vector? v)
       (= :i18n (first v))
       (keyword? (second v))
       (or (= 2 (count v)) (and (= 3 (count v)) (map? (nth v 2))))))

(defn- translate-marker
  "Translate an i18n marker to its string value.
   
   Examples:
   [:i18n :hello] -> \"Hello\"
   [:i18n :greeting {:name \"John\"}] -> \"Hello, John!\""
  [[_i18n-kw translation-key params]]
  (if params (tr translation-key params) (tr translation-key)))

(defn i18n-markers->translation
  "Walk a data structure and translate all [:i18n ...] markers.
   
   Translation markers follow the pattern:
   [:i18n :translation-key]
   or
   [:i18n :translation-key {:param value}]
   
   Uses `tr` for translation.
   
   Examples:
   {:title [:i18n :hello]} 
   -> {:title \"Hello\"}
   
   {:nested {:msg [:i18n :greeting {:name \"John\"}]}}
   -> {:nested {:msg \"Hello, John!\"}}"
  [data]
  (walk/prewalk #(if (i18n-marker? %) (translate-marker %) %) data))

;; Public API - Language Selection

(defn current-language
  "Get the current language as a keyword.
  
  This is a reactive watch - components will re-render when language changes.
  
  Returns: language keyword (e.g., :en, :pl)"
  []
  @(state/watch [:i18n/lang]))

(defn current-language-str
  "Get the current language as a UI string.
  
  Returns: language string (e.g., \"English\", \"Polski\")"
  []
  @(state/watch [:i18n/lang-str]))

(defn change-language!
  "Change the current language.
  
  This triggers state update, cookie persistence, and URL parameter change.
  
  Params:
  * `lang-evt` - DOM event from language selector (e.target.value will be used)"
  [lang-evt]
  (events/dispatch! [:i18n/change-lang lang-evt]))


