(ns mateuszmazurczak.portfolio.ui-components.command
  (:require
   ["lucide-react"                         :refer [Calendar CreditCard Settings Smile User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.command :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Command"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Command palette component built on cmdk (Command Menu Dialog Kit)."
            :npm-install "npm install cmdk lucide-react"
            :source-code (embed-source mateuszmazurczak.ui.components.command)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/command.cljs"
            :filename "command.cljs"}])

(defscene
 api-reference
 "Complete reference for all Command component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Command components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "command"
                                             :description "Command component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-dialog"
       :description "Command dialog component"
       :props [[":title" "string, optional (default 'Command Palette') - Dialog title"]
               [":description" "string, optional (default 'Search for a command to run...') - Dialog description"]
               [":class" "string, optional - Additional Tailwind classes"]
               [":showCloseButton" "boolean, optional (default true) - Show close button in dialog"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-input"
                                             :description "Command input component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-list"
                                             :description "Command list component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-empty"
                                             :description "Command empty component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-group"
                                             :description "Command group component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-separator"
                                             :description "Command separator component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-item"
                                             :description "Command item component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "command-shortcut"
                                             :description "Command shortcut component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[command {}]"]]]]]]))

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