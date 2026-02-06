(ns mateuszmazurczak.portfolio.ui-components.tooltip
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.tooltip :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Tooltip"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Self-contained tooltip component for displaying contextual information on hover/focus."
            :npm-install "npm install @radix-ui/react-tooltip"
            :source-code (embed-source mateuszmazurczak.ui.components.tooltip)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/tooltip.cljs"
            :filename "tooltip.cljs"}])

(defscene api-reference
          "Complete reference for all Tooltip component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Tooltip components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "tooltip"
                :description "Tooltip component"
                :props [[":trigger" "any, optional - Component prop"]
                        [":content" "any, optional - Component prop"]
                        [":side" "any, optional - Component prop"]
                        [":side-offset" "any, optional - Component prop"]
                        [":align" "any, optional - Component prop"]
                        [":align-offset" "any, optional - Component prop"]
                        [":collision-padding" "any, optional - Component prop"]
                        [":avoid-collisions?" "any, optional - Component prop"]
                        [":sticky" "any, optional - Component prop"]
                        [":delay-duration" "any, optional - Component prop"]
                        [":skip-delay-duration" "any, optional - Component prop"]
                        [":open" "any, optional - Component prop"]
                        [":default-open" "any, optional - Component prop"]
                        [":on-open-change" "any, optional - Component prop"]
                        [":content-class" "any, optional - Component prop"]
                        [":content-hidden?" "any, optional - Component prop"]
                        [":trigger-as-child?" "any, optional - Component prop"]
                        [":disable-hoverable-content?" "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[tooltip {}]"]]]]]]))

(defscene
 tooltip-demo
 "Basic tooltip with a button trigger.

  Based on shadcn/ui Tooltip — https://ui.shadcn.com/docs/components/tooltip
  Radix primitive: @radix-ui/react-tooltip

  Use tooltips for concise, contextual hints."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/tooltip {:trigger [button/button {:variant :outline}
                                                             "Hover"]
                                                   :content "Add to library"}]]))

(defscene
 kbd-tooltip
 "Tooltip with keyboard shortcut hints.

  Based on shadcn/ui Tooltip — https://ui.shadcn.com/docs/components/tooltip
  Radix primitive: @radix-ui/react-tooltip

  Use inline <kbd> tags to show shortcuts."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 flex flex-wrap gap-4"}
   [sut/tooltip
    {:trigger [button/button {:size :sm
                              :variant :outline}
               "Save"]
     :content
     [:div {:class "flex items-center gap-2"}
      "Save Changes"
      [:kbd
       {:class
        "bg-muted text-muted-foreground inline-flex h-5 items-center rounded border px-1.5 font-mono text-[10px]"}
       "S"]]}]
   [sut/tooltip
    {:trigger [button/button {:size :sm
                              :variant :outline}
               "Print"]
     :content
     [:div {:class "flex items-center gap-2"}
      "Print Document"
      [:span {:class "flex items-center gap-1"}
       [:kbd
        {:class
         "bg-muted text-muted-foreground inline-flex h-5 items-center rounded border px-1.5 font-mono text-[10px]"}
        "Ctrl"]
       [:kbd
        {:class
         "bg-muted text-muted-foreground inline-flex h-5 items-center rounded border px-1.5 font-mono text-[10px]"}
        "P"]]]}]]))

(defscene
 tooltip-custom-content
 "Tooltip with rich content.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-tooltip

  Rich content works well for onboarding hints."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/tooltip {:trigger [button/button {:variant :ghost}
                                                             "Hover for info"]
                                                   :content [:div {:class "space-y-1"}
                                                             [:p {:class "font-semibold"}
                                                              "Pro tip"]
                                                             [:p {:class
                                                                  "text-xs text-muted-foreground"}
                                                              "Use ⌘K to search across projects."]]
                                                   :content-class "max-w-xs"}]]))