(ns mateuszmazurczak.portfolio.ui-components.spinner
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge   :as badge]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.spinner :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Spinner"})

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