(ns mateuszmazurczak.ui.components.sheet
  "Sheet (drawer/slide-out) component for modal content that slides in from edges.
  
  A sheet is a modal dialog that slides in from the edge of the screen.
  Commonly used for navigation menus, filters, and secondary content.
  Built on @radix-ui/react-dialog for accessibility.
  
  Docs: https://www.radix-ui.com/docs/primitives/components/dialog

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/dialog"
  (:require
   ["@radix-ui/react-dialog"      :as DialogPrimitive]
   ["lucide-react"                :refer [X]]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

;; ============================================================================
;; Root Components (Simple Aliases)
;; ============================================================================

(def sheet
  "Root component for Sheet. Manages open/closed state.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:default-open` - Uncontrolled default open state (boolean)
  - `:on-open-change` - Callback when open state changes (fn [open?])
  - `:modal` - Whether sheet is modal (default: true)
  
  Example:
  ```clojure
  [sheet {:open @open?
          :on-open-change #(reset! open? %)}
   [sheet-trigger {:as-child true}
    [button \"Open Sheet\"]]
   [sheet-content
    [sheet-header
     [sheet-title \"Sheet Title\"]
     [sheet-description \"Description\"]]
    [:div \"Content\"]
    [sheet-footer
     [button \"Save\"]]]]
  ```"
  (.-Root DialogPrimitive))

(def sheet-trigger
  "Trigger button that opens the sheet.
  
  Props:
  - `:as-child` - Merge props into child element (default: false)
  - Standard button props when not using as-child
  
  Example:
  ```clojure
  [sheet-trigger {:as-child true}
   [button {:variant :outline} \"Open Sheet\"]]
  ```"
  (.-Trigger DialogPrimitive))

(def sheet-close
  "Button that closes the sheet.
  
  Props:
  - `:as-child` - Merge props into child element (default: false)
  
  Example:
  ```clojure
  [sheet-close {:as-child true}
   [button {:variant :outline} \"Close\"]]
  ```"
  (.-Close DialogPrimitive))

(def sheet-portal
  "Portal component that renders sheet content in a portal (outside DOM hierarchy).
  
  Props:
  - `:container` - DOM element to portal into (default: document.body)
  
  Note: Usually used internally by sheet-content, rarely needed directly."
  (.-Portal DialogPrimitive))

;; ============================================================================
;; Sheet Overlay
;; ============================================================================

(defn sheet-overlay
  "Semi-transparent backdrop overlay that appears behind the sheet.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Dark semi-transparent background (bg-black/80)
  - Fade-in/out animations on open/close
  - Fixed positioning covering entire viewport
  - z-index 50 for proper layering
  
  Example:
  ```clojure
  [sheet-overlay]
  
  ;; Custom opacity
  [sheet-overlay {:class \"bg-black/60\"}]
  ```"
  [{:keys [class]
    :as props}]
  [:>
   (.-Overlay DialogPrimitive)
   (-> props
       (assoc :data-slot "sheet-overlay"
              :class (merge-classes (str
                                     "fixed inset-0 z-50 bg-black/80 "
                                     "data-[state=open]:animate-in data-[state=closed]:animate-out "
                                     "data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0")
                                    class))
       (dissoc :class-name))])

;; ============================================================================
;; Sheet Content with Variants
;; ============================================================================

(defn- side-classes
  "Returns Tailwind classes for the given side variant.
  
  Sides:
  - `:top` - Slide in from top edge
  - `:bottom` - Slide in from bottom edge
  - `:left` - Slide in from left edge
  - `:right` - Slide in from right edge (default)"
  [side]
  (case side
    :top (str "inset-x-0 top-0 border-b "
              "data-[state=closed]:slide-out-to-top "
              "data-[state=open]:slide-in-from-top")
    :bottom (str "inset-x-0 bottom-0 border-t "
                 "data-[state=closed]:slide-out-to-bottom "
                 "data-[state=open]:slide-in-from-bottom")
    :left (str "inset-y-0 left-0 h-full w-3/4 border-r " "data-[state=closed]:slide-out-to-left "
               "data-[state=open]:slide-in-from-left " "sm:max-w-sm")
    :right (str "inset-y-0 right-0 h-full w-3/4 border-l " "data-[state=closed]:slide-out-to-right "
                "data-[state=open]:slide-in-from-right " "sm:max-w-sm")
    ;; default fallback to right
    (str "inset-y-0 right-0 h-full w-3/4 border-l " "data-[state=closed]:slide-out-to-right "
         "data-[state=open]:slide-in-from-right " "sm:max-w-sm")))

(defn sheet-content
  "Main content container for the sheet with slide-in animation from specified side.
  
  Props:
  - `:side` - Direction sheet slides from: `:top`, `:bottom`, `:left`, `:right` (default)
  - `:class` - Additional Tailwind classes
  - `:children` - Child elements to render
  
  Features:
  - Slides in from specified edge with animation
  - Includes built-in close button (X icon) in top-right corner
  - Responsive sizing: 75% width on mobile, max-w-sm on desktop (for left/right)
  - Full width for top/bottom sheets
  - Shadow and border styling
  - Smooth entrance/exit transitions
  - Focus trap and keyboard navigation (Esc to close)
  
  Examples:
  
  Right side sheet (default):
  ```clojure
  [sheet-content
   [sheet-header
    [sheet-title \"Settings\"]
    [sheet-description \"Update your preferences\"]]
   [:div {:class \"py-4\"}
    \"Sheet content here\"]]
  ```
  
  Left side navigation:
  ```clojure
  [sheet-content {:side :left}
   [sheet-header
    [sheet-title \"Menu\"]]
   [:nav
    [:a {:href \"/home\"} \"Home\"]
    [:a {:href \"/about\"} \"About\"]]]
  ```
  
  Bottom sheet (mobile-friendly):
  ```clojure
  [sheet-content {:side :bottom}
   [sheet-header
    [sheet-title \"Filter Options\"]]
   [:div \"Filters...\"]]
  ```
  
  Top notification sheet:
  ```clojure
  [sheet-content {:side :top}
   [:div \"Important announcement\"]]
  ```"
  [{:keys [class side]
    :or {side :right}
    :as props}
   &
   children]
  [:>
   sheet-portal
   [:>
    (.-Overlay DialogPrimitive)
    {:data-slot "sheet-overlay"
     :class (merge-classes (str "fixed inset-0 z-50 bg-black/80 "
                                "data-[state=open]:animate-in data-[state=closed]:animate-out "
                                "data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0"))}]
   [:>
    (.-Content DialogPrimitive)
    (-> props
        (assoc :data-slot "sheet-content"
               :class (merge-classes
                       (str "fixed z-50 gap-4 bg-background p-6 shadow-lg "
                            "transition ease-in-out "
                            "data-[state=open]:animate-in data-[state=closed]:animate-out "
                            "data-[state=closed]:duration-300 data-[state=open]:duration-500")
                       (side-classes side)
                       class))
        (dissoc :class-name :side))
    ;; Render children
    (into [:<>] children)
    ;; Built-in close button
    [:>
     (.-Close DialogPrimitive)
     {:data-slot "sheet-close-button"
      :class (str "absolute right-4 top-4 rounded-sm opacity-70 "
                  "ring-offset-background transition-opacity "
                  "hover:opacity-100 "
                  "focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 "
                  "disabled:pointer-events-none " "data-[state=open]:bg-secondary")}
     [:> X {:class "h-4 w-4"}]
     [:span {:class "sr-only"}
      "Close"]]]])

;; ============================================================================
;; Layout Components
;; ============================================================================

(defn sheet-header
  "Header section for sheet content with consistent styling.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Vertical flex layout with spacing
  - Center-aligned on mobile, left-aligned on desktop
  - Typically contains sheet-title and sheet-description
  
  Example:
  ```clojure
  [sheet-header
   [sheet-title \"Edit Profile\"]
   [sheet-description \"Update your profile information\"]]
  ```"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :data-slot "sheet-header"
                    :class (merge-classes "flex flex-col space-y-2 text-center sm:text-left" class))
             (dissoc :class-name))]
        children))

