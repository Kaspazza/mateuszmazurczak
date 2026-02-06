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
            :source-code (embed-source mateuszmazurczak.ui.components.header)
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
       :description "Base header component"
       :props [[":size" "keyword, optional (default :full). One of: :full | :half"]
               [":sticky?" "boolean, optional - Sticky positioning"]
               [":border?" "boolean, optional - Render border styles"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "transparent-header-comp"
       :description "Transparent header comp component"
       :props [[":size" "keyword, optional (default :full). One of: :full | :half"]
               [":sticky?" "boolean, optional - Sticky positioning"]
               [":border?" "boolean, optional - Render border styles"]
               [":logo" "hiccup | component, optional - Logo/content shown on the left"]
               [":right-section" "hiccup | component, optional - Right-side header content"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "header-comp"
       :description "Header comp component"
       :props [[":size" "keyword, optional (default :full). One of: :full | :half"]
               [":logo" "hiccup | component, optional - Logo/content shown on the left"]
               [":sticky?" "boolean, optional - Sticky positioning"]
               [":border?" "boolean, optional - Render border styles"]
               [":right-section" "hiccup | component, optional - Right-side header content"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "lang-select"
                                             :description "Lang select component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card
      {:component-name "transparent-header"
       :description "Transparent header component"
       :props [[":size" "keyword, optional (default :full). One of: :full | :half"]
               [":border?" "boolean, optional - Render border styles"]
               [":sticky?" "boolean, optional - Sticky positioning"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "toggle-header-border"
                                             :description "Toggle header border component"
                                             :props []}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[base-header {}]"]]]]]]))

(defscene
 header-basic
 "Basic header with logo and right section.

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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