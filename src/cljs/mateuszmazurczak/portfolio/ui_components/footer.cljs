(ns mateuszmazurczak.portfolio.ui-components.footer
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.footer :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Footer"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Frontend based implementation of footer."
            :npm-install "npm install footer"
            :source-code (embed-source mateuszmazurczak.ui.components.footer)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/footer.cljs"
            :filename "footer.cljs"}])

(defscene api-reference
          "Complete reference for all Footer component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-4xl"}
                                              [:div {:class "space-y-6"}
                                               [:div
                                                [:p {:class "text-sm text-muted-foreground"}
                                                 "All available props for Footer components."]]
                                               [:div {:class "space-y-4"}
                                                [mm-portfolio-utils/api-component-card
                                                 {:component-name "footer"
                                                  :description "Footer component"
                                                  :props []}]
                                                [:div {:class "border rounded-lg p-4 bg-muted/50"}
                                                 [:h4 {:class "text-sm font-semibold mb-2"}
                                                  "Usage Example"]
                                                 [:pre {:class "text-xs overflow-x-auto"}
                                                  [:code "[footer {}]"]]]]]]))

(defscene
 footer-basic
 "Footer with social icons.

  Custom component — not from shadcn/ui.
  Simple footer layout for social links.

  This component is app-aware (theme-driven icons)."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-background"}
                                     [sut/footer]]))

(defscene
 footer-in-layout
 "Footer inside a page layout.

  Custom component — not from shadcn/ui.
  Demonstrates the footer anchoring the bottom of a page.

  Useful for marketing or landing pages."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 min-h-[240px] border rounded-md flex flex-col justify-between"}
   [:div {:class "space-y-2"}
    [:h3 {:class "text-lg font-semibold"}
     "Acme Product"]
    [:p {:class "text-sm text-muted-foreground"}
     "Build better workflows with a modern UI kit."]]
   [sut/footer]]))

(defscene
 footer-on-muted
 "Footer on muted background.

  Custom component — not from shadcn/ui.
  Shows how the footer reads on a tinted surface.

  Use for dashboards or side panels."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-muted rounded-md"}
                                     [sut/footer]]))