(ns mateuszmazurczak.utils.styles
  "Tailwind CSS class name utilities"
  (:require
   ["tailwind-merge" :refer [twMerge]]))

(defn merge-classes
  "Utility to merge Tailwind CSS class names properly, handling conflicts.
  Uses tailwind-merge to ensure later classes override earlier ones correctly.
  
  Example:
  (merge-classes \"px-4\" \"px-2\") => \"px-2\"
  (merge-classes \"text-sm\" nil \"font-bold\") => \"text-sm font-bold\""
  [& classes]
  (twMerge (clj->js (remove nil? classes))))
