(ns mateuszmazurczak.ports.theme
  "Theme management port.
   
   Provides API for applying theme changes to the DOM.
   Theme persistence is handled by the cache system (see domain.cache.registry).")

(defn apply-theme!
  "Apply theme to document root element.
   
   Arguments:
   - theme: Theme keyword (:light/:dark)
   
   Adds appropriate class to <html> element and removes others."
  [theme]
  (when-let [root js/document.documentElement]
    (let [classList (.-classList root)]
      (.remove classList "light" "dark")
      (.add classList (name theme)))))
