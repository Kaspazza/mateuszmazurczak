(ns mateuszmazurczak.ui.components.notification
  "Toast notification component using Sonner.
  
  This component provides a toast notification system with support for
  different toast types (success, info, warning, error, loading) and
  custom icons from lucide-react.
  
  Usage:
  1. Add the Toaster component to your app root
  2. Use the toast function to show notifications
  
  Example:
  ;; In your root component
  [toaster]
  
  ;; Trigger a toast
  (toast \"Event has been created\"
         {:description \"Sunday, December 03, 2023 at 9:00 AM\"
          :action {:label \"Undo\"
                   :on-click #(js/console.log \"Undo\")}})

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["lucide-react" :refer [CircleCheckIcon InfoIcon Loader2Icon OctagonXIcon TriangleAlertIcon]]
   ["sonner"       :refer [Toaster toast]]
   [reagent.core   :as r]))

(defn toaster
  "Toast notification provider component.
  
  This component should be placed once in your app root component.
  It renders the toast container with custom styling and icons.
  
  Props:
  - All props from Sonner's ToasterProps are supported
  - Additional Tailwind classes can be passed via :class
  
  The component uses CSS variables from your Tailwind theme:
  - --popover for background
  - --popover-foreground for text
  - --border for borders
  - --radius for border radius
  
  Custom icons are provided for each toast type:
  - success: CircleCheckIcon
  - info: InfoIcon
  - warning: TriangleAlertIcon
  - error: OctagonXIcon
  - loading: Loader2Icon (with spin animation)"
  [& [{:as props}]]
  [:>
   Toaster
   (merge {:className "toaster group"
           :position "top-right"
           :icons (clj->js {:success (r/as-element [:> CircleCheckIcon {:className "size-4"}])
                            :info (r/as-element [:> InfoIcon {:className "size-4"}])
                            :warning (r/as-element [:> TriangleAlertIcon {:className "size-4"}])
                            :error (r/as-element [:> OctagonXIcon {:className "size-4"}])
                            :loading (r/as-element
                                      [:> Loader2Icon {:className "size-4 animate-spin"}])})
           :style (clj->js {"--normal-bg" "var(--popover)"
                            "--normal-text" "var(--popover-foreground)"
                            "--normal-border" "var(--border)"
                            "--border-radius" "var(--radius)"})}
          props)])

(defn show-toast
  "Display a toast notification.
  
  This is a wrapper around Sonner's toast function that provides
  a more Clojure-friendly API.
  
  Args:
  - message: String message to display
  - options: Optional map with keys:
    - :description - Additional description text
    - :action - Map with :label and :on-click for an action button
    - :duration - How long to show the toast (ms)
    - :position - Position of toast (e.g., \"top-right\", \"bottom-center\")
    - :cancel - Map with :label and :on-click for a cancel button
    - :id - Custom ID for the toast
    - :important - Boolean, if true prevents dismissal
    - :on-dismiss - Callback when toast is dismissed
    - :on-auto-close - Callback when toast auto-closes
  
  Returns:
  - Toast ID that can be used with dismiss-toast
  
  Examples:
  ;; Simple toast
  (show-toast \"Success!\")
  
  ;; Toast with description
  (show-toast \"Event created\"
              {:description \"Sunday, December 03, 2023 at 9:00 AM\"})
  
  ;; Toast with action
  (show-toast \"File deleted\"
              {:action {:label \"Undo\"
                        :on-click #(js/console.log \"Undo clicked\")}})
  
  ;; Custom position and duration
  (show-toast \"Warning!\"
              {:duration 5000
               :position \"top-center\"})"
  ([message] (show-toast message nil))
  ([message
    {:keys [description action duration position cancel id important on-dismiss on-auto-close]
     :or {position "top-right"}
     :as _options}]
   (let [opts (cond-> {}
                description (assoc :description description)
                action (assoc :action
                              (clj->js (-> action
                                           (update :on-click (fn [f] #(f)))
                                           (update :label identity))))
                cancel (assoc :cancel
                              (clj->js (-> cancel
                                           (update :on-click (fn [f] #(f)))
                                           (update :label identity))))
                duration (assoc :duration duration)
                position (assoc :position position)
                id (assoc :id id)
                important (assoc :important important)
                on-dismiss (assoc :onDismiss (fn [_] (on-dismiss)))
                on-auto-close (assoc :onAutoClose (fn [_] (on-auto-close))))]
     (toast message (clj->js opts)))))

(defn show-success
  "Display a success toast with outline styling.
  
  Uses consistent green color scheme for success messages:
  - Light mode: green-600
  - Dark mode: green-400
  
  Args:
  - message: String message to display
  - options: Optional map (same as show-toast)
  
  Example:
  (show-success \"Action completed successfully!\")"
  ([message] (show-success message nil))
  ([message options]
   (let [styled-options
         (assoc options
                :style
                {"--normal-bg" "var(--background)"
                 "--normal-text" "light-dark(var(--color-green-600), var(--color-green-400))"
                 "--normal-border" "light-dark(var(--color-green-600), var(--color-green-400))"})]
     (toast.success message (clj->js styled-options)))))

(defn show-error
  "Display an error toast with destructive outline styling.
  
  Uses the theme's destructive color for error messages.
  
  Args:
  - message: String message to display
  - options: Optional map (same as show-toast)
  
  Example:
  (show-error \"Oops, there was an error processing your request.\")"
  ([message] (show-error message nil))
  ([message options]
   (let [styled-options (assoc options
                               :style
                               {"--normal-bg" "var(--background)"
                                "--normal-text" "var(--destructive)"
                                "--normal-border" "var(--destructive)"})]
     (toast.error message (clj->js styled-options)))))

(defn show-info
  "Display an info toast with outline styling.
  
  Uses consistent sky/blue color scheme for informational messages:
  - Light mode: sky-600
  - Dark mode: sky-400
  
  Args:
  - message: String message to display
  - options: Optional map (same as show-toast)
  
  Example:
  (show-info \"This is for your information, please note.\")"
  ([message] (show-info message nil))
  ([message options]
   (let [styled-options
         (assoc options
                :style
                {"--normal-bg" "var(--background)"
                 "--normal-text" "light-dark(var(--color-sky-600), var(--color-sky-400))"
                 "--normal-border" "light-dark(var(--color-sky-600), var(--color-sky-400))"})]
     (toast.info message (clj->js styled-options)))))

(defn show-warning
  "Display a warning toast with outline styling.
  
  Uses consistent amber/yellow color scheme for warning messages:
  - Light mode: amber-600
  - Dark mode: amber-400
  
  Args:
  - message: String message to display
  - options: Optional map (same as show-toast)
  
  Example:
  (show-warning \"Warning: Please check the entered data.\")"
  ([message] (show-warning message nil))
  ([message options]
   (let [styled-options
         (assoc options
                :style
                {"--normal-bg" "var(--background)"
                 "--normal-text" "light-dark(var(--color-amber-600), var(--color-amber-400))"
                 "--normal-border" "light-dark(var(--color-amber-600), var(--color-amber-400))"})]
     (toast.warning message (clj->js styled-options)))))

(defn show-loading
  "Display a loading toast.
  
  Args:
  - message: String message to display
  - options: Optional map (same as show-toast)
  
  Returns:
  - Toast ID that can be used to update or dismiss the toast
  
  Example:
  (let [toast-id (show-loading \"Uploading file...\")]
    ;; Later, dismiss it
    (dismiss-toast toast-id))"
  ([message] (show-loading message nil))
  ([message options] (toast.loading message (clj->js (or options {})))))

(defn show-promise
  "Display a toast for a promise.
  
  Automatically shows loading state, then success or error based on promise result.
  
  Args:
  - promise: JavaScript promise or ClojureScript promise
  - messages: Map with :loading, :success, and :error messages
  - options: Optional map (same as show-toast)
  
  Example:
  (show-promise
    (js/fetch \"/api/data\")
    {:loading \"Fetching data...\"
     :success \"Data loaded!\"
     :error \"Failed to load data\"})"
  [promise messages options]
  (toast.promise promise (clj->js messages) (clj->js (or options {}))))

(defn dismiss-toast
  "Dismiss a specific toast by ID, or all toasts if no ID provided.
  
  Args:
  - toast-id: Optional toast ID to dismiss (from show-toast, show-loading, etc.)
  
  Examples:
  ;; Dismiss specific toast
  (dismiss-toast \"my-toast-id\")
  
  ;; Dismiss all toasts
  (dismiss-toast)"
  ([] (toast.dismiss))
  ([toast-id] (toast.dismiss toast-id)))

(defn custom-toast
  "Display a custom toast with a custom component.
  
  Args:
  - component: Reagent component or React element to render
  - options: Optional map (same as show-toast)
  
  Example:
  (custom-toast
    [:div {:class \"flex items-center gap-2\"}
     [:span \"Custom content\"]]
    {:duration 3000})"
  [component options]
  (toast.custom (if (vector? component)
                  ;; Convert Reagent component to React element
                  (r/as-element component)
                  component)
                (clj->js (or options {}))))
