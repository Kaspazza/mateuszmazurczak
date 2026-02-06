(ns mateuszmazurczak.ui.components.label
  "Label component for form fields with accessibility support.
  https://www.radix-ui.com/primitives/docs/components/label

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/label"
  (:require
   ["@radix-ui/react-label"       :as RadixLabel]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn label
  "Label component that automatically associates with form controls.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:htmlFor` - ID of the form element to associate with (optional, handled automatically by Radix)
  
  Features:
  - Automatically handles click-to-focus for associated form controls
  - Supports disabled state styling (when parent has data-disabled or peer is disabled)
  - Prevents text selection for better UX
  - Fully accessible with proper ARIA attributes
  
  Examples:
  ;; Basic label
  [label {:htmlFor \"email\"} \"Email address\"]
  
  ;; With custom classes
  [label {:class \"text-lg font-bold\"} \"Username\"]
  
  ;; In a form group
  [:div {:class \"space-y-2\"}
   [label {:htmlFor \"password\"} \"Password\"]
   [input {:id \"password\" :type \"password\"}]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         RadixLabel/Root
         (-> props
             (assoc :data-slot "label"
                    :class (merge-classes
                            ["flex items-center gap-2 text-sm leading-none font-medium select-none"
                             "group-data-[disabled=true]:pointer-events-none"
                             "group-data-[disabled=true]:opacity-50"
                             "peer-disabled:cursor-not-allowed peer-disabled:opacity-50"]
                            class))
             (dissoc :class-name))]
        children))
