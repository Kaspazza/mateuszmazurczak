(ns mateuszmazurczak.portfolio.ui-components.skeleton
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.skeleton :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Skeleton"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Skeleton component for loading placeholders."
            :npm-install "No external dependencies"
            :source-code (embed-source "mateuszmazurczak.ui.components.skeleton")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/skeleton.cljs"
            :filename "skeleton.cljs"}])

(defscene api-reference
          "Complete reference for all Skeleton component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Skeleton components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "skeleton"
                :description "Animated loading placeholder block. Additional props are forwarded to underlying <div>."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name ":role"           :type "string"      :default nil :description "Accessibility role, e.g. \"status\"."}
                        {:name ":aria-label"     :type "string"      :default nil :description "Screen reader label for loading context."}
                        {:name ":aria-live"      :type "string"      :default nil :description "Announce updates politely/assertively."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to native <div>."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Skeleton is purely visual by default; add ARIA attributes when used as a status indicator."]
                [:li "Use realistic width/height classes to reduce layout shift during content hydration."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:div {:class \"space-y-2 w-64\"}\n [skeleton {:class \"h-4 w-40\"}]\n [skeleton {:class \"h-4 w-full\"}]\n [skeleton {:class \"h-4 w-5/6\" :role \"status\" :aria-label \"Loading profile\"}]]" ]]]]]]))

(defscene
 skeleton-demo
 "Avatar + text skeleton layout.

  Native element: <div>

  Use for loading placeholders while data is fetched."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex items-center space-x-4"}
                                      [sut/skeleton {:class "h-12 w-12 rounded-full"}]
                                      [:div {:class "space-y-2"}
                                       [sut/skeleton {:class "h-4 w-[250px]"}]
                                       [sut/skeleton {:class "h-4 w-[200px]"}]]]]))

(defscene
 skeleton-card
 "Card-like skeleton placeholder.

  Native element: <div>

  Use for cards, previews, or media blocks."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex flex-col space-y-3"}
                                      [sut/skeleton {:class "h-[125px] w-[250px] rounded-xl"}]
                                      [:div {:class "space-y-2"}
                                       [sut/skeleton {:class "h-4 w-[250px]"}]
                                       [sut/skeleton {:class "h-4 w-[200px]"}]]]]))

(defscene
 skeleton-grid
 "Multi-column skeleton grid.

  Native element: <div>

  Useful for list or gallery loading states."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 grid gap-4 sm:grid-cols-3"}
                                     (for [idx (range 6)]
                                       ^{:key idx}
                                       [sut/skeleton {:class "h-24 w-full rounded-lg"}])]))