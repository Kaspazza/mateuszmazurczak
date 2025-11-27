(ns mateuszmazurczak.ui.components.collapsible
  "Collapsible component for showing and hiding content with animation.
  https://www.radix-ui.com/primitives/docs/components/collapsible"
  (:require
   ["@radix-ui/react-collapsible" :as CollapsiblePrimitive]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn collapsible
  "Collapsible root component. Manages the open/closed state of the collapsible.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:default-open` - Uncontrolled default open state (boolean)
  - `:on-open-change` - Callback function when open state changes: (fn [open?] ...)
  - `:disabled` - Disable the collapsible (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Uncontrolled
  [collapsible {:default-open true}
    [collapsible-trigger {} \"Toggle\"]
    [collapsible-content {} \"Hidden content\"]]
  
  ;; Controlled
  (let [open? (r/atom false)]
    [collapsible {:open @open?
                  :on-open-change #(reset! open? %)}
      [collapsible-trigger {} \"Toggle\"]
      [collapsible-content {} \"Hidden content\"]])"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Root CollapsiblePrimitive)
         (-> props
             (assoc :data-slot "collapsible" :class class)
             (dissoc :class-name))]
        children))

(defn collapsible-trigger
  "Collapsible trigger component. The button that toggles the collapsible state.
  
  Props:
  - `:as-child` - When true, uses Radix Slot for polymorphic rendering
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:on-click` - Additional click handler (will be composed with toggle behavior)
  
  Examples:
  ;; Default button
  [collapsible-trigger {} \"Show more\"]
  
  ;; With custom styling
  [collapsible-trigger {:class \"font-semibold text-lg\"} \"Toggle\"]
  
  ;; As child (polymorphic)
  [collapsible-trigger {:as-child true}
    [:button {:class \"custom-button\"} \"Toggle\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-CollapsibleTrigger CollapsiblePrimitive)
         (-> props
             (assoc :data-slot "collapsible-trigger" :class class)
             (dissoc :class-name))]
        children))

(defn collapsible-content
  "Collapsible content component. The content that expands and collapses with animation.
  
  Props:
  - `:force-mount` - Force mounting for animation libraries (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Note: The content will animate using the Radix UI built-in animations.
  You can customize the animation using CSS/Tailwind classes targeting the
  [data-state='open'] and [data-state='closed'] attributes.
  
  Examples:
  ;; Basic content
  [collapsible-content {}
    [:div {:class \"p-4\"}
      \"This content will collapse\"]]
  
  ;; With custom animation classes
  [collapsible-content {:class \"data-[state=open]:animate-slide-down data-[state=closed]:animate-slide-up\"}
    [:div \"Animated content\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-CollapsibleContent CollapsiblePrimitive)
         (-> props
             (assoc :data-slot "collapsible-content" :class class)
             (dissoc :class-name))]
        children))
