(ns mateuszmazurczak.portfolio.ui-components.theme-toggle
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.header       :as header]
   [mateuszmazurczak.ui.components.theme-toggle :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Theme Toggle"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Theme toggle component for switching between light and dark themes."
            :npm-install "npm install lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.theme_toggle")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/theme_toggle.cljs"
            :filename "theme_toggle.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "theme-toggle"
                :description "Zero-arity theme switch button. Reads current theme from app state and dispatches an event to toggle between light and dark modes."
                :props [{:name "arguments" :type "No props" :default nil :description "Render as [theme-toggle]."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "This component takes no props and should be rendered as [theme-toggle] (without props map)."]
                [:li "Internal behavior: subscribes to [:theme/current] and dispatches [:theme/toggle] on click."]
                [:li "Requires theme state/events wiring in your system (ports.state + ports.events adapters)."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:div {:class \"flex items-center justify-between\"}\n [:span {:class \"text-sm\"} \"Theme\"]\n [theme-toggle]]"]]]]]]))

(defscene
 theme-toggle-basic
 "Theme toggle button.
  Uses app theme state to toggle light/dark mode.

  Include in headers or settings panels."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/theme-toggle]]))

(defscene
 theme-toggle-in-header
 "Theme toggle inside a header.
  Demonstrates composition with header component."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-20 bg-background"}
                                     [header/header-comp {:size :full
                                                          :sticky? false
                                                          :border? true
                                                          :logo [:span {:class
                                                                        "text-sm font-semibold"}
                                                                 "Brand"]
                                                          :right-section [sut/theme-toggle]}
                                      {:title "Home"
                                       :href "#"}
                                      {:title "Docs"
                                       :href "#"}]]))

(defscene
 theme-toggle-settings-row
 "Theme toggle in a settings row.
  Shows the toggle alongside descriptive text.

  Useful for preference screens or settings panels."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-sm"}
   [:div {:class "flex items-center justify-between rounded-md border px-4 py-3"}
    [:div {:class "space-y-1"}
     [:p {:class "text-sm font-medium"}
      "Dark mode"]
     [:p {:class "text-xs text-muted-foreground"}
      "Switch theme for the application."]]
    [sut/theme-toggle]]]))