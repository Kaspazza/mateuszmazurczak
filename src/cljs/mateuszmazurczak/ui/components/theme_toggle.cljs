(ns mateuszmazurczak.ui.components.theme-toggle
  "Theme toggle component for switching between light and dark themes.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["lucide-react"                :refer [Moon Sun]]
   [mateuszmazurczak.ports.events :as events]
   [mateuszmazurczak.ports.state  :as state]))

(defn theme-toggle
  "Theme toggle button component.
   
   Displays a button that shows a sun icon in dark mode and moon icon in light mode.
   Clicking the button toggles between light and dark themes with smooth transitions."
  []
  (let [current-theme @(state/watch [:theme/current])
        is-dark? (= current-theme :dark)]
    [:button {:type "button"
              :on-click #(events/dispatch! [:theme/toggle])
              :class ["relative"
                      "inline-flex"
                      "items-center"
                      "justify-center"
                      "rounded-md"
                      "border"
                      "border-gray-300"
                      "dark:border-gray-700"
                      "bg-white"
                      "dark:bg-gray-800"
                      "px-3"
                      "py-2"
                      "text-sm"
                      "font-medium"
                      "text-gray-700"
                      "dark:text-gray-200"
                      "hover:bg-gray-50"
                      "dark:hover:bg-gray-700"
                      "focus:outline-none"
                      "focus:ring-2"
                      "focus:ring-indigo-500"
                      "focus:ring-offset-2"
                      "dark:focus:ring-offset-gray-900"
                      "transition-colors"]
              :aria-label "Toggle theme"}
     ;; Sun icon - visible in light mode
     [:>
      Sun
      {:class (str "h-[1.2rem] w-[1.2rem] transition-all "
                   (if is-dark? "scale-0 -rotate-90" "scale-100 rotate-0"))}]
     ;; Moon icon - visible in dark mode
     [:>
      Moon
      {:class (str "absolute h-[1.2rem] w-[1.2rem] transition-all "
                   (if is-dark? "scale-100 rotate-0" "scale-0 rotate-90"))}]
     [:span {:class ["sr-only"]}
      "Toggle theme"]]))
