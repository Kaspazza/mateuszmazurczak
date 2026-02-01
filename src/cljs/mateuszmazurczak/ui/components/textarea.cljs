(ns mateuszmazurczak.ui.components.textarea
  "Textarea component for forms.
  
  A styled native HTML textarea element with comprehensive styling for
  multi-line text input."
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn textarea
  "Renders a styled textarea element with comprehensive form styling.
  
  Props:
  - `:value`          - Controlled textarea value
  - `:default-value`  - Uncontrolled default value
  - `:placeholder`    - Placeholder text
  - `:disabled`       - Whether textarea is disabled
  - `:required`       - Whether textarea is required
  - `:rows`           - Number of visible text rows
  - `:cols`           - Visible width of the text control
  - `:on-change`      - Change handler function
  - `:on-blur`        - Blur handler function
  - `:on-focus`       - Focus handler function
  - `:class`          - Additional Tailwind classes
  - All other standard HTML textarea attributes
  
  Features:
  - Field-sizing-content for automatic height adjustment
  - Minimum height of 16 (4rem)
  - Comprehensive focus-visible ring styling with border transition
  - Disabled state with reduced opacity and cursor
  - Placeholder text styling
  - Responsive text sizing (base on mobile, sm on desktop)
  - ARIA invalid state styling with destructive colors
  - Shadow styling for depth
  - Full accessibility support
  
  Examples:
  
  Basic textarea:
  ```clojure
  [textarea {:placeholder \"Enter your message\"}]
  ```
  
  Controlled textarea:
  ```clojure
  [textarea {:value @message
             :on-change #(reset! message (-> % .-target .-value))
             :placeholder \"Type your message here...\"}]
  ```
  
  Disabled textarea:
  ```clojure
  [textarea {:value \"Read-only content\"
             :disabled true}]
  ```
  
  With specific rows:
  ```clojure
  [textarea {:rows 10
             :placeholder \"Enter detailed description\"}]
  ```
  
  Custom styling:
  ```clojure
  [textarea {:placeholder \"Custom styled textarea\"
             :class \"border-blue-500 focus-visible:ring-blue-500\"}]
  ```
  
  Invalid state (use with aria-invalid):
  ```clojure
  [textarea {:value @comment
             :aria-invalid (not (valid? @comment))
             :placeholder \"Required field\"}]
  ```"
  [{:keys [class auto-size?]
    :or {auto-size? true}
    :as props}]
  [:textarea
   (-> props
       (assoc :data-slot "textarea"
              :class (merge-classes
                      (str "border-input placeholder:text-muted-foreground "
                           "focus-visible:border-ring focus-visible:ring-ring/50 "
                           "aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 "
                           "aria-invalid:border-destructive dark:bg-input/30 "
                           "flex min-h-16 w-full rounded-md border "
                           "bg-transparent px-3 py-2 text-base shadow-xs "
                           "transition-[color,box-shadow] outline-none focus-visible:ring-[3px] "
                           "disabled:cursor-not-allowed disabled:opacity-50 md:text-sm "
                           (if auto-size?
                             "field-sizing-content"
                             "overflow-y-auto resize-y"))
                      class))
       (dissoc :class-name :auto-size?))])
