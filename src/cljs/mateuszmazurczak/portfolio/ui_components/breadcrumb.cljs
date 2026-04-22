(ns mateuszmazurczak.portfolio.ui-components.breadcrumb
  (:require
   ["lucide-react"                               :refer [ChevronDown Slash]]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.breadcrumb    :as sut]
   [mateuszmazurczak.ui.components.drawer        :as drawer]
   [mateuszmazurczak.ui.components.dropdown-menu :as dropdown-menu]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]
   [reagent.core                                 :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Breadcrumb"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Breadcrumb navigation component for displaying hierarchical page location."
            :npm-install "npm install @radix-ui/react-slot lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.breadcrumb")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/breadcrumb.cljs"
            :filename "breadcrumb.cljs"}])

(defscene api-reference
          "Complete reference for all Breadcrumb component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Breadcrumb components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb"
                :description "Root nav wrapper for hierarchical page location trail."
                :props [{:name ":class"     :type "string"              :default nil :description "Additional Tailwind classes."}
                        {:name ":separator" :type "hiccup/component"    :default nil :description "Reserved custom separator prop on root (children separators still control rendering)."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-list"
                :description "Ordered list container for breadcrumb items."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-item"
                :description "List item wrapper for a single crumb segment."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-link"
                :description "Navigable breadcrumb segment."
                :props [{:name ":href"     :type "string"  :default nil     :description "Destination URL."}
                        {:name ":as-child" :type "boolean" :default "false" :description "Render via Radix Slot."}
                        {:name ":class"    :type "string"  :default nil     :description "Additional Tailwind classes."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-page"
                :description "Current-page segment (non-clickable) with aria-current semantics."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-separator"
                :description "Visual separator between segments; renders ChevronRight by default, or custom children when provided."
                :props [{:name ":class"    :type "string" :default nil :description "Additional Tailwind classes."}
                        {:name "children" :type nil       :default nil :description "Custom separator content; overrides default icon."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-ellipsis"
                :description "Collapsed-path indicator used when intermediate crumbs are hidden."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "breadcrumb-page sets aria-current=page and should represent the final/current segment."]
                [:li "Use children in breadcrumb-separator to override the default ChevronRight icon."]
                [:li "For long paths, pair breadcrumb-ellipsis with dropdown/drawer to reveal hidden ancestors."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[breadcrumb {}\n [breadcrumb-list {}\n  [breadcrumb-item {} [breadcrumb-link {:href \"/\"} \"Home\"]]\n  [breadcrumb-separator {}]\n  [breadcrumb-item {} [breadcrumb-page {} \"Settings\"]]]]" ]]]]]]))

(defscene
 breadcrumb-demo
 "Breadcrumb with ellipsis dropdown in the middle.

  Radix primitives: @radix-ui/react-dropdown-menu, @radix-ui/react-separator

  Useful for long navigation paths."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/breadcrumb {}
    [sut/breadcrumb-list {}
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"}
       "Home"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [dropdown-menu/dropdown-menu {}
       [dropdown-menu/dropdown-menu-trigger {:class "flex items-center gap-1 cursor-pointer"}
        [sut/breadcrumb-ellipsis {:class "size-4"}]
        [:span {:class "sr-only"}
         "Toggle menu"]]
       [dropdown-menu/dropdown-menu-content {:align "start"}
        [dropdown-menu/dropdown-menu-item {:class "cursor-pointer"}
         "Documentation"]
        [dropdown-menu/dropdown-menu-item {:class "cursor-pointer"}
         "Themes"]
        [dropdown-menu/dropdown-menu-item {:class "cursor-pointer"}
         "GitHub"]]]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"}
       "Components"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-page {}
       "Breadcrumb"]]]]]))

(defscene
 breadcrumb-simple
 "Simple breadcrumb link example.

  Radix primitive: @radix-ui/react-separator

  Use breadcrumb-link for navigable segments and breadcrumb-page for current."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/breadcrumb {}
                                      [sut/breadcrumb-list {}
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-link {:href "#"}
                                         "Home"]]
                                       [sut/breadcrumb-separator {}]
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-link {:href "#"}
                                         "Components"]]
                                       [sut/breadcrumb-separator {}]
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-page {}
                                         "Breadcrumb"]]]]]))

(defscene
 breadcrumb-ellipsis
 "Collapsed breadcrumb using ellipsis.

  Radix primitives: @radix-ui/react-separator

  Use the ellipsis when intermediate items are hidden."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/breadcrumb {}
    [sut/breadcrumb-list {}
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"}
       "Home"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-ellipsis {}]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"}
       "Components"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-page {}
       "Breadcrumb"]]]]]))

(defscene
 breadcrumb-separator
 "Breadcrumb with custom separator icon.

  Radix primitives: @radix-ui/react-separator

  Custom separators can be inserted per segment."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/breadcrumb {}
                                      [sut/breadcrumb-list {}
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-link {:href "#"}
                                         "Home"]]
                                       [sut/breadcrumb-separator {}
                                        [:> Slash]]
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-link {:href "#"}
                                         "Components"]]
                                       [sut/breadcrumb-separator {}
                                        [:> Slash]]
                                       [sut/breadcrumb-item {}
                                        [sut/breadcrumb-page {}
                                         "Breadcrumb"]]]]]))

(defscene
 breadcrumb-dropdown
 "Breadcrumb with dropdown menu item.

  Radix primitives: @radix-ui/react-dropdown-menu, @radix-ui/react-separator

  Dropdowns can replace intermediate links."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/breadcrumb {}
    [sut/breadcrumb-list {}
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"}
       "Home"]]
     [sut/breadcrumb-separator {}
      [:> Slash]]
     [sut/breadcrumb-item {}
      [dropdown-menu/dropdown-menu {}
       [dropdown-menu/dropdown-menu-trigger {:as-child true}
        [:button {:class "flex items-center gap-1"}
         "Components"
         [:> ChevronDown {:class "size-3.5"}]]]
       [dropdown-menu/dropdown-menu-content {:align "start"}
        [dropdown-menu/dropdown-menu-item {}
         "Documentation"]
        [dropdown-menu/dropdown-menu-item {}
         "Themes"]
        [dropdown-menu/dropdown-menu-item {}
         "GitHub"]]]]
     [sut/breadcrumb-separator {}
      [:> Slash]]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-page {}
       "Breadcrumb"]]]]]))



(defscene
 breadcrumb-responsive
 "Responsive breadcrumb using dropdown or drawer.

  Radix primitives: @radix-ui/react-dropdown-menu, @radix-ui/react-dialog

  This example shows both desktop (dropdown) and mobile (drawer) patterns."
 []
 (let [dropdown-open? (r/atom false)
       drawer-open? (r/atom false)
       items [{:href "#"
               :label "Home"}
              {:href "#"
               :label "Documentation"}
              {:href "#"
               :label "Building Your Application"}
              {:href "#"
               :label "Data Fetching"}
              {:label "Caching and Revalidating"}]]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 space-y-6"}
       [:div {:class "hidden md:block"}
        [sut/breadcrumb {}
         [sut/breadcrumb-list {}
          [sut/breadcrumb-item {}
           [sut/breadcrumb-link {:href (:href (first items))}
            (:label (first items))]]
          [sut/breadcrumb-separator {}]
          [sut/breadcrumb-item {}
           [dropdown-menu/dropdown-menu {:open @dropdown-open?
                                         :on-open-change #(reset! dropdown-open? %)}
            [dropdown-menu/dropdown-menu-trigger {:as-child true}
             [:button {:class "flex items-center gap-1"
                       :aria-label "Toggle menu"}
              [sut/breadcrumb-ellipsis {}]]]
            [dropdown-menu/dropdown-menu-content {:align "start"}
             (for [{:keys [href label]} (subvec (vec items) 1 3)]
               ^{:key label}
               [dropdown-menu/dropdown-menu-item {}
                [:a {:href (or href "#")}
                 label]])]]]
          [sut/breadcrumb-separator {}]
          [sut/breadcrumb-item {}
           [sut/breadcrumb-link {:href "#"
                                 :class "max-w-20 truncate"}
            "Data Fetching"]]
          [sut/breadcrumb-separator {}]
          [sut/breadcrumb-item {}
           [sut/breadcrumb-page {:class "max-w-20 truncate"}
            "Caching and Revalidating"]]]]]
       [:div {:class "md:hidden"}
        [sut/breadcrumb {}
         [sut/breadcrumb-list {}
          [sut/breadcrumb-item {}
           [sut/breadcrumb-link {:href (:href (first items))}
            (:label (first items))]]
          [sut/breadcrumb-separator {}]
          [sut/breadcrumb-item {}
           [drawer/drawer {:open @drawer-open?
                           :on-open-change #(reset! drawer-open? %)}
            [drawer/drawer-trigger {:class "cursor-pointer"
                                    :aria-label "Toggle menu"}
             [sut/breadcrumb-ellipsis {}]]
            [drawer/drawer-content {}
             [drawer/drawer-header {:class "text-left"}
              [drawer/drawer-title {}
               "Navigate to"]
              [drawer/drawer-description {}
               "Select a page to navigate to."]]
             [:div {:class "grid gap-1 px-4"}
              (for [{:keys [href label]} (subvec (vec items) 1 3)]
                ^{:key label}
                [:a {:href (or href "#")
                     :class "py-1 text-sm"}
                 label])]
             [drawer/drawer-footer {:class "pt-4"}
              [drawer/drawer-close
               {:class
                "inline-flex h-10 w-full items-center justify-center rounded-md border border-input bg-background px-4 py-2 text-sm font-medium ring-offset-background transition-colors hover:bg-accent hover:text-accent-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"}
               "Close"]]]]]]]]]))))

