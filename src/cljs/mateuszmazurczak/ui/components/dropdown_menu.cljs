(ns mateuszmazurczak.ui.components.dropdown-menu
  "Dropdown menu component for displaying a menu of actions.
  https://www.radix-ui.com/primitives/docs/components/dropdown-menu

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/dropdown-menu"
  (:require
   ["@radix-ui/react-dropdown-menu" :as DropdownMenuPrimitive]
   ["lucide-react"                  :refer [Check ChevronRight Circle]]
   [mateuszmazurczak.utils.styles   :refer [merge-classes]]
   [reagent.core                    :as r]))

;; ============================================================================
;; Root Components (simple re-exports)
;; ============================================================================

(defn dropdown-menu
  "Dropdown menu root component. Manages the open/closed state.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:default-open` - Uncontrolled default open state (boolean)
  - `:on-open-change` - Callback when open state changes: (fn [open?] ...)
  - `:modal` - Whether the dropdown is modal (default: true)
  
  Example:
  [dropdown-menu {}
    [dropdown-menu-trigger {} \"Open\"]
    [dropdown-menu-content {}
      [dropdown-menu-item {} \"Item 1\"]
      [dropdown-menu-item {} \"Item 2\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Root DropdownMenuPrimitive)
         (cond-> props
           class (assoc :class class)
           true (assoc :data-slot "dropdown-menu"))]
        children))

(defn dropdown-menu-trigger
  "Dropdown menu trigger component. The button that opens the menu.
  
  Props:
  - `:as-child` - When true, uses Radix Slot for polymorphic rendering
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-trigger {}
    [button {:variant :outline} \"Open menu\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Trigger DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-trigger" :class class)
             (dissoc :class-name))]
        children))

(defn dropdown-menu-group
  "Dropdown menu group component. Groups related menu items.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-group {}
    [dropdown-menu-item {} \"Cut\"]
    [dropdown-menu-item {} \"Copy\"]
    [dropdown-menu-item {} \"Paste\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Group DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-group" :class class)
             (dissoc :class-name))]
        children))

(defn dropdown-menu-portal
  "Dropdown menu portal component. Renders content in a portal (outside DOM hierarchy).
  
  Props:
  - `:container` - DOM node to render the portal into
  - `:force-mount` - Force mounting for animation control
  
  Example:
  [dropdown-menu-portal {}
    [dropdown-menu-content {} ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Portal DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-portal" :class class)
             (dissoc :class-name))]
        children))