(defn sheet-footer
  "Footer section for sheet content with button layout.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Responsive flex layout
  - Vertical column on mobile (reversed order)
  - Horizontal row on desktop with right alignment
  - Spacing between buttons
  
  Example:
  ```clojure
  [sheet-footer
   [button {:variant :outline :on-click close-sheet} \"Cancel\"]
   [button {:on-click save-changes} \"Save Changes\"]]
  ```"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :data-slot "sheet-footer"
                    :class (merge-classes (str "flex flex-col-reverse "
                                               "sm:flex-row sm:justify-end sm:space-x-2")
                                          class))
             (dissoc :class-name))]
        children))

;; ============================================================================
;; Text Components
;; ============================================================================

(defn sheet-title
  "Title component for sheet header.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Semantic heading with proper ARIA attributes
  - Large text with semibold weight
  - Required for accessibility (screen readers announce sheet by title)
  
  Example:
  ```clojure
  [sheet-title \"Account Settings\"]
  
  [sheet-title {:class \"text-xl\"} \"Custom Size Title\"]
  ```"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Title DialogPrimitive)
         (-> props
             (assoc :data-slot "sheet-title"
                    :class (merge-classes "text-lg font-semibold text-foreground" class))
             (dissoc :class-name))]
        children))

(defn sheet-description
  "Description component for sheet header.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Features:
  - Muted text color for secondary information
  - Proper ARIA description (announced by screen readers)
  - Smaller text size
  
  Example:
  ```clojure
  [sheet-description
   \"Make changes to your account settings here. Click save when done.\"]
  ```"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         (.-Description DialogPrimitive)
         (-> props
             (assoc :data-slot "sheet-description"
                    :class (merge-classes "text-sm text-muted-foreground" class))
             (dissoc :class-name))]
        children))
