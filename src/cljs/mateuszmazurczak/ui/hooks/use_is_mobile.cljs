(ns mateuszmazurczak.ui.hooks.use-is-mobile
  "React hook for detecting mobile viewport breakpoints.

  Provides a reactive hook that tracks whether the current viewport is mobile-sized
  (width < 768px). Updates automatically when the viewport crosses the breakpoint.

  Uses CSS Media Queries (`matchMedia`) for efficient breakpoint detection - only
  fires when crossing the threshold, not on every resize event."
  (:require
   [reagent.hooks :as hooks]))

(def ^:private mobile-breakpoint
  "Breakpoint width in pixels. Viewports below this are considered mobile."
  768)

(defn use-is-mobile
  "Returns true if viewport is mobile-sized (< 768px), false otherwise.

  This is a React hook - components using it will re-render when the viewport
  crosses the mobile breakpoint.

  The value is nil during initial render, then becomes boolean once the effect runs.
  The `boolean` coercion ensures it returns false instead of nil initially.

  ## Usage

  ```clojure
  (defn responsive-nav []
    (let [mobile? (use-is-mobile)]
      (if mobile?
        [mobile-navigation]
        [desktop-navigation])))
  ```

  ```clojure
  (defn adaptive-layout []
    (let [mobile? (use-is-mobile)]
      [:div {:class (if mobile? \"flex-col\" \"flex-row\")}
       ;; content
       ]))
  ```

  ## Technical Details

  - Uses `matchMedia` API for efficient breakpoint detection
  - Only fires when crossing the 768px threshold (not on every resize)
  - Per-component state (each component using this hook has its own state)
  - Automatically sets up and cleans up listener on mount/unmount
  - Breakpoint: 768px (standard mobile/tablet boundary)

  ## Return Value

  Boolean (coerced from nil during initial render):
  - `true` - Viewport width < 768px (mobile)
  - `false` - Viewport width >= 768px (desktop/tablet)"
  []
  (let [[is-mobile set-is-mobile] (hooks/use-state nil)]
    (hooks/use-effect
     (fn []
       (let [mql (.matchMedia js/window (str "(max-width: " (dec mobile-breakpoint) "px)"))
             on-change (fn [] (set-is-mobile (< (.-innerWidth js/window) mobile-breakpoint)))]
         (.addEventListener mql "change" on-change)
         (set-is-mobile (< (.-innerWidth js/window) mobile-breakpoint))
         ;; Return cleanup function
         #(.removeEventListener mql "change" on-change)))
     []) ;; Empty deps array - run once on mount
    (boolean is-mobile)))
