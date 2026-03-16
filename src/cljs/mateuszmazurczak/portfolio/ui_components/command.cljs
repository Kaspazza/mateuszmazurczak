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
 "Complete reference for all Command component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Command components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "command"
       :description "Root cmdk container for searchable command surfaces. Additional props are forwarded to cmdk Command root."
       :props [[":value" "string, optional - Controlled search query value."]
               [":onValueChange" "function, optional - Called when query changes: (fn [value] ...)."]
               [":filter" "function, optional - Custom cmdk filter function: (fn [value search keywords] score)."]
               [":shouldFilter" "boolean, optional (default true) - Enables/disables built-in filtering."]
               [":loop" "boolean, optional (default false) - Keyboard navigation loops from last to first."]
               [":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Command root."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-dialog"
       :description "Dialog wrapper combining Dialog + Command root. Useful for global command palettes."
       :props [[":open" "boolean, optional - Controlled dialog open state."]
               [":defaultOpen" "boolean, optional - Uncontrolled initial open state."]
               [":onOpenChange" "function, optional - Callback for open state changes: (fn [open?] ...)."]
               [":modal" "boolean, optional - Whether dialog is modal."]
               [":title" "string, optional (default \"Command Palette\") - Accessible dialog title."]
               [":description" "string, optional (default \"Search for a command to run...\") - Accessible dialog description."]
               [":showCloseButton" "boolean, optional (default true) - Shows/hides close button in dialog content."]
               [":class" "string, optional - Additional Tailwind classes for dialog content."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-input"
       :description "Search input field with icon, rendered via cmdk Input primitive."
       :props [[":placeholder" "string, optional - Input placeholder."]
               [":value" "string, optional - Controlled input value."]
               [":onValueChange" "function, optional - Callback when value changes."]
               [":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Input."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-list"
       :description "Scrollable list container for command groups and items."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk List."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-empty"
       :description "Empty-state content rendered when query has no matching results."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Empty."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-group"
       :description "Groups related command items and optionally renders a heading."
       :props [[":heading" "string | hiccup, optional - Group heading label."]
               [":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Group."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-separator"
       :description "Visual separator between command groups."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Separator."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-item"
       :description "Selectable command option supporting keyboard and pointer interactions."
       :props [[":value" "string, optional - Explicit search/select value (otherwise derived from text content)."]
               [":onSelect" "function, optional - Called when item is selected: (fn [value] ...)."]
               [":disabled" "boolean, optional - Disables selection."]
               [":keywords" "vector<string>, optional - Extra search aliases for matching."]
               [":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to cmdk Item."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "command-shortcut"
       :description "Visual hint for keyboard shortcut displayed on the right side of an item."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to underlying span."]]}]
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
       [:code "[command {:class \"rounded-lg border\"}\n  [command-input {:placeholder \"Search actions...\"}]\n  [command-list {}\n    [command-empty {} \"No results.\"]\n    [command-group {:heading \"Actions\"}\n      [command-item {:value \"new-project\"\n                     :onSelect #(js/console.log %)}\n        [:span \"New project\"]\n        [command-shortcut {} \"⌘N\"]]]]]"]]
      [:div {:class "flex flex-wrap gap-2 mt-3"}
       [:a {:href "https://cmdk.paco.me"
            :target "_blank"
            :rel "noopener noreferrer"
            :class "inline-flex items-center text-sm text-primary hover:underline"}
        "cmdk Docs →"]
       [:a {:href "https://ui.shadcn.com/docs/components/command"
            :target "_blank"
            :rel "noopener noreferrer"
            :class "inline-flex items-center text-sm text-primary hover:underline"}
        "shadcn Command Docs →"]]]]]]))

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