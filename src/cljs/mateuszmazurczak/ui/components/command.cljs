(ns mateuszmazurczak.ui.components.command
  "Command palette component built on cmdk (Command Menu Dialog Kit).
  https://cmdk.paco.me/

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["cmdk"                                :refer [Command]]
   ["lucide-react"                        :refer [SearchIcon]]
   [mateuszmazurczak.ui.components.dialog :as dialog]
   [mateuszmazurczak.utils.styles         :refer [merge-classes]]))

(defn command
  "Root command component. Container for command palette.
  
  Props:
  - `:class` - Additional Tailwind classes
  - `:value` - Controlled search value
  - `:onValueChange` - Callback when search value changes (fn [value])
  - `:filter` - Custom filter function (fn [value search])
  - `:shouldFilter` - Whether to filter items (default: true)
  - `:loop` - Whether selection should loop (default: false)
  
  Example:
  [command {}
   [command-input {:placeholder \"Type a command...\"}]
   [command-list {}
    [command-empty {} \"No results found.\"]
    [command-group {:heading \"Suggestions\"}
     [command-item {} \"Calendar\"]
     [command-item {} \"Search Emoji\"]]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         Command
         (-> props
             (assoc :data-slot "command"
                    :class (merge-classes "bg-popover text-popover-foreground"
                                          "flex h-full w-full flex-col overflow-hidden rounded-md"
                                          class))
             (dissoc :class-name))]
        children))

(defn command-dialog
  "Command dialog component that wraps command in a modal dialog.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:onOpenChange` - Callback when open state changes (fn [open?])
  - `:title` - Dialog title for accessibility (default: \"Command Palette\")
  - `:description` - Dialog description for accessibility (default: \"Search for a command to run...\")
  - `:class` - Additional Tailwind classes for the dialog content
  - `:showCloseButton` - Show X close button (default: true)
  
  Example:
  [command-dialog {:open @open?
                   :onOpenChange #(reset! open? %)
                   :title \"Quick Actions\"}
   [command-input {:placeholder \"Search actions...\"}]
   [command-list {}
    [command-empty {} \"No actions found.\"]
    [command-group {:heading \"Actions\"}
     [command-item {:onSelect #(js/console.log \"save\")} \"Save\"]
     [command-item {:onSelect #(js/console.log \"delete\")} \"Delete\"]]]]"
  [{:keys [title description class showCloseButton]
    :or {title "Command Palette"
         description "Search for a command to run..."
         showCloseButton true}
    :as props}
   &
   children]
  [dialog/dialog
   (-> props
       (select-keys [:open :defaultOpen :onOpenChange :modal])
       (assoc :data-slot "command-dialog"))
   [dialog/dialog-header {:class "sr-only"}
    [dialog/dialog-title {}
     title]
    [dialog/dialog-description {}
     description]]
   (into [dialog/dialog-content {:class (merge-classes "overflow-hidden p-0" class)
                                 :showCloseButton showCloseButton}
          [command {:class (merge-classes "[&_[cmdk-group-heading]]:text-muted-foreground"
                                          "[&_[data-slot=command-input-wrapper]]:h-12"
                                          "[&_[cmdk-group-heading]]:px-2"
                                          "[&_[cmdk-group-heading]]:font-medium"
                                          "[&_[cmdk-group]]:px-2"
                                          "[&_[cmdk-group]:not([hidden])_~[cmdk-group]]:pt-0"
                                          "[&_[cmdk-input-wrapper]_svg]:h-5"
                                          "[&_[cmdk-input-wrapper]_svg]:w-5" "[&_[cmdk-input]]:h-12"
                                          "[&_[cmdk-item]]:px-2" "[&_[cmdk-item]]:py-3"
                                          "[&_[cmdk-item]_svg]:h-5" "[&_[cmdk-item]_svg]:w-5")}]]
         children)])

(defn command-input
  "Command input field with search icon.
  
  Props:
  - `:class` - Additional Tailwind classes for the input
  - `:placeholder` - Input placeholder text
  - `:value` - Controlled input value
  - `:onValueChange` - Callback when value changes (fn [value])
  
  Example:
  [command-input {:placeholder \"Search commands...\"
                  :value @search
                  :onValueChange #(reset! search %)}]"
  [{:keys [class]
    :as props}]
  [:div {:data-slot "command-input-wrapper"
         :class "flex h-9 items-center gap-2 border-b px-3"}
   [:> SearchIcon {:class "size-4 shrink-0 opacity-50"}]
   [:>
    (.-Input Command)
    (-> props
        (assoc :data-slot "command-input"
               :class
               (merge-classes
                "placeholder:text-muted-foreground"
                "flex h-10 w-full rounded-md border-0 bg-transparent py-3 text-sm outline-none"
                "disabled:cursor-not-allowed disabled:opacity-50" class))
        (dissoc :class-name))]])

(defn command-list
  "Scrollable list container for command items.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Max height of 300px with scroll
  - Keyboard navigation support
  - Automatically shows/hides empty state
  
  Example:
  [command-list {}
   [command-empty {} \"No results.\"]
   [command-group {}
    [command-item {} \"Item 1\"]
    [command-item {} \"Item 2\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-List Command)
         (-> props
             (assoc :data-slot "command-list"
                    :class (merge-classes
                            "max-h-[300px] scroll-py-1 overflow-x-hidden overflow-y-auto"
                            class))
             (dissoc :class-name))]
        children))

(defn command-empty
  "Empty state shown when no items match the search.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Example:
  [command-empty {} \"No results found.\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Empty Command)
         (-> props
             (assoc :data-slot "command-empty"
                    :class (merge-classes "py-6 text-center text-sm" class))
             (dissoc :class-name))]
        children))

(defn command-group
  "Group container for related command items.
  
  Props:
  - `:class` - Additional Tailwind classes
  - `:heading` - Group heading text
  
  Example:
  [command-group {:heading \"File Operations\"}
   [command-item {} \"New File\"]
   [command-item {} \"Open File\"]
   [command-item {} \"Save File\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Group Command)
         (-> props
             (assoc :data-slot "command-group"
                    :class (merge-classes
                            "text-foreground [&_[cmdk-group-heading]]:text-muted-foreground"
                            "overflow-hidden p-1" "[&_[cmdk-group-heading]]:px-2"
                            "[&_[cmdk-group-heading]]:py-1.5" "[&_[cmdk-group-heading]]:text-xs"
                            "[&_[cmdk-group-heading]]:font-medium" class))
             (dissoc :class-name))]
        children))

(defn command-separator
  "Horizontal separator between command groups.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Example:
  [command-group {:heading \"Group 1\"}
   [command-item {} \"Item 1\"]]
  [command-separator {}]
  [command-group {:heading \"Group 2\"}
   [command-item {} \"Item 2\"]]"
  [{:keys [class]
    :as props}]
  [:>
   (.-Separator Command)
   (-> props
       (assoc :data-slot "command-separator" :class (merge-classes "bg-border -mx-1 h-px" class))
       (dissoc :class-name))])

(defn command-item
  "Selectable command item.
  
  Props:
  - `:class` - Additional Tailwind classes
  - `:value` - Search value for filtering (defaults to children text)
  - `:onSelect` - Callback when item is selected (fn [value])
  - `:disabled` - Whether item is disabled (boolean)
  - `:keywords` - Additional keywords for search (array/vector of strings)
  
  Features:
  - Keyboard navigation (arrow keys)
  - Mouse hover/click selection
  - Visual states (selected, disabled)
  - Automatic filtering based on search
  
  Example:
  [command-item {:value \"new-file\"
                 :onSelect #(create-new-file!)
                 :keywords [\"create\" \"add\"]}
   [:> FileIcon]
   [:span \"New File\"]
   [command-shortcut {} \"⌘N\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Item Command)
         (-> props
             (assoc :data-slot "command-item"
                    :class
                    (merge-classes
                     "data-[selected=true]:bg-accent data-[selected=true]:text-accent-foreground"
                     "[&_svg:not([class*='text-'])]:text-muted-foreground"
                     "relative flex cursor-default items-center gap-2 rounded-sm px-2 py-1.5"
                     "text-sm outline-hidden select-none"
                     "data-[disabled=true]:pointer-events-none data-[disabled=true]:opacity-50"
                     "[&_svg]:pointer-events-none [&_svg]:shrink-0"
                     "[&_svg:not([class*='size-'])]:size-4" class))
             (dissoc :class-name))]
        children))

(defn command-shortcut
  "Keyboard shortcut display for command items.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Note: This is purely visual - you need to handle the actual keyboard shortcut
  separately using your keyboard event handlers.
  
  Example:
  [command-item {}
   [:span \"Save File\"]
   [command-shortcut {} \"⌘S\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:span
         (-> props
             (assoc :data-slot "command-shortcut"
                    :class (merge-classes "text-muted-foreground ml-auto text-xs tracking-widest"
                                          class))
             (dissoc :class-name))]
        children))
