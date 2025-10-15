(ns mateuszmazurczak.ui.header
  (:require
   [clojure.string                     :as str]
   [mateuszmazurczak.frontend-i18n     :as fi18n]
   [mateuszmazurczak.i18n.language     :as mm-i18n-lang]
   [mateuszmazurczak.navigation.core   :as navigation]
   [mateuszmazurczak.navigation.routes :as mm-routes]
   [reagent.core                       :as r]))

(defn string-to-id
  "Transform what is not alphanumerical to an id
  If `txt` is an empty string, a uuid turned into a string is returned
  Params:
  * `txt` text to transform"
  [txt]
  (if (str/blank? txt)
    (-> (random-uuid)
        str)
    (-> txt
        str
        (str/replace #"[^\w]" "-")
        str/lower-case)))

(defn reagent-option
  "Return the option of an existing reagent object
  Manages both case where the option map is already existing or not
  Params:
  * `comp` reagent component to update "
  [component]
  (let [maybe-opt (second component)] (if (map? maybe-opt) maybe-opt {})))


(defn- update-select-options
  "Add options to select options components.
  Generate a key based on `select-id` and `opt-value`"
  [{:keys [opt-value key]
    :as opt}
   select-id]
  (assoc opt :key (or key (str select-id "-" (string-to-id opt-value)))))

(defn update-reagent-options
  "Update the reagent component to insert `options`
  Manage both cases where the option map already exist or not
  Params:
  * `options` reagent options to be inserted
  * `component` reagent component to update"
  [options component]
  (let [[comp-key & comp-rest] component
        maybe-opt (first comp-rest)
        updated-options (if (map? maybe-opt)
                          (apply vector comp-key options (rest comp-rest))
                          (apply vector comp-key options comp-rest))]
    updated-options))

(defn simple-select
  "Simple html select

  Params:
  * `props` properties to tweak the selector
      * `id` Optional (default to string-to-id of html-name) is the html id of the component
      * `html-name` name to represent the data stored if that data are POSTed in a form
      * `class`  css attributes to add to default presentation
      * `value` is a currently selected value
      * `on-change` method to call on change of the value, typically dispatch an event
      * `options` a list of option, as `options-arg`, easier to use if you already handle a collection of options
  * `options-arg` options should be a collection of [:option] html tags. This value is useful to directly pass options as a variadic arguments. It's superseeding `options` keyword."
  [{:keys [id html-name class on-change value options]
    :as _props}
   &
   options-arg]
  (let [options (for [select-option (or options-arg options)]
                  (-> select-option
                      reagent-option
                      (update-select-options id)
                      (update-reagent-options select-option)))]
    (fn [] [:select {:id id
                     :name html-name
                     :default-value value
                     :class (vec (concat ["block"
                                          "w-full"
                                          "rounded-md"
                                          "border-0"
                                          "py-1"
                                          "pl-3"
                                          "pr-10"
                                          "text-gray-900"
                                          "ring-1"
                                          "ring-inset"
                                          "ring-gray-300"
                                          "focus:ring-2"
                                          "focus:ring-indigo-600"
                                          "sm:text-sm"
                                          "sm:leading-6"]
                                         class))
                     :on-change on-change}
            options])))

(defn- base-header
  [{:keys [size sticky? border?]} content]
  [:header {:class [(if sticky? "sticky" "absolute")
                    (when border?
                      "border border-solid border-b-theme-dark bg-theme-light")
                    "inset-x-0 top-0"
                    "py-2"
                    (if (= :full size) "w-full" "w-full lg:w-1/2")]}
   content])

(defn transparent-header-comp
  [{:keys [size sticky? border? logo right-section]}]
  [base-header {:size size
                :sticky? sticky?
                :border? border?}
   [:nav {:class
          ["flex items-center content-between justify-between px-6 lg:px-8"]}
    logo
    [:div right-section]]])


(defn header-comp
  [{:keys [size logo sticky? border? right-section]} & menu-items]
  [base-header {:size size
                :sticky? sticky?
                :border? border?}
   [:nav {:class
          ["flex items-center content-between justify-between px-6 lg:px-8"]}
    logo
    [:div {:class ["hidden lg:flex lg:gap-x-12"]}
     (for [{:keys [title href]} menu-items]
       ^{:key (str title href)}
       [:a {:href href
            :class ["text-sm font-semibold leading-6 text-gray-900"]}
        title])]
    [:div right-section]]])


(def languages-options
  (->> mm-i18n-lang/web-languages
       (map (fn [[_lang-id {:keys [ui-text]}]] [:option {:value ui-text}
                                                ui-text]))))
(defn lang-select
  []
  (let [selected-value (fi18n/current-language-str)]
    [simple-select {:id "lang"
                    :name "lang"
                    :on-change fi18n/change-language!
                    :value selected-value
                    :options languages-options}]))

(defn transparent-header
  [{:keys [size border? sticky?]}]
  [transparent-header-comp {:size size
                            :sticky? sticky?
                            :border? border?
                            :right-section [lang-select]}])

(defn toggle-header-border
  [_]
  (let [scroll-y (.-scrollY js/window)
        header-css-list (.-classList (.querySelector js/document "header"))
        border-none? (.contains header-css-list "border-none!")]
    (cond
      (and (< scroll-y 50) (true? border-none?))
      (.remove header-css-list "border-none!" "hidden" "md:block")
      (and (> scroll-y 50) (false? border-none?))
      (.add header-css-list "border-none!" "hidden" "md:block")
      :else nil)))

(defn header
  [{:keys [_size _border? _sticky?]}]
  (r/create-class
   {:component-did-mount
    (fn [_] (.addEventListener js/window "scroll" toggle-header-border))
    :component-will-unmount
    (fn [_] (.removeEventListener js/window "scroll" toggle-header-border))
    :reagent-render (fn [{:keys [size border? sticky?]}]
                      [header-comp {:size size
                                    :sticky? sticky?
                                    :border? border?
                                    :right-section [lang-select]}
                       {:title "Mateusz Mazurczak"
                        :href (navigation/href ::mm-routes/home)}
                       {:title (fi18n/tr :articles)
                        :href (navigation/href ::mm-routes/articles)}])}))
