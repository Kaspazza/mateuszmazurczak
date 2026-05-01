(ns mateuszmazurczak.ui.components.spinner
  "Spinner component for loading states.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["lucide-react"                :refer [Loader2]]
   [mateuszmazurczak.utils.styles :as styles]))

(defn spinner
  "Loading spinner component.
  
  Props:
  - `:class` - Additional CSS classes (optional)
  - Other props passed to svg element"
  [{:keys [class]
    :as props}]
  (let [other-props (dissoc props :class)]
    [:>
     Loader2
     (merge {:role "status"
             :aria-label "Loading"
             :class (styles/merge-classes "size-4 animate-spin" class)}
            other-props)]))
