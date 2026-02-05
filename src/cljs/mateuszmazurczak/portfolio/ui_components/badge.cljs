(ns mateuszmazurczak.portfolio.ui-components.badge
  (:require
   ["lucide-react"                         :refer [BadgeCheck]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge   :as sut]
   [mateuszmazurczak.ui.components.spinner :as spinner]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Badge"})

(defscene
 badge-demo
 "Badge variants and numeric indicators.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot (for :as-child polymorphism)

  Use badges for statuses, labels, and small counters."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-3"}
   [:div {:class "flex flex-wrap gap-2"}
    [sut/badge {}
     "Badge"]
    [sut/badge {:variant :secondary}
     "Secondary"]
    [sut/badge {:variant :destructive}
     "Destructive"]
    [sut/badge {:variant :outline}
     "Outline"]]
   [:div {:class "flex flex-wrap gap-2"}
    [sut/badge {:variant :secondary
                :class "bg-blue-500 text-white"}
     [:> BadgeCheck]
     "Verified"]
    [sut/badge {:class "h-5 min-w-5 rounded-full px-1 font-mono tabular-nums"}
     "8"]
    [sut/badge {:variant :destructive
                :class "h-5 min-w-5 rounded-full px-1 font-mono tabular-nums"}
     "99"]
    [sut/badge {:variant :outline
                :class "h-5 min-w-5 rounded-full px-1 font-mono tabular-nums"}
     "20+"]]]))

(defscene
 badge-outline
 "Outlined badge for neutral tags.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Outline badges work well for metadata or filters."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:variant :outline}
                                      "Outline"]]))

(defscene
 badge-secondary
 "Secondary badge for low-emphasis labels.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Use :secondary for de-emphasized categories."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:variant :secondary}
                                      "Secondary"]]))

(defscene
 badge-destructive
 "Destructive badge for error states.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Use :destructive for failed or blocked statuses."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:variant :destructive}
                                      "Destructive"]]))

(defscene
 spinner-badge
 "Badges paired with inline spinners.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Combine spinners with badges to show background activity."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex flex-wrap items-center gap-4"}
                                     [sut/badge {}
                                      [spinner/spinner {:class "size-4"}]
                                      "Syncing"]
                                     [sut/badge {:variant :secondary}
                                      [spinner/spinner {:class "size-4"}]
                                      "Updating"]
                                     [sut/badge {:variant :outline}
                                      [spinner/spinner {:class "size-4"}]
                                      "Processing"]]))

(defscene
 badge-as-child
 "Badge rendered as a link via :as-child.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Our wrapper supports :as-child to render anchors or buttons with badge styles."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:as-child true}
                                      [:a {:href "#"
                                           :class "inline-flex items-center gap-1"}
                                       "View status"]]]))