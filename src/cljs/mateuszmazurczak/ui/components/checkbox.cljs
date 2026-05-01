(ns mateuszmazurczak.ui.components.checkbox
  "Checkbox component with built-in check indicator.
  https://www.radix-ui.com/primitives/docs/components/checkbox

  Version: 1.0.0
  Last updated: 2026-02-06

  Based on Radix UI primitives.
  Documentation: https://www.radix-ui.com/primitives/docs/components/checkbox"
  (:require
   ["@radix-ui/react-checkbox"    :as CheckboxPrimitive]
   ["lucide-react"                :refer [Check]]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]
   [reagent.core                  :as r]))

(defn- checked->radix
  "Coerces :indeterminate keyword to the \"indeterminate\" string expected by
  the Radix UI JS primitive. Booleans and nil pass through unchanged."
  [v]
  (if (= v :indeterminate) "indeterminate" v))

(defn- radix->checked
  "Coerces the \"indeterminate\" string from Radix callbacks back to
  the :indeterminate keyword. Booleans pass through unchanged."
  [v]
  (if (= v "indeterminate") :indeterminate v))

(defn checkbox
  "Checkbox component with built-in check indicator.
  
  Props:
  - `:checked` - Controlled checked state (boolean, :indeterminate keyword, or \"indeterminate\" string)
  - `:default-checked` - Uncontrolled default checked state (boolean)
  - `:on-checked-change` - Callback when checked state changes: (fn [checked?] ...). Receives true, false, or :indeterminate
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
  
  ;; Indeterminate state (keyword preferred, string also accepted)
  [checkbox {:checked :indeterminate}]
  
  ;; Disabled checkbox
  [checkbox {:disabled true :checked true}]
  
  ;; With custom styling
  [checkbox {:class \"border-blue-500\"}]
  
  ;; Form integration
  [checkbox {:name \"terms\"
             :value \"accepted\"
             :required true}]"
  [{:keys [class checked on-checked-change]
    :as props}]
  [:>
   (.-Root CheckboxPrimitive)
   (->
    props
    (assoc
     :checked (checked->radix checked)
     :on-checked-change (when on-checked-change
                          (comp on-checked-change radix->checked))
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