(defn dropdown-menu-sub
  "Dropdown menu sub component. Root for submenu (nested menu).
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:default-open` - Uncontrolled default open state (boolean)
  - `:on-open-change` - Callback when open state changes
  
  Example:
  [dropdown-menu-sub {}
    [dropdown-menu-sub-trigger {} \"More options\"]
    [dropdown-menu-sub-content {}
      [dropdown-menu-item {} \"Sub item 1\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Sub DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-sub" :class class)
             (dissoc :class-name))]
        children))

(defn dropdown-menu-radio-group
  "Dropdown menu radio group component. Groups radio items for single selection.
  
  Props:
  - `:value` - Selected value
  - `:on-value-change` - Callback when selection changes: (fn [value] ...)
  
  Example:
  [dropdown-menu-radio-group {:value \"option1\"
                              :on-value-change #(println \"Selected:\" %)}
    [dropdown-menu-radio-item {:value \"option1\"} \"Option 1\"]
    [dropdown-menu-radio-item {:value \"option2\"} \"Option 2\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-RadioGroup DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-radio-group" :class class)
             (dissoc :class-name))]
        children))

;; ============================================================================
;; Styled Components
;; ============================================================================

(defn dropdown-menu-sub-trigger
  "Dropdown menu sub-trigger component. Trigger for opening a submenu.
  
  Props:
  - `:inset` - Add left padding (pl-8) for alignment (boolean)
  - `:disabled` - Disable the trigger (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-sub-trigger {}
    \"More options\"]
  
  [dropdown-menu-sub-trigger {:inset true}
    \"Nested menu\"]"
  [{:keys [class inset]
    :as props}
   &
   children]
  (into
   [:>
    (.-SubTrigger DropdownMenuPrimitive)
    (->
      props
      (assoc
       :data-slot "dropdown-menu-sub-trigger"
       :data-inset inset
       :class
       (merge-classes
        "focus:bg-accent focus:text-accent-foreground data-[state=open]:bg-accent data-[state=open]:text-accent-foreground [&_svg:not([class*='text-'])]:text-muted-foreground flex cursor-default items-center gap-2 rounded-sm px-2 py-1.5 text-sm outline-hidden select-none data-[inset]:pl-8 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4"
        class))
      (dissoc :class-name :inset))]
   (concat children [(r/as-element [:> ChevronRight {:class "ml-auto"}])])))

(defn dropdown-menu-sub-content
  "Dropdown menu sub-content component. Content of a submenu with animations.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-sub-content {}
    [dropdown-menu-item {} \"Sub item 1\"]
    [dropdown-menu-item {} \"Sub item 2\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:>
    (.-SubContent DropdownMenuPrimitive)
    (->
      props
      (assoc
       :data-slot "dropdown-menu-sub-content"
       :class
       (merge-classes
        "bg-popover text-popover-foreground data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 z-50 min-w-[8rem] origin-(--radix-dropdown-menu-content-transform-origin) overflow-hidden rounded-md border p-1 shadow-lg"
        class))
      (dissoc :class-name))]
   children))

(defn dropdown-menu-content
  "Dropdown menu content component. The main content container with animations.
  Automatically wrapped in a Portal.
  
  Props:
  - `:side-offset` - Distance from trigger (default: 4)
  - `:align` - Alignment relative to trigger (:start, :center, :end)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-content {}
    [dropdown-menu-label {} \"My Account\"]
    [dropdown-menu-separator {}]
    [dropdown-menu-item {} \"Profile\"]
    [dropdown-menu-item {} \"Settings\"]]"
  [{:keys [class side-offset]
    :or {side-offset 4}
    :as props}
   &
   children]
  [:>
   (.-Portal DropdownMenuPrimitive)
   (into
    [:>
     (.-Content DropdownMenuPrimitive)
     (->
       props
       (assoc
        :side-offset side-offset
        :data-slot "dropdown-menu-content"
        :class
        (merge-classes
         "bg-popover text-popover-foreground data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 z-50 max-h-(--radix-dropdown-menu-content-available-height) min-w-[8rem] origin-(--radix-dropdown-menu-content-transform-origin) overflow-x-hidden overflow-y-auto rounded-md border p-1 shadow-md"
         class))
       (dissoc :class-name :side-offset))]
    children)])

(defn dropdown-menu-item
  "Dropdown menu item component. A selectable menu item.
  
  Props:
  - `:inset` - Add left padding (pl-8) for alignment (boolean)
  - `:disabled` - Disable the item (boolean)
  - `:on-select` - Callback when item is selected: (fn [event] ...)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-item {:on-select #(js/console.log \"clicked\")}
    \"Click me\"]
  
  [dropdown-menu-item {:inset true}
    [:> FileIcon]
    \"Open file\"]"
  [{:keys [class inset]
    :as props}
   &
   children]
  (into
   [:>
    (.-Item DropdownMenuPrimitive)
    (->
      props
      (assoc
       :data-slot "dropdown-menu-item"
       :data-inset inset
       :class
       (merge-classes
        "focus:bg-accent focus:text-accent-foreground [&_svg:not([class*='text-'])]:text-muted-foreground relative flex cursor-default items-center gap-2 rounded-sm px-2 py-1.5 text-sm outline-hidden select-none data-[disabled]:pointer-events-none data-[disabled]:opacity-50 data-[inset]:pl-8 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4"
        class))
      (dissoc :class-name :inset))]
   children))

(defn dropdown-menu-checkbox-item
  "Dropdown menu checkbox item component. A menu item with checkbox state.
  
  Props:
  - `:checked` - Checked state (boolean or \"indeterminate\")
  - `:on-checked-change` - Callback when checked state changes: (fn [checked?] ...)
  - `:disabled` - Disable the item (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  (let [checked? (r/atom false)]
    [dropdown-menu-checkbox-item {:checked @checked?
                                  :on-checked-change #(reset! checked? %)}
      \"Show sidebar\"])"
  [{:keys [class checked]
    :as props}
   &
   children]
  (into
   [:>
    (.-CheckboxItem DropdownMenuPrimitive)
    (->
      props
      (assoc
       :checked checked
       :data-slot "dropdown-menu-checkbox-item"
       :class
       (merge-classes
        "focus:bg-accent focus:text-accent-foreground relative flex cursor-default items-center gap-2 rounded-sm py-1.5 pr-2 pl-8 text-sm outline-hidden select-none data-[disabled]:pointer-events-none data-[disabled]:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4"
        class))
      (dissoc :class-name))]
   (cons [:span {:class
                 "pointer-events-none absolute left-2 flex size-3.5 items-center justify-center"}
          [:> (.-ItemIndicator DropdownMenuPrimitive) (r/as-element [:> Check {:class "size-4"}])]]
         children)))

(defn dropdown-menu-radio-item
  "Dropdown menu radio item component. A menu item for radio group selection.
  
  Props:
  - `:value` - The value of this radio item
  - `:disabled` - Disable the item (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-radio-group {:value \"comfortable\"}
    [dropdown-menu-radio-item {:value \"compact\"} \"Compact\"]
    [dropdown-menu-radio-item {:value \"comfortable\"} \"Comfortable\"]
    [dropdown-menu-radio-item {:value \"spacious\"} \"Spacious\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:>
    (.-RadioItem DropdownMenuPrimitive)
    (->
      props
      (assoc
       :data-slot "dropdown-menu-radio-item"
       :class
       (merge-classes
        "focus:bg-accent focus:text-accent-foreground relative flex cursor-default items-center gap-2 rounded-sm py-1.5 pr-2 pl-8 text-sm outline-hidden select-none data-[disabled]:pointer-events-none data-[disabled]:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4"
        class))
      (dissoc :class-name))]
   (cons [:span {:class
                 "pointer-events-none absolute left-2 flex size-3.5 items-center justify-center"}
          [:>
           (.-ItemIndicator DropdownMenuPrimitive)
           (r/as-element [:> Circle {:class "size-2 fill-current"}])]]
         children)))

(defn dropdown-menu-label
  "Dropdown menu label component. A non-interactive label for menu sections.
  
  Props:
  - `:inset` - Add left padding (pl-8) for alignment (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-label {} \"My Account\"]
  [dropdown-menu-label {:inset true} \"Options\"]"
  [{:keys [class inset]
    :as props}
   &
   children]
  (into [:>
         (.-Label DropdownMenuPrimitive)
         (-> props
             (assoc :data-slot "dropdown-menu-label"
                    :data-inset inset
                    :class (merge-classes "px-2 py-1.5 text-sm font-medium data-[inset]:pl-8"
                                          class))
             (dissoc :class-name :inset))]
        children))

(defn dropdown-menu-separator
  "Dropdown menu separator component. A visual separator between menu sections.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-item {} \"Item 1\"]
  [dropdown-menu-separator {}]
  [dropdown-menu-item {} \"Item 2\"]"
  [{:keys [class]
    :as props}]
  [:>
   (.-Separator DropdownMenuPrimitive)
   (-> props
       (assoc :data-slot "dropdown-menu-separator"
              :class (merge-classes "bg-border -mx-1 my-1 h-px" class))
       (dissoc :class-name))])

(defn dropdown-menu-shortcut
  "Dropdown menu shortcut component. Displays keyboard shortcuts (non-interactive).
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [dropdown-menu-item {}
    \"Save\"
    [dropdown-menu-shortcut {} \"⌘S\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:span
         (-> props
             (assoc :data-slot "dropdown-menu-shortcut"
                    :class (merge-classes "text-muted-foreground ml-auto text-xs tracking-widest"
                                          class))
             (dissoc :class-name))]
        children))
