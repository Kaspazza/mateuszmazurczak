(ns mateuszmazurczak.ui.components.radio-group
  "Radio Group component built on Radix UI primitives.
  https://www.radix-ui.com/primitives/docs/components/radio-group"
  (:require
   ["@radix-ui/react-radio-group" :as RadioGroupPrimitive]
   ["lucide-react"                :refer [CircleIcon]]
   [mateuszmazurczak.utils.styles             :refer [merge-classes]]
   [reagent.core                  :as r]))

(defn radio-group
  "Radio Group component for selecting a single option from a set.
  
  Props:
  - `:value` - Controlled value state (string)
  - `:default-value` - Uncontrolled default value (string)
  - `:on-value-change` - Callback when value changes: (fn [value] ...)
  - `:disabled` - Disable all radio items (boolean)
  - `:required` - Mark as required for form validation (boolean)
  - `:name` - Form field name (string)
  - `:orientation` - Layout orientation (:horizontal or :vertical, default :vertical)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Uncontrolled radio group
  [radio-group {}
    [radio-group-item {:value \"option1\" :id \"opt1\"}]
    [label {:html-for \"opt1\"} \"Option 1\"]
    [radio-group-item {:value \"option2\" :id \"opt2\"}]
    [label {:html-for \"opt2\"} \"Option 2\"]]
  
  ;; Controlled radio group
  (let [value (r/atom \"default\")]
    [radio-group {:value @value
                  :on-value-change #(reset! value %)}
      [radio-group-item {:value \"option1\" :id \"opt1\"}]
      [label {:html-for \"opt1\"} \"Option 1\"]
      [radio-group-item {:value \"option2\" :id \"opt2\"}]
      [label {:html-for \"opt2\"} \"Option 2\"]])
  
  ;; Horizontal orientation
  [radio-group {:orientation :horizontal :class \"flex-row\"}
    [radio-group-item {:value \"a\"}]
    [radio-group-item {:value \"b\"}]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Root RadioGroupPrimitive)
         (-> props
             (assoc :data-slot "radio-group" :class (merge-classes "grid gap-3" class))
             (dissoc :class-name))]
        children))

(defn radio-group-item
  "Radio Group Item component representing a single selectable option.
  
  Props:
  - `:value` - The value associated with this radio item (string, required)
  - `:id` - HTML id attribute for label association (string)
  - `:disabled` - Disable this specific radio item (boolean)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Basic radio item
  [radio-group-item {:value \"option1\"}]
  
  ;; With label
  [radio-group-item {:value \"option1\" :id \"opt1\"}]
  [label {:html-for \"opt1\"} \"Option 1\"]
  
  ;; Disabled item
  [radio-group-item {:value \"option2\" :disabled true}]"
  [{:keys [class]
    :as props}]
  [:>
   (.-Item RadioGroupPrimitive)
   (->
     props
     (assoc
      :data-slot "radio-group-item"
      :class
      (merge-classes
       "border-input text-primary focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:bg-input/30 aspect-square size-4 shrink-0 rounded-full border shadow-xs transition-[color,box-shadow] outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50"
       class))
     (dissoc :class-name))
   [:>
    (.-Indicator RadioGroupPrimitive)
    {:data-slot "radio-group-indicator"
     :class "relative flex items-center justify-center"}
    (r/as-element
     [:>
      CircleIcon
      {:class
       "fill-primary absolute top-1/2 left-1/2 size-2 -translate-x-1/2 -translate-y-1/2"}])]])
