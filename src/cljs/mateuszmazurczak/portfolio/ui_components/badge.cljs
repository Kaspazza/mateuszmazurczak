(ns mateuszmazurczak.portfolio.ui-components.badge
  (:require
   ["lucide-react"                         :refer [BadgeCheck Bookmark]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge   :as sut]
   [mateuszmazurczak.ui.components.spinner :as spinner]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Badge"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Badge component with support for multiple variants."
            :npm-install "npm install @radix-ui/react-slot"
            :source-code (embed-source mateuszmazurczak.ui.components.badge)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/badge.cljs"
            :filename "badge.cljs"}])

(defscene
 api-reference
 "Complete reference for all Badge component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Badge components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "badge"
       :description "Badge component"
       :props
       [[":variant"
         "keyword, optional (default :default). One of: :default | :secondary | :destructive | :outline | :ghost | :link"]
        [":class" "string, optional - Additional Tailwind classes"]
        [":as-child" "boolean, optional (default false) - Use Radix Slot polymorphism"]
        [":on-click" "fn, optional - Click handler"]
        [":...dom-props" "map entries, optional - Forwarded to the rendered element"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[badge {:variant :outline} \"Outline\"]"]]]]]]))

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
     "Outline"]
    [sut/badge {:variant :ghost}
     "Ghost"]
    [sut/badge {:variant :link}
     [:a {:href "#"}
      "Link"]]]
   [:div {:class "flex flex-wrap gap-2"}
    [sut/badge {:variant :secondary
                :class "bg-blue-500 text-white"}
     [:> BadgeCheck {:data-icon "inline-start"}]
     "Verified"]
    [sut/badge {:variant :outline}
     "Bookmark"
     [:> Bookmark {:data-icon "inline-end"}]]
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
 badge-ghost
 "Ghost badge with no background.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Use :ghost for minimal emphasis badges."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:variant :ghost}
                                      "Ghost"]]))

(defscene
 badge-link
 "Link-styled badge with underline on hover.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Use :link for clickable text-style badges."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/badge {:variant :link}
                                      [:a {:href "#"}
                                       "Link"]]]))

(defscene
 spinner-badge
 "Badges paired with inline spinners.

  Based on shadcn/ui Badge — https://ui.shadcn.com/docs/components/badge
  Radix primitive: @radix-ui/react-slot

  Combine spinners with badges to show background activity."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex flex-wrap items-center gap-4"}
                                     [sut/badge {}
                                      [spinner/spinner {:class "size-4"
                                                        :data-icon "inline-start"}]
                                      "Syncing"]
                                     [sut/badge {:variant :secondary}
                                      [spinner/spinner {:class "size-4"
                                                        :data-icon "inline-start"}]
                                      "Updating"]
                                     [sut/badge {:variant :outline}
                                      "Processing"
                                      [spinner/spinner {:class "size-4"
                                                        :data-icon "inline-end"}]]]))

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
