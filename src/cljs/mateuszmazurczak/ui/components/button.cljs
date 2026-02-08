(ns mateuszmazurczak.ui.components.button
  "Button component with support for multiple variants and sizes.
  Supports polymorphic rendering via :as-child prop.

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/slot"
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
    :default
    "bg-primary text-primary-foreground hover:bg-primary/90 [&_a]:hover:bg-primary/80 focus-visible:ring-primary/20 dark:focus-visible:ring-primary/40 focus-visible:border-primary/40"
    :destructive
    "bg-destructive/10 hover:bg-destructive/20 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/20 text-destructive focus-visible:border-destructive/40 dark:hover:bg-destructive/30"
    :outline
    "border-border bg-background hover:bg-muted hover:text-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input/50 aria-expanded:bg-muted aria-expanded:text-foreground"
    :secondary
    "bg-secondary text-secondary-foreground hover:bg-secondary/80 aria-expanded:bg-secondary aria-expanded:text-secondary-foreground"
    :ghost
    "hover:bg-muted hover:text-foreground dark:hover:bg-muted/50 aria-expanded:bg-muted aria-expanded:text-foreground"
    :link "text-primary underline-offset-4 hover:underline"
    ;; default fallback
    "bg-primary text-primary-foreground [&_a]:hover:bg-primary/80"))

(defn- size-classes
  "Returns Tailwind classes for the given size.
  
  Sizes:
  - `:default` - Standard button height (h-8)
  - `:xs` - Extra small button (h-6)
  - `:sm` - Small button (h-7)
  - `:lg` - Large button (h-9)
  - `:icon` - Square button for icon-only (size-8)
  - `:icon-xs` - Extra small square icon button (size-6)
  - `:icon-sm` - Small square icon button (size-7)
  - `:icon-lg` - Large square icon button (size-9)"
  [size]
  (case size
    :default "h-8 gap-1.5 px-2.5 has-data-[icon=inline-end]:pr-2 has-data-[icon=inline-start]:pl-2"
    :xs
    "h-6 gap-1 rounded-[min(var(--radius-md),10px)] px-2 text-xs in-data-[slot=button-group]:rounded-lg has-data-[icon=inline-end]:pr-1.5 has-data-[icon=inline-start]:pl-1.5 [&_svg:not([class*='size-'])]:size-3"
    :sm
    "h-7 gap-1 rounded-[min(var(--radius-md),12px)] px-2.5 text-[0.8rem] in-data-[slot=button-group]:rounded-lg has-data-[icon=inline-end]:pr-1.5 has-data-[icon=inline-start]:pl-1.5 [&_svg:not([class*='size-'])]:size-3.5"
    :lg "h-9 gap-1.5 px-2.5 has-data-[icon=inline-end]:pr-3 has-data-[icon=inline-start]:pl-3"
    :icon "size-8"
    :icon-xs
    "size-6 rounded-[min(var(--radius-md),10px)] in-data-[slot=button-group]:rounded-lg [&_svg:not([class*='size-'])]:size-3"
    :icon-sm "size-7 rounded-[min(var(--radius-md),12px)] in-data-[slot=button-group]:rounded-lg"
    :icon-lg "size-9"
    ;; default fallback
    "h-8 gap-1.5 px-2.5 has-data-[icon=inline-end]:pr-2 has-data-[icon=inline-start]:pl-2"))

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
    - `:xs` - Extra small size
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
     "focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 rounded-lg border border-transparent bg-clip-padding text-sm font-medium focus-visible:ring-3 aria-invalid:ring-3 [&_svg:not([class*='size-'])]:size-4 inline-flex items-center justify-center whitespace-nowrap transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none shrink-0 [&_svg]:shrink-0 outline-none group/button select-none"
     combined-classes
     (merge-classes base-classes (variant-classes variant) (size-classes size) class)]
    (into [:>
           component
           (-> props
               (assoc :data-slot "button"
                      :data-variant (name variant)
                      :data-size (name size)
                      :class combined-classes)
               (dissoc :class-name :variant :size :as-child))]
          children)))
