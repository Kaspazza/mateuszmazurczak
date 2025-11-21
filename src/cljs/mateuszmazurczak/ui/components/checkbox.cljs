(ns mateuszmazurczak.ui.components.checkbox
  "Checkbox component with built-in check indicator.
  https://www.radix-ui.com/primitives/docs/components/checkbox"
  (:require
   ["@radix-ui/react-checkbox" :as CheckboxPrimitive]
   ["lucide-react"             :refer [Check]]
   [ui.utils.styles          :refer [merge-classes]]
   [reagent.core               :as r]))

(defn checkbox
  "Checkbox component with built-in check indicator.
  
  Props:
  - `:checked` - Controlled checked state (boolean or \"indeterminate\")
  - `:default-checked` - Uncontrolled default checked state (boolean)
  - `:on-checked-change` - Callback when checked state changes: (fn [checked?] ...)
  - `:disabled` - Disable the checkbox (boolean)
  - `:required` - Mark as required for form validation (boolean)
  - `:name` - Form field name (string)
  - `:value` - Form field value (string)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Uncontrolled checkbox
  [checkbox {}]
  
  ;; Controlled checkbox
  (let [checked? (r/atom false)]
    [checkbox {:checked @checked?
               :on-checked-change #(reset! checked? %)}])
  
  ;; Disabled checkbox
  [checkbox {:disabled true :checked true}]
  
  ;; With custom styling
  [checkbox {:class \"border-blue-500\"}]
  
  ;; Form integration
  [checkbox {:name \"terms\"
             :value \"accepted\"
             :required true}]"
  [{:keys [class]
    :as props}]
  [:>
   (.-Root CheckboxPrimitive)
   (-> props
    (assoc
     :data-slot "checkbox"
     :class
     (merge-classes
      "peer border-input dark:bg-input/30 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground dark:data-[state=checked]:bg-primary data-[state=checked]:border-primary focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive size-4 shrink-0 rounded-[4px] border shadow-xs transition-shadow outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50"
      class))
    (dissoc :class-name))
   [:>
    (.-Indicator CheckboxPrimitive)
    {:data-slot "checkbox-indicator"
     :class "grid place-content-center text-current transition-none"}
    (r/as-element [:> Check {:class "size-3.5"}])]])
