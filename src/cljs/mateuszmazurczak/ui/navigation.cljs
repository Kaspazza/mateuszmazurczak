(ns mateuszmazurczak.ui.navigation)

(defn navigation
  "Print a navigation button
  * The linked could be set with `href` or `on-click` and are higher priority"
  [{:keys [href text dark? on-click]}]
  [:a
   (merge (cond
            href {:href href}
            on-click {:on-click on-click})
          {:class ["font-semibold leading-7"
                   (when (some nil? [href on-click]) "cursor-pointer")
                   (if dark? "text-additional" "text-primary")]})
   text])
