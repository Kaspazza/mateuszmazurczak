(ns mateuszmazurczak.ui.components.field
  "Field component for building accessible form layouts.
  
  Provides a set of composable components for creating form fields with
  labels, descriptions, errors, and various layout orientations."
  (:require
   [mateuszmazurczak.ui.components.label     :as label-comp]
   [mateuszmazurczak.ui.components.separator :as separator-comp]
   [mateuszmazurczak.utils.styles            :refer [merge-classes]]
   [reagent.core                 :as r]))

(defn field-set
  "Container for grouping multiple related fields.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [field-set {}
    [field-legend \"Account Settings\"]
    [field {} ...]
    [field {} ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:fieldset
         (-> props
             (assoc :data-slot "field-set"
                    :class
                    (merge-classes
                     "flex flex-col gap-6"
                     "has-[>[data-slot=checkbox-group]]:gap-3 has-[>[data-slot=radio-group]]:gap-3"
                     class))
             (dissoc :class-name))]
        children))

(defn field-legend
  "Legend element for field-set, with support for different variants.
  
  Props:
  - `:variant` - Visual style (`:legend` (default) or `:label`)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Default legend variant
  [field-legend {} \"Personal Information\"]
  
  ;; Label-sized variant
  [field-legend {:variant :label} \"Account Details\"]"
  [{:keys [class variant]
    :or {variant :legend}
    :as props}
   &
   children]
  (into [:legend
         (-> props
             (assoc :data-slot "field-legend"
                    :data-variant (name variant)
                    :class (merge-classes "mb-3 font-medium" "data-[variant=legend]:text-base"
                                          "data-[variant=label]:text-sm" class))
             (dissoc :class-name :variant))]
        children))

(defn field-group
  "Container for grouping multiple fields with consistent spacing.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [field-group {}
    [field {} ...]
    [field {} ...]
    [field {} ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:div
    (->
      props
      (assoc
       :data-slot "field-group"
       :class
       (merge-classes
        "group/field-group @container/field-group flex w-full flex-col gap-7 data-[slot=checkbox-group]:gap-3 [&>[data-slot=field-group]]:gap-4"
        class))
      (dissoc :class-name))]
   children))

(defn- field-orientation-classes
  "Returns Tailwind classes for the given field orientation."
  [orientation]
  (case orientation
    :vertical ["flex-col [&>*]:w-full [&>.sr-only]:w-auto"]
    :horizontal
    ["flex-row items-center"
     "[&>[data-slot=field-label]]:flex-auto"
     "has-[>[data-slot=field-content]]:items-start has-[>[data-slot=field-content]]:[&>[role=checkbox],[role=radio]]:mt-px"]
    :responsive
    ["flex-col [&>*]:w-full [&>.sr-only]:w-auto @md/field-group:flex-row @md/field-group:items-center @md/field-group:[&>*]:w-auto"
     "@md/field-group:[&>[data-slot=field-label]]:flex-auto"
     "@md/field-group:has-[>[data-slot=field-content]]:items-start @md/field-group:has-[>[data-slot=field-content]]:[&>[role=checkbox],[role=radio]]:mt-px"]
    ;; default fallback to vertical
    ["flex-col [&>*]:w-full [&>.sr-only]:w-auto"]))

(defn field
  "Main field container for form controls with flexible orientation.
  
  Props:
  - `:orientation` - Layout direction (`:vertical` (default), `:horizontal`, or `:responsive`)
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Orientation variants:
  - `:vertical` - Stack label and control vertically
  - `:horizontal` - Place label and control side-by-side
  - `:responsive` - Vertical on mobile, horizontal on larger screens
  
  Examples:
  ;; Vertical field (default)
  [field {}
    [field-label {:html-for \"email\"} \"Email\"]
    [input {:id \"email\" :type \"email\"}]]
  
  ;; Horizontal field
  [field {:orientation :horizontal}
    [field-label {:html-for \"name\"} \"Name\"]
    [input {:id \"name\"}]]
  
  ;; Responsive field
  [field {:orientation :responsive}
    [field-label {:html-for \"bio\"} \"Bio\"]
    [field-content
      [textarea {:id \"bio\"}]
      [field-description \"Tell us about yourself\"]]]"
  [{:keys [class orientation]
    :or {orientation :vertical}
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :role "group"
                    :data-slot "field"
                    :data-orientation (name orientation)
                    :class (merge-classes
                            "group/field flex w-full gap-3 data-[invalid=true]:text-destructive"
                            (field-orientation-classes orientation)
                            class))
             (dissoc :class-name :orientation))]
        children))

(defn field-content
  "Container for field control and its description/error messages.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [field-content {}
    [input {:type \"text\"}]
    [field-description \"Your full name\"]
    [field-error {:errors validation-errors}]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :data-slot "field-content"
                    :class (merge-classes
                            "group/field-content flex flex-1 flex-col gap-1.5 leading-snug"
                            class))
             (dissoc :class-name))]
        children))

(defn field-label
  "Label component for fields, wrapping the base label component with field-specific styling.
  
  Props:
  - `:html-for` - ID of the associated form control
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Features:
  - Supports nested fields for complex layouts
  - Shows selected state styling
  - Handles disabled state opacity
  
  Examples:
  ;; Basic label
  [field-label {:html-for \"username\"} \"Username\"]
  
  ;; Label with nested field (for radio/checkbox cards)
  [field-label {}
    [field {}
      [radio-group-item {:value \"option1\"}]
      [field-title \"Option 1\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [label-comp/label
    (->
      props
      (assoc
       :data-slot "field-label"
       :class
       (merge-classes
        "group/field-label peer/field-label flex w-fit gap-2 leading-snug group-data-[disabled=true]/field:opacity-50"
        "has-[>[data-slot=field]]:w-full has-[>[data-slot=field]]:flex-col has-[>[data-slot=field]]:rounded-md has-[>[data-slot=field]]:border [&>*]:data-[slot=field]:p-4"
        "has-data-[state=checked]:bg-primary/5 has-data-[state=checked]:border-primary dark:has-data-[state=checked]:bg-primary/10"
        class))
      (dissoc :class-name))]
   children))

(defn field-title
  "Title element for field labels, typically used in complex field layouts.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [field-title {} \"Payment Method\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:div
    (->
      props
      (assoc
       :data-slot "field-label"
       :class
       (merge-classes
        "flex w-fit items-center gap-2 text-sm leading-snug font-medium group-data-[disabled=true]/field:opacity-50"
        class))
      (dissoc :class-name))]
   children))

(defn field-description
  "Descriptive text for a field, providing context or instructions.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [field-description {} \"Your email will not be shared with anyone.\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:p
    (->
      props
      (assoc
       :data-slot "field-description"
       :class
       (merge-classes
        "text-muted-foreground text-sm leading-normal font-normal group-has-[[data-orientation=horizontal]]/field:text-balance"
        "last:mt-0 nth-last-2:-mt-1 [[data-variant=legend]+&]:-mt-1.5"
        "[&>a:hover]:text-primary [&>a]:underline [&>a]:underline-offset-4" class))
      (dissoc :class-name))]
   children))

(defn field-separator
  "Visual separator for fields, with optional label text.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Examples:
  ;; Simple separator
  [field-separator]
  
  ;; Separator with label
  [field-separator {} \"or\"]"
  [{:keys [class]
    :as props}
   &
   children]
  [:div
   (-> props
       (assoc :data-slot "field-separator"
              :data-content (boolean (seq children))
              :class (merge-classes
                      "relative -my-2 h-5 text-sm group-data-[variant=outline]/field-group:-mb-2"
                      class))
       (dissoc :class-name))
   [separator-comp/separator {:class "absolute inset-0 top-1/2"}]
   (when (seq children)
     (into [:span {:data-slot "field-separator-content"
                   :class "bg-background text-muted-foreground relative mx-auto block w-fit px-2"}]
           children))])

(defn field-error
  "Error message display for field validation errors.
  
  Props:
  - `:errors` - Vector of error maps with `:message` key
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Features:
  - Automatically deduplicates error messages
  - Displays single error as text
  - Displays multiple errors as a bulleted list
  - Returns nil if no errors or children
  
  Examples:
  ;; With explicit children
  [field-error {} \"This field is required\"]
  
  ;; With error objects
  [field-error {:errors [{:message \"Email is invalid\"}]}]
  
  ;; Multiple errors
  [field-error {:errors [{:message \"Too short\"}
                         {:message \"Must contain a number\"}]}]"
  [{:keys [class errors]
    :as props}
   &
   children]
  (let [content (r/atom nil)]
    (r/create-class
     {:display-name "field-error"
      :reagent-render
      (fn [{:keys [class errors]
            :as props}
           &
           children]
        (reset! content (cond
                          ;; If children provided, use them
                          (seq children) children
                          ;; No errors, return nil
                          (empty? errors) nil
                          ;; Single error, display as text
                          (= 1 (count errors)) (get-in (first errors) [:message])
                          ;; Multiple errors
                          :else (let [unique-errors (->> errors
                                                         (filter :message)
                                                         (map (fn [error] [(:message error) error]))
                                                         (into {})
                                                         vals)]
                                  (when (seq unique-errors)
                                    (if (= 1 (count unique-errors))
                                      (:message (first unique-errors))
                                      [:ul {:class "ml-4 flex list-disc flex-col gap-1"}
                                       (for [[idx error] (map-indexed vector unique-errors)]
                                         (when-let [msg (:message error)]
                                           ^{:key idx} [:li msg]))])))))
        (when @content
          [:div
           (-> props
               (assoc :role "alert"
                      :data-slot "field-error"
                      :class (merge-classes "text-destructive text-sm font-normal" class))
               (dissoc :class-name :errors))
           @content]))})))
