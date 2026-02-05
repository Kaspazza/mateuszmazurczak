(ns mateuszmazurczak.portfolio.ui-components.tooltip
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.tooltip :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Tooltip"})

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