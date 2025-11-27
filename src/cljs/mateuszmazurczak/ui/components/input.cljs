(ns mateuszmazurczak.ui.components.input
  "Input component for forms.
  
  A styled native HTML input element with comprehensive styling for text, file,
  and other input types."
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn input
  "Renders a styled input element with comprehensive form styling.
  
  Props:
  - `:type`        - Input type (text, email, password, file, etc.)
  - `:value`       - Controlled input value
  - `:default-value` - Uncontrolled default value
  - `:placeholder` - Placeholder text
  - `:disabled`    - Whether input is disabled
  - `:required`    - Whether input is required
  - `:on-change`   - Change handler function
  - `:on-blur`     - Blur handler function
  - `:on-focus`    - Focus handler function
  - `:class`       - Additional Tailwind classes
  - All other standard HTML input attributes
  
  Features:
  - Comprehensive focus-visible ring styling
  - Disabled state with reduced opacity and cursor
  - File input specific styling
  - Placeholder text styling
  - Responsive text sizing (base on mobile, sm on desktop)
  - Full accessibility support
  
  Examples:
  
  Basic text input:
  ```clojure
  [input {:type \"text\"
          :placeholder \"Enter your name\"}]
  ```
  
  Controlled input:
  ```clojure
  [input {:type \"email\"
          :value @email
          :on-change #(reset! email (-> % .-target .-value))
          :placeholder \"email@example.com\"}]
  ```
  
  Disabled input:
  ```clojure
  [input {:type \"text\"
          :value \"Read-only value\"
          :disabled true}]
  ```
  
  File input:
  ```clojure
  [input {:type \"file\"
          :on-change handle-file-change
          :accept \".pdf,.doc,.docx\"}]
  ```
  
  Custom styling:
  ```clojure
  [input {:type \"password\"
          :placeholder \"Enter password\"
          :class \"border-red-500 focus-visible:ring-red-500\"}]
  ```"
  [{:keys [class type]
    :or {type "text"}
    :as props}]
  [:input
   (-> props
       (assoc :data-slot "input"
              :type type
              :class (merge-classes
                      (str "flex h-10 w-full rounded-md border border-input bg-background "
                           "px-3 py-2 text-base ring-offset-background "
                           "file:border-0 file:bg-transparent file:text-sm file:font-medium "
                           "file:text-foreground " "placeholder:text-muted-foreground "
                           "focus-visible:outline-none focus-visible:ring-2 "
                           "focus-visible:ring-ring focus-visible:ring-offset-2 "
                           "disabled:cursor-not-allowed disabled:opacity-50 " "md:text-sm")
                      class))
       (dissoc :class-name))])
