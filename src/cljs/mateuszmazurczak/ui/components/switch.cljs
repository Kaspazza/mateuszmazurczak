(ns mateuszmazurczak.ui.components.switch
  "Switch component for toggle controls.
  https://www.radix-ui.com/primitives/docs/components/switch"
  (:require
   ["@radix-ui/react-switch" :as SwitchPrimitive]
   [mateuszmazurczak.utils.styles        :refer [merge-classes]]))

(defn switch
  "Switch component for toggle controls.
  
  Props:
  - `:checked` - Controlled checked state (boolean)
  - `:default-checked` - Uncontrolled default checked state (boolean)
  - `:on-checked-change` - Callback when checked state changes: (fn [checked?] ...)
  - `:disabled` - Disable the switch (boolean)
  - `:required` - Mark as required for form validation (boolean)
  - `:name` - Form field name (string)
  - `:value` - Form field value (string)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Uncontrolled switch
  [switch {}]
  
  ;; Controlled switch
  (let [enabled? (r/atom false)]
    [switch {:checked @enabled?
             :on-checked-change #(reset! enabled? %)}])
  
  ;; Disabled switch
  [switch {:disabled true :checked true}]
  
  ;; With custom styling
  [switch {:class \"scale-125\"}]
  
  ;; Form integration
  [switch {:name \"notifications\"
           :value \"enabled\"
           :required true}]"
  [{:keys [class]
    :as props}]
  [:>
   (.-Root SwitchPrimitive)
   (->
     props
     (assoc
      :data-slot "switch"
      :class
      (merge-classes
       "peer data-[state=checked]:bg-primary data-[state=unchecked]:bg-input focus-visible:border-ring focus-visible:ring-ring/50 dark:data-[state=unchecked]:bg-input/80 inline-flex h-[1.15rem] w-8 shrink-0 items-center rounded-full border border-transparent shadow-xs transition-all outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50"
       class))
     (dissoc :class-name))
   [:>
    (.-Thumb SwitchPrimitive)
    {:data-slot "switch-thumb"
     :class
     "bg-background dark:data-[state=unchecked]:bg-foreground dark:data-[state=checked]:bg-primary-foreground pointer-events-none block size-4 rounded-full ring-0 transition-transform data-[state=checked]:translate-x-[calc(100%-2px)] data-[state=unchecked]:translate-x-0"}]])
