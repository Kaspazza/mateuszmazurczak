(ns mateuszmazurczak.portfolio.ui-components.navigation
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.navigation :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Navigation"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "navigation component."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.navigation)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/navigation.cljs"
            :filename "navigation.cljs"}])

(defscene api-reference
          "Complete reference for all Navigation component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Navigation components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "navigation"
                :description "Navigation component"
                :props [[":href" "any, optional - Component prop"]
                        [":text" "any, optional - Component prop"]
                        [":on-click" "any, optional - Component prop"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "back-navigation"
                :description "Back navigation component"
                :props [[":href" "any, optional - Component prop"]
                        [":text" "any, optional - Component prop"]
                        [":dark?" "any, optional - Component prop"]
                        [":on-click" "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[navigation {}]"]]]]]]))

(defscene
 navigation-forward
 "Forward navigation link.

  Custom component — not from shadcn/ui.
  Simple anchor for primary navigation.

  Use :href or :on-click for routing."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/navigation {:href "#"
                                                      :text "Go to Docs"}]]))

(defscene
 navigation-back
 "Back navigation link with arrow.

  Custom component — not from shadcn/ui.
  Useful for returning to parent pages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/back-navigation {:href "#"
                                                           :text "Back to Home"
                                                           :dark? false}]]))

(defscene
 navigation-dark-mode
 "Back navigation in dark mode variant.

  Custom component — not from shadcn/ui.
  Use :dark? true to switch text color for dark backgrounds."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-neutral-900"}
                                     [sut/back-navigation {:href "#"
                                                           :text "Back to Home"
                                                           :dark? true}]]))