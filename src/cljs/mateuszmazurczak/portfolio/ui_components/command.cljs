(ns mateuszmazurczak.portfolio.ui-components.command
  (:require
   ["lucide-react"                         :refer [Calendar CreditCard Settings Smile User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.command :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Command"})

(defscene
 command-demo
 "Command list with groups, separators, and disabled items.

  Based on shadcn/ui Command — https://ui.shadcn.com/docs/components/command
  Library: cmdk

  Use Command for searchable lists and quick actions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/command {:class "rounded-lg border shadow-md md:min-w-[450px]"}
    [sut/command-input {:placeholder "Type a command or search..."}]
    [sut/command-list {}
     [sut/command-empty {}
      "No results found."]
     [sut/command-group {:heading "Suggestions"}
      [sut/command-item {}
       [:> Calendar]
       [:span "Calendar"]]
      [sut/command-item {}
       [:> Smile]
       [:span "Search Emoji"]]
      [sut/command-item {:disabled true}
       [:> CreditCard]
       [:span "Calculator"]]]
     [sut/command-separator {}]
     [sut/command-group {:heading "Settings"}
      [sut/command-item {}
       [:> User]
       [:span "Profile"]]
      [sut/command-item {}
       [:> Settings]
       [:span "Settings"]]]]]]))

(defscene
 command-dialog
 "Command dialog with open state.

  Based on shadcn/ui Command Dialog — https://ui.shadcn.com/docs/components/command
  Library: cmdk

  Useful for global search triggered from a button or shortcut."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-3"}
   [:p {:class "text-muted-foreground text-sm"}
    "Command dialog rendered in an open state for showcase."]
   [sut/command-dialog {:open true
                        :title "Quick Actions"}
    [sut/command-input {:placeholder "Type a command or search..."}]
    [sut/command-list {}
     [sut/command-empty {}
      "No results found."]
     [sut/command-group {:heading "Suggestions"}
      [sut/command-item {}
       [:> Calendar]
       [:span "Calendar"]]
      [sut/command-item {}
       [:> Smile]
       [:span "Search Emoji"]]
      [sut/command-item {}
       [:> CreditCard]
       [:span "Calculator"]]]
     [sut/command-separator {}]
     [sut/command-group {:heading "Settings"}
      [sut/command-item {}
       [:> User]
       [:span "Profile"]]
      [sut/command-item {}
       [:> Settings]
       [:span "Settings"]]]]]]))

(defscene
 command-composition
 "Command embedded in a card-like container.

  Custom example — not from shadcn/ui.
  Library: cmdk

  Demonstrates how Command can be styled to match surrounding UI."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [:div {:class "rounded-lg border bg-card p-4 shadow-sm"}
                                      [:p {:class "text-sm font-medium mb-2"}
                                       "Quick Actions"]
                                      [sut/command {}
                                       [sut/command-input {:placeholder "Filter actions..."}]
                                       [sut/command-list {}
                                        [sut/command-item {}
                                         "Create project"]
                                        [sut/command-item {}
                                         "Invite teammate"]
                                        [sut/command-item {}
                                         "Open settings"]]]]]))