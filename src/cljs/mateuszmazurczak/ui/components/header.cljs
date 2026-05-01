(ns mateuszmazurczak.ui.components.header
  "Header component with support for dark/light theme and language selection.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   [mateuszmazurczak.domain.i18n.language       :as mm-i18n-lang]
   [mateuszmazurczak.frontend-i18n              :as fi18n]
   [mateuszmazurczak.ui.components.select       :as ui-select]
   [mateuszmazurczak.ui.components.theme-toggle :as theme-toggle]
   [mateuszmazurczak.utils.styles               :refer [merge-classes]]))

(defn base-header
  "Base header component with common layout and styling.
  
  Props:
  - :size (:full | :half) - Header width (default: :full)
  - :sticky? (boolean) - Whether header is sticky or absolute
  - :border? (boolean) - Whether to show border
  - :class - Additional CSS classes"
  [{:keys [size sticky? border? class]} content]
  [:header
   {:class
    (merge-classes
     (if sticky? "sticky" "absolute")
     (when border?
       "border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60")
     "inset-x-0"
     "top-0"
     "z-50"
     "py-2"
     (if (= :full size) "w-full" "w-full lg:w-1/2")
     class)}
   content])

(defn transparent-header-comp
  "Transparent header component without menu items.
  
  Props:
  - :size (:full | :half) - Header width
  - :sticky? (boolean) - Sticky positioning
  - :border? (boolean) - Show border
  - :logo - Logo component/hiccup
  - :right-section - Right section component/hiccup
  - :class - Additional CSS classes"
  [{:keys [size sticky? border? logo right-section class]}]
  [base-header {:size size
                :sticky? sticky?
                :border? border?
                :class class}
   [:nav {:class (merge-classes "flex" "items-center" "justify-between" "px-6" "lg:px-8")}
    logo
    [:div {:class (merge-classes "flex" "items-center" "gap-4")}
     right-section]]])


(defn header-comp
  "Header component with menu items.
  
  Props:
  - :size (:full | :half) - Header width
  - :sticky? (boolean) - Sticky positioning
  - :border? (boolean) - Show border
  - :logo - Logo component/hiccup
  - :right-section - Right section component/hiccup
  - :class - Additional CSS classes
  - menu-items - Collection of menu item maps with :title and :href"
  [{:keys [size logo sticky? border? right-section class]} & menu-items]
  [base-header {:size size
                :sticky? sticky?
                :border? border?
                :class class}
   [:nav {:class (merge-classes "flex" "items-center" "justify-between" "px-6" "lg:px-8")}
    logo
    [:div {:class (merge-classes "hidden" "lg:flex" "lg:gap-x-12")}
     (for [{:keys [title href]} menu-items]
       ^{:key (str title href)}
       [:a {:href href
            :class (merge-classes "text-sm" "font-semibold"
                                  "leading-6" "text-foreground"
                                  "hover:text-foreground/80" "transition-colors")}
        title])]
    [:div {:class (merge-classes "flex" "items-center" "gap-4")}
     right-section]]])


(defn lang-select
  "Language selection dropdown using Radix UI select component.
  
  Displays available languages and allows users to switch between them.
  Uses the modern select component with dark mode support."
  []
  (let [selected-value (fi18n/current-language-str)
        languages mm-i18n-lang/web-languages]
    [ui-select/select {:value selected-value
                       :on-value-change fi18n/change-language!}
     [ui-select/select-trigger {:size "sm"
                                :class "min-w-[100px]"}
      [ui-select/select-value {:placeholder "Language"}]]
     [ui-select/select-content {}
      (for [[_lang-id {:keys [ui-text]}] languages]
        ^{:key ui-text}
        [ui-select/select-item {:value ui-text}
         ui-text])]]))

(defn transparent-header
  "Transparent header with theme toggle and language selector.
  
  Props:
  - :size (:full | :half) - Header width (default: :full)
  - :border? (boolean) - Show border (default: false)
  - :sticky? (boolean) - Sticky positioning (default: true)
  - :class - Additional CSS classes
  
  Example:
  [transparent-header {:size :full :border? true :sticky? true}]"
  [{:keys [size border? sticky? class]}]
  [transparent-header-comp {:size size
                            :sticky? sticky?
                            :border? border?
                            :class class
                            :right-section [:<> [theme-toggle/theme-toggle] [lang-select]]}])

(defn toggle-header-border
  "Toggle header border based on scroll position.
  
  Shows/hides border when scrolling past 50px threshold.
  This is used in lifecycle methods to add dynamic scroll behavior."
  [_]
  (let [scroll-y (.-scrollY js/window)
        header-el (.querySelector js/document "header")]
    (when header-el
      (let [header-css-list (.-classList header-el)
            has-border? (.contains header-css-list "border-b")]
        (cond
          ;; Scrolled down - remove border
          (and (> scroll-y 50) has-border?) (.remove header-css-list "border-b")
          ;; Scrolled up - add border
          (and (<= scroll-y 50) (not has-border?)) (.add header-css-list "border-b")
          :else nil)))))


