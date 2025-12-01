(ns mateuszmazurczak.ui.components.spinner
  "Spinner component using Lucide Loader2 icon.
  Works on both CLJ (SSR) and CLJS (browser)."
  (:require
   [clojure.string :as str]))

(defn spinner
  "Loading spinner component using Lucide Loader2 SVG.
  
  Opts:
  - `:class` - Additional CSS classes (string or vector)
  - `:size` - Size in pixels or Tailwind size class (default: depends on context)
  
  Example:
  (spinner {:class \"text-blue-500\" :size 32})
  (spinner {:class \"size-8\"})"
  ([] (spinner {}))
  ([opts]
   (let [{:keys [class size]} opts
         class-str (cond
                     (vector? class) (str/join " " class)
                     (string? class) class
                     :else "")
         ;; Default size handling - if class contains size-*, don't set explicit size
         explicit-size (when (and size (not (str/includes? class-str "size-")))
                         size)
         merged-class (str "animate-spin " class-str)]
     [:svg (cond-> {:xmlns "http://www.w3.org/2000/svg"
                    :viewBox "0 0 24 24"
                    :fill "none"
                    :stroke "currentColor"
                    :stroke-width "2"
                    :stroke-linecap "round"
                    :stroke-linejoin "round"
                    :class merged-class
                    :role "status"
                    :aria-label "Loading"}
             explicit-size (assoc :width explicit-size :height explicit-size))
      ;; Lucide Loader2 icon path
      [:path {:d "M21 12a9 9 0 1 1-6.219-8.56"}]])))
