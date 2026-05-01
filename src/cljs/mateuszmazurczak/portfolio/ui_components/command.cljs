(ns mateuszmazurczak.portfolio.ui-components.command
  (:require
   ["lucide-react"                         :refer [Calendar CreditCard Settings Smile User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.command :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Command"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Command palette component built on cmdk (Command Menu Dialog Kit)."
            :npm-install "npm install cmdk lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.command")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/command.cljs"
            :filename "command.cljs"}])

(defscene
 api-reference
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-4"}
    [mm-portfolio-utils/api-component-card
     {:component-name "command"
      :link {:href "https://cmdk.paco.me" :label "cmdk Docs"}
      :description "Root cmdk container for searchable command surfaces. Additional props are forwarded to cmdk Command root."
      :props [{:name ":value"           :type "string"      :default nil    :description "Controlled search query value."}
              {:name ":onValueChange"   :type "function"    :default nil    :description "Called when query changes: (fn [value] ...)."}
              {:name ":filter"          :type "function"    :default nil    :description "Custom cmdk filter function: (fn [value search keywords] score)."}
              {:name ":shouldFilter"    :type "boolean"     :default "true" :description "Enables/disables built-in filtering."}
              {:name ":loop"            :type "boolean"     :default "false" :description "Keyboard navigation loops from last to first."}
              {:name ":class"           :type "string"      :default nil    :description "Additional Tailwind classes."}
              {:name "additional props" :type "map entries" :default nil    :description "Forwarded to cmdk Command root."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-dialog"
       :description "Dialog wrapper combining Dialog + Command root. Useful for global command palettes."
       :props [{:name ":open"            :type "boolean" :default nil                              :description "Controlled dialog open state."}
               {:name ":defaultOpen"     :type "boolean" :default nil                              :description "Uncontrolled initial open state."}
               {:name ":onOpenChange"    :type "function" :default nil                             :description "Callback for open state changes: (fn [open?] ...)."}
               {:name ":modal"           :type "boolean" :default nil                              :description "Whether dialog is modal."}
               {:name ":title"           :type "string"  :default "\"Command Palette\""            :description "Accessible dialog title."}
               {:name ":description"     :type "string"  :default "\"Search for a command to run...\"" :description "Accessible dialog description."}
               {:name ":showCloseButton" :type "boolean" :default "true"                           :description "Shows/hides close button in dialog content."}
               {:name ":class"           :type "string"  :default nil                              :description "Additional Tailwind classes for dialog content."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-input"
       :description "Search input field with icon, rendered via cmdk Input primitive."
       :props [{:name ":placeholder"     :type "string"      :default nil :description "Input placeholder."}
               {:name ":value"           :type "string"      :default nil :description "Controlled input value."}
               {:name ":onValueChange"   :type "function"    :default nil :description "Callback when value changes."}
               {:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to cmdk Input."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-list"
       :description "Scrollable list container for command groups and items."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk List."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-empty"
       :description "Empty-state content rendered when query has no matching results."
       :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to cmdk Empty."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-group"
       :description "Groups related command items and optionally renders a heading."
       :props [{:name ":heading"         :type "string | hiccup" :default nil :description "Group heading label."}
               {:name ":class"           :type "string"          :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries"     :default nil :description "Forwarded to cmdk Group."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-separator"
       :description "Visual separator between command groups."
       :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to cmdk Separator."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-item"
       :description "Selectable command option supporting keyboard and pointer interactions."
       :props [{:name ":value"           :type "string"          :default nil :description "Explicit search/select value (otherwise derived from text content)."}
               {:name ":onSelect"        :type "function"        :default nil :description "Called when item is selected: (fn [value] ...)."}
               {:name ":disabled"        :type "boolean"         :default nil :description "Disables selection."}
               {:name ":keywords"        :type "vector<string>"  :default nil :description "Extra search aliases for matching."}
               {:name ":class"           :type "string"          :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries"     :default nil :description "Forwarded to cmdk Item."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-shortcut"
       :description "Visual hint for keyboard shortcut displayed on the right side of an item."
       :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to underlying span."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "cmdk uses camelCase prop names like :onValueChange and :onSelect (not kebab-case)."]
       [:li "command-shortcut is visual only; keyboard handling must be implemented separately."]
       [:li "For command-dialog accessibility, keep meaningful :title and :description values."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[command {:class \"rounded-lg border\"}\n  [command-input {:placeholder \"Search actions...\"}]\n  [command-list {}\n    [command-empty {} \"No results.\"]\n    [command-group {:heading \"Actions\"}\n      [command-item {:value \"new-project\"\n                     :onSelect #(js/console.log %)}\n        [:span \"New project\"]\n        [command-shortcut {} \"⌘N\"]]]]]"]]]]]]))

(defscene
 command-demo
 "Command list with groups, separators, and disabled items.

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