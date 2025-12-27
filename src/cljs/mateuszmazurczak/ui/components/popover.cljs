(ns mateuszmazurczak.ui.components.popover
  "Popover component for displaying floating content relative to a trigger.
  https://www.radix-ui.com/primitives/docs/components/popover"
  (:require
   ["@radix-ui/react-popover"     :as RadixPopover]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn popover
  "Root popover component. Controls open/closed state.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:defaultOpen` - Default open state for uncontrolled usage
  - `:onOpenChange` - Callback when open state changes (fn [open?])
  - `:modal` - Whether to trap focus (default: false, unlike Dialog)
  
  Example:
  [popover {:open @open? :onOpenChange #(reset! open? %)}
   [popover-trigger {} [button {} \"Open\"]]
   [popover-content {}
    [:div \"Popover content\"]]]"
  [props & children]
  (into [:> RadixPopover/Root (assoc props :data-slot "popover")] children))

(defn popover-trigger
  "Trigger element that opens the popover when clicked.
  
  Props:
  - `:asChild` - Compose with child component (default: false)
  
  Example:
  [popover-trigger {}
   [button {:variant :outline} \"Open Popover\"]]"
  [props & children]
  (into [:> RadixPopover/Trigger (assoc props :data-slot "popover-trigger")] children))

(defn popover-anchor
  "Optional anchor element to position the popover relative to.
  If not provided, the trigger is used as the anchor.
  
  Props:
  - `:asChild` - Compose with child component
  
  Example:
  [popover-anchor {:asChild true}
   [:div {:ref anchor-ref} \"Anchor element\"]]"
  [props & children]
  (into [:> RadixPopover/Anchor (assoc props :data-slot "popover-anchor")] children))

(defn popover-content
  "Popover content container with portal rendering and positioning.
  
  Props:
  - `:class` - Additional Tailwind classes
  - `:align` - Alignment relative to trigger: \"start\", \"center\" (default), \"end\"
  - `:side` - Which side to place popover: \"top\", \"right\", \"bottom\" (default), \"left\"
  - `:sideOffset` - Distance in pixels from the trigger (default: 4)
  - `:alignOffset` - Offset along the align axis
  - `:collisionPadding` - Padding from viewport edges when positioning
  - `:avoidCollisions` - Whether to avoid collisions with viewport (default: true)
  
  Features:
  - Automatically portaled to document body
  - Animated entry/exit
  - Smart collision detection and repositioning
  - Focus management
  - ESC to close
  
  Example:
  [popover-content {:align \"start\" :side \"bottom\"}
   [:div {:class \"space-y-2\"}
    [:h4 {:class \"font-medium\"} \"Popover Title\"]
    [:p {:class \"text-sm text-muted-foreground\"}
     \"Additional information here.\"]]]"
  [{:keys [class align sideOffset]
    :or {align "center"
         sideOffset 4}
    :as props}
   &
   children]
  [:>
   RadixPopover/Portal
   (into [:>
          RadixPopover/Content
          (-> props
              (assoc :data-slot "popover-content"
                     :align align
                     :sideOffset sideOffset
                     :class (merge-classes
                             ["bg-popover text-popover-foreground"
                              "data-[state=open]:animate-in data-[state=closed]:animate-out"
                              "data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0"
                              "data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95"
                              "data-[side=bottom]:slide-in-from-top-2"
                              "data-[side=left]:slide-in-from-right-2"
                              "data-[side=right]:slide-in-from-left-2"
                              "data-[side=top]:slide-in-from-bottom-2"
                              "z-50 w-72"
                              "origin-(--radix-popover-content-transform-origin)"
                              "rounded-md border p-4 shadow-md outline-hidden"]
                             class))
              (dissoc :class-name))]
         children)])
