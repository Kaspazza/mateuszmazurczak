(ns mateuszmazurczak.ui.components.button
  "Button component with support for multiple variants and sizes.
  Supports polymorphic rendering via :as-child prop."
  (:require
   ["@radix-ui/react-slot"        :refer [Slot]]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn- variant-classes
  "Returns Tailwind classes for the given variant.
  
  Variants:
  - `:default` - Primary button with solid background
  - `:destructive` - Destructive action button (delete, remove, etc.)
  - `:outline` - Outlined button with transparent background
  - `:secondary` - Secondary action button
  - `:ghost` - Ghost button with no background
  - `:link` - Link-styled button with underline"
  [variant]
  (case variant
    :default "bg-primary text-primary-foreground hover:bg-primary/90"
    :destructive "bg-destructive text-destructive-foreground hover:bg-destructive/90"
    :outline "border border-input bg-background hover:bg-accent hover:text-accent-foreground"
    :secondary "bg-secondary text-secondary-foreground hover:bg-secondary/80"
    :ghost "hover:bg-accent hover:text-accent-foreground"
    :link "text-primary underline-offset-4 hover:underline"
    ;; default fallback
    "bg-primary text-primary-foreground hover:bg-primary/90"))

(defn- size-classes
  "Returns Tailwind classes for the given size.
  
  Sizes:
  - `:default` - Standard button height (h-10)
  - `:sm` - Small button (h-9)
  - `:lg` - Large button (h-11)
  - `:icon` - Square button for icon-only (h-10 w-10)"
  [size]
  (case size
    :default "h-10 px-4 py-2"
    :xs "h-6 rounded-sm px-2"
    :sm "h-9 rounded-md px-3"
    :lg "h-11 rounded-md px-8"
    :icon "h-10 w-10"
    ;; default fallback
    "h-10 px-4 py-2"))

(defn button
  "Button component with support for multiple variants, sizes, and polymorphic rendering.
  
  Props:
  - `:variant` - Button variant (default: `:default`)
    - `:default` - Primary button
    - `:destructive` - Destructive action button
    - `:outline` - Outlined button
    - `:secondary` - Secondary action button
    - `:ghost` - Ghost button
    - `:link` - Link-styled button
  - `:size` - Button size (default: `:default`)
    - `:default` - Standard size
    - `:sm` - Small size
    - `:lg` - Large size
    - `:icon` - Square size for icon-only buttons
  - `:as-child` - When true, uses Radix Slot for polymorphic rendering
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:disabled` - Boolean to disable the button
  - `:type` - Button type (\"button\", \"submit\", \"reset\")
  - `:on-click` - Click handler function
  
  Examples:
  ;; Primary button
  [button {:on-click #(js/console.log \"clicked\")} \"Click me\"]
  
  ;; Destructive button
  [button {:variant :destructive} \"Delete\"]
  
  ;; Small outlined button
  [button {:variant :outline :size :sm} \"Cancel\"]
  
  ;; Icon button
  [button {:variant :ghost :size :icon}
    [:> TrashIcon]]
  
  ;; As child (polymorphic)
  [button {:as-child true}
    [:a {:href \"/login\"} \"Login\"]]"
  [{:keys [variant size class as-child]
    :or {variant :default
         size :default}
    :as props}
   &
   children]
  (let
    [component (if as-child Slot "button")
     base-classes
     "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"
     combined-classes
     (merge-classes base-classes (variant-classes variant) (size-classes size) class)]
    (into [:>
           component
           (-> props
               (assoc :data-slot "button" :class combined-classes)
               (dissoc :class-name :variant :size :as-child))]
          children)))
