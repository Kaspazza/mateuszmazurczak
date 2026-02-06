(ns mateuszmazurczak.portfolio.ui-components.separator
  (:require
   ["lucide-react"                            :refer [Slash]]
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.breadcrumb :as breadcrumb]
   [mateuszmazurczak.ui.components.separator  :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Separator"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Separator component for visual dividers."
            :npm-install "npm install @radix-ui/react-separator"
            :source-code (embed-source mateuszmazurczak.ui.components.separator)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/separator.cljs"
            :filename "separator.cljs"}])

(defscene api-reference
          "Complete reference for all Separator component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Separator components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "separator"
                :description "Separator component"
                :props [[":class" "string, optional - Additional Tailwind classes"]
                        [":orientation" "keyword, optional (default :horizontal). One of: :horizontal | :vertical"]
                        [":decorative" "boolean, optional (default true) - Decorative vs semantic separator"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[separator {}]"]]]]]]))

(defscene
 separator-demo
 "Horizontal and vertical separators.

  Based on shadcn/ui Separator — https://ui.shadcn.com/docs/components/separator
  Radix primitive: @radix-ui/react-separator

  Use separators to divide sections or inline items."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-4"}
                                     [:div {:class "space-y-1"}
                                      [:h4 {:class "text-sm font-medium"}
                                       "Radix Primitives"]
                                      [:p {:class "text-muted-foreground text-sm"}
                                       "An open-source UI component library."]]
                                     [sut/separator {:class "my-4"}]
                                     [:div {:class "flex h-5 items-center space-x-4 text-sm"}
                                      [:div "Blog"]
                                      [sut/separator {:orientation :vertical}]
                                      [:div "Docs"]
                                      [sut/separator {:orientation :vertical}]
                                      [:div "Source"]]]))

(defscene
 breadcrumb-separator
 "Separator used inside breadcrumb navigation.

  Based on shadcn/ui Breadcrumb Separator — https://ui.shadcn.com/docs/components/breadcrumb
  Radix primitive: @radix-ui/react-separator

  Custom separators can be inserted between breadcrumb items."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [breadcrumb/breadcrumb {}
                                      [breadcrumb/breadcrumb-list {}
                                       [breadcrumb/breadcrumb-item {}
                                        [breadcrumb/breadcrumb-link {:href "#"}
                                         "Home"]]
                                       [breadcrumb/breadcrumb-separator {}
                                        [:> Slash]]
                                       [breadcrumb/breadcrumb-item {}
                                        [breadcrumb/breadcrumb-link {:href "#"}
                                         "Components"]]
                                       [breadcrumb/breadcrumb-separator {}
                                        [:> Slash]]
                                       [breadcrumb/breadcrumb-item {}
                                        [breadcrumb/breadcrumb-page {}
                                         "Separator"]]]]]))

(defscene
 separator-custom
 "Separator with custom styling.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-separator

  Add classes to adjust thickness or color."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-3"}
                                     [:p {:class "text-sm"}
                                      "Primary accent"]
                                     [sut/separator {:class "bg-primary h-[2px]"}]
                                     [:p {:class "text-sm"}
                                      "Muted divider"]
                                     [sut/separator {:class "bg-muted h-[2px]"}]]))