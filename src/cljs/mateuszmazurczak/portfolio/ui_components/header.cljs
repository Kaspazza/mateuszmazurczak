(ns mateuszmazurczak.portfolio.ui-components.header
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.header       :as sut]
   [mateuszmazurczak.ui.components.theme-toggle :as theme-toggle]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Header"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Header component with support for dark/light theme and language selection."
            :npm-install "No external dependencies"
            :source-code (embed-source "mateuszmazurczak.ui.components.header")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/header.cljs"
            :filename "header.cljs"}])

(defscene
 api-reference
 "Complete reference for all Header component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Header components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "base-header"
       :description "Low-level layout wrapper for header surfaces."
       :props [[":size" "keyword, optional - :full | :half."]
               [":sticky?" "boolean, optional - Uses sticky vs absolute positioning."]
               [":border?" "boolean, optional - Enables border/backdrop styling."]
               [":class" "string, optional - Additional Tailwind classes."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "transparent-header-comp"
       :description "Header shell without navigation menu items; left logo + right section only."
       :props [[":size" "keyword, optional - :full | :half."]
               [":sticky?" "boolean, optional - Sticky positioning."]
               [":border?" "boolean, optional - Border/backdrop toggle."]
               [":logo" "hiccup | component, optional - Left section content."]
               [":right-section" "hiccup | component, optional - Right section content."]
               [":class" "string, optional - Additional Tailwind classes."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "header-comp"
       :description "Main navigational header with variadic menu item maps appended after props map."
       :props [[":size" "keyword, optional - :full | :half."]
               [":logo" "hiccup | component, optional - Left logo content."]
               [":sticky?" "boolean, optional - Sticky positioning."]
               [":border?" "boolean, optional - Border/backdrop toggle."]
               [":right-section" "hiccup | component, optional - Right section content."]
               [":class" "string, optional - Additional Tailwind classes."]
               ["variadic menu-items" "zero or more maps after props, each {:title string :href string}."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "lang-select"
       :description "Language selector bound to frontend i18n state."
       :props [["arguments" "No props. Render as [lang-select]."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "transparent-header"
       :description "Precomposed transparent header with built-in theme toggle + language selector."
       :props [[":size" "keyword, optional - :full | :half."]
               [":border?" "boolean, optional - Border/backdrop toggle."]
               [":sticky?" "boolean, optional - Sticky positioning."]
               [":class" "string, optional - Additional Tailwind classes."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "toggle-header-border (utility fn)"
       :description "Scroll handler utility (not a UI component). Adds/removes header border class based on window scroll position threshold."
       :props [["arity" "(toggle-header-border event)"]
               ["event" "ignored argument; function reads window/document directly."]]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "header-comp takes variadic menu item maps after the props map (not :menu-items key)."]
       [:li "toggle-header-border should be wired as a scroll-side effect (listener), not rendered as a component."]
       [:li "transparent-header depends on theme + i18n ports being configured in application state/events."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[header-comp {:size :full\n              :sticky? true\n              :border? true\n              :logo [:span \"Brand\"]\n              :right-section [theme-toggle/theme-toggle]}\n {:title \"Docs\" :href \"/docs\"}\n {:title \"Pricing\" :href \"/pricing\"}]"]]]]]]))

(defscene
 header-basic
 "Basic header with logo and right section.
  Pure presentation wrapper for site headers.

  Use header-comp to supply navigation items."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "relative h-24 bg-background"}
   [sut/header-comp {:size :full
                     :sticky? false
                     :border? true
                     :logo [:span {:class "text-sm font-semibold"}
                            "Brand"]
                     :right-section [:span {:class "text-xs text-muted-foreground"}
                                     "Right"]}
    {:title "Docs"
     :href "#"}
    {:title "Blog"
     :href "#"}
    {:title "Contact"
     :href "#"}]]))

(defscene
 header-sticky
 "Sticky header with border.
  Sticky headers remain visible on scroll.

  Use :sticky? true for fixed navigation."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :full
                                                       :sticky? true
                                                       :border? true
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [:span {:class "text-xs"}
                                                                       "Account"]}
                                      {:title "Overview"
                                       :href "#"}
                                      {:title "Projects"
                                       :href "#"}
                                      {:title "Settings"
                                       :href "#"}]]))

(defscene
 header-half-width
 "Header with half-width layout.
  Use :size :half for constrained layouts."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :half
                                                       :sticky? false
                                                       :border? false
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [:span {:class "text-xs"}
                                                                       "Sign In"]}
                                      {:title "Features"
                                       :href "#"}
                                      {:title "Pricing"
                                       :href "#"}]]))

(defscene
 header-with-theme-toggle
 "Header with theme toggle in right section.
  Demonstrates composition with theme toggle control."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :full
                                                       :sticky? false
                                                       :border? true
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [theme-toggle/theme-toggle]}
                                      {:title "Home"
                                       :href "#"}
                                      {:title "Docs"
                                       :href "#"}]]))