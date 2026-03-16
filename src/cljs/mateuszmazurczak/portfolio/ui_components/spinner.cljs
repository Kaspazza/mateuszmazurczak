(ns mateuszmazurczak.portfolio.ui-components.spinner
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge   :as badge]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.spinner :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Spinner"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Spinner component for loading states."
            :npm-install "npm install lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.spinner")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/spinner.cljs"
            :filename "spinner.cljs"}])

(defscene api-reference
          "Complete reference for all Spinner component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Spinner components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "spinner"
                :description "Lucide Loader2-based spinner with built-in accessibility defaults."
                :props [[":class" "string, optional - Additional CSS classes."]
                        [":role" "string, optional (default \"status\") - Accessibility role."]
                        [":aria-label" "string, optional (default \"Loading\") - Screen reader label."]
                        ["additional props" "map entries, optional - Forwarded to underlying icon element."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Default icon size is size-4; override with :class for larger/smaller spinners."]
                [:li "Spinner is purely presentational—pair with status text in long-running operations."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:div {:class \"flex items-center gap-2\"}\n [spinner {:class \"size-4\"}]\n [:span \"Loading data...\"]]" ]]]]]]))

(defscene
 spinner-basic
 "Basic spinner indicator.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Use for lightweight loading indicators."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/spinner {}]]))

(defscene
 spinner-size
 "Spinner size variants using class overrides.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Adjust size via Tailwind size classes."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-6"}
                                     [sut/spinner {:class "size-3"}]
                                     [sut/spinner {:class "size-4"}]
                                     [sut/spinner {:class "size-6"}]
                                     [sut/spinner {:class "size-8"}]]))

(defscene
 spinner-button
 "Spinner inside disabled buttons.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Combine with buttons to show in-progress actions."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex flex-col items-start gap-4"}
                                     (button/button {:disabled true
                                                     :size :sm}
                                                    [sut/spinner {}]
                                                    "Loading...")
                                     (button/button {:variant :outline
                                                     :disabled true
                                                     :size :sm}
                                                    [sut/spinner {}]
                                                    "Please wait")
                                     (button/button {:variant :secondary
                                                     :disabled true
                                                     :size :sm}
                                                    [sut/spinner {}]
                                                    "Processing")]))

(defscene
 spinner-badge
 "Spinner embedded in badges.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Useful for background status updates."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-4"}
                                     [badge/badge {}
                                      [sut/spinner {}]
                                      "Syncing"]
                                     [badge/badge {:variant :secondary}
                                      [sut/spinner {}]
                                      "Updating"]
                                     [badge/badge {:variant :outline}
                                      [sut/spinner {}]
                                      "Processing"]]))

(defscene
 spinner-color
 "Spinner color variations.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Color via text utility classes."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-6"}
                                     [sut/spinner {:class "size-6 text-red-500"}]
                                     [sut/spinner {:class "size-6 text-green-500"}]
                                     [sut/spinner {:class "size-6 text-blue-500"}]
                                     [sut/spinner {:class "size-6 text-yellow-500"}]
                                     [sut/spinner {:class "size-6 text-purple-500"}]]))

(defscene
 spinner-demo
 "Spinner inside list item layout.

  Based on shadcn/ui Spinner — https://ui.shadcn.com/docs/components/spinner
  Icon: lucide-react Loader2

  Demonstrates inline usage in a row layout."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [:div {:class
          "flex w-full max-w-xs items-center justify-between rounded-lg border bg-muted/50 p-4"}
    [:div {:class "flex items-center gap-3"}
     [sut/spinner {}]
     [:div {:class "text-sm font-medium"}
      "Processing payment..."]]
    [:span {:class "text-sm tabular-nums"}
     "$100.00"]]]))