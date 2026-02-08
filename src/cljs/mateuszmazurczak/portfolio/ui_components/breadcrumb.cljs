(ns mateuszmazurczak.portfolio.ui-components.breadcrumb
  (:require
   ["lucide-react"                               :refer [ChevronDown Slash]]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.breadcrumb    :as sut]
   [mateuszmazurczak.ui.components.button        :as button]
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
            :source-code (embed-source mateuszmazurczak.ui.components.breadcrumb)
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
                :description "Breadcrumb component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-list"
                :description "Breadcrumb list component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-item"
                :description "Breadcrumb item component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-link"
                :description "Breadcrumb link component"
                :props [[":href" "string, optional - Link URL"]
                        [":class" "string, optional - Additional Tailwind classes"]
                        [":as-child" "boolean, optional (default false) - Render via Radix Slot"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-page"
                :description "Breadcrumb page component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-separator"
                :description "Breadcrumb separator component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "breadcrumb-ellipsis"
                :description "Breadcrumb ellipsis component"
                :props [[":class" "string, optional - Additional Tailwind classes"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[breadcrumb {}]"]]]]]]))

(defscene
 breadcrumb-demo
 "Breadcrumb with ellipsis dropdown in the middle.

  Based on shadcn/ui Breadcrumb — https://ui.shadcn.com/docs/components/breadcrumb
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

  Based on shadcn/ui Breadcrumb — https://ui.shadcn.com/docs/components/breadcrumb
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

  Based on shadcn/ui Breadcrumb — https://ui.shadcn.com/docs/components/breadcrumb
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

  Based on shadcn/ui Breadcrumb — https://ui.shadcn.com/docs/components/breadcrumb
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

  Based on shadcn/ui Breadcrumb — https://ui.shadcn.com/docs/components/breadcrumb
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

  Based on shadcn/ui Breadcrumb Responsive — https://ui.shadcn.com/docs/components/breadcrumb
  Radix primitives: @radix-ui/react-dropdown-menu, @radix-ui/react-dialog

  This example shows both desktop (dropdown) and mobile (drawer) patterns."
 []
 (let [open? (r/atom false)
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
       [sut/breadcrumb {}
        [sut/breadcrumb-list {}
         [sut/breadcrumb-item {}
          [sut/breadcrumb-link {:href (:href (first items))}
           (:label (first items))]]
         [sut/breadcrumb-separator {}]
         [sut/breadcrumb-item {}
          [dropdown-menu/dropdown-menu {:open @open?
                                        :on-open-change #(reset! open? %)}
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
           "Caching and Revalidating"]]]]
       [sut/breadcrumb {}
        [sut/breadcrumb-list {}
         [sut/breadcrumb-item {}
          [sut/breadcrumb-link {:href (:href (first items))}
           (:label (first items))]]
         [sut/breadcrumb-separator {}]
         [sut/breadcrumb-item {}
          [drawer/drawer {:open @open?
                          :on-open-change #(reset! open? %)}
           [drawer/drawer-trigger {}
            [:button {:aria-label "Toggle menu"}
             [sut/breadcrumb-ellipsis {}]]]
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
             (button/button {:variant :outline
                             :on-click #(reset! open? false)}
                            "Close")]]]]]]]))))

