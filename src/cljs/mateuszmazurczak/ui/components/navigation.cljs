(ns mateuszmazurczak.ui.components.navigation
  "Navigation link components for primary and back navigation patterns.

  Version: 1.0.0
  Last updated: 2026-02-06

  Custom component implementation.")

(defn navigation
  "Print a navigation button
  * The linked could be set with `href` or `on-click` and are higher priority"
  [{:keys [href text on-click]}]
  [:a
   (merge (cond
            href {:href href}
            on-click {:on-click on-click})
          {:class ["font-semibold leading-7"
                   (when (some nil? [href on-click]) "cursor-pointer")
                   #_"text-primary"]})
   text])

(defn back-navigation
  "Back navigation button
  * The linked could be set with `href` or `on-click` and are higher priority"
  [{:keys [href text dark? on-click]}]
  [:a
   (merge (cond
            href {:href href}
            on-click {:on-click on-click})
          {:class ["font-semibold leading-7"
                   (when (some nil? [href on-click]) "cursor-pointer")
                   (if dark? "text-additional" "text-primary")]})
   [:span {:aria-hidden "true"}
    "← "]
   text])
