(ns mateuszmazurczak.ui.components.badge
  "Badge component with support for multiple variants.
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
  - `:default` - Primary badge with solid background
  - `:secondary` - Secondary badge
  - `:destructive` - Destructive/error badge (red)
  - `:outline` - Outlined badge with transparent background"
  [variant]
  (case variant
    :default "border-transparent bg-primary text-primary-foreground [a&]:hover:bg-primary/90"
    :secondary
    "border-transparent bg-secondary text-secondary-foreground [a&]:hover:bg-secondary/90"
    :destructive
    "border-transparent bg-destructive text-white [a&]:hover:bg-destructive/90 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60"
    :outline "text-foreground [a&]:hover:bg-accent [a&]:hover:text-accent-foreground"
    "border-transparent bg-primary text-primary-foreground [a&]:hover:bg-primary/90"))

(defn badge
  "Badge component with support for multiple variants and polymorphic rendering.
  
  Props:
  - `:variant` - Badge variant (default: `:default`)
    - `:default` - Primary badge with solid background
    - `:secondary` - Secondary badge
    - `:destructive` - Destructive/error badge (red)
    - `:outline` - Outlined badge
  - `:as-child` - When true, uses Radix Slot for polymorphic rendering
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:on-click` - Click handler function
  
  Examples:
  ;; Default badge
  [badge \"New\"]
  
  ;; Destructive badge
  [badge {:variant :destructive} \"Error\"]
  
  ;; Outline badge with custom class
  [badge {:variant :outline :class \"uppercase\"} \"Beta\"]
  
  ;; As child (polymorphic)
  [badge {:as-child true}
    [:a {:href \"/status\"} \"Active\"]]"
  [{:keys [variant class as-child]
    :or {variant :default}
    :as props}
   &
   children]
  (let
    [component (if as-child Slot "span")
     base-classes
     "inline-flex items-center justify-center rounded-full border px-2 py-0.5 text-xs font-medium w-fit whitespace-nowrap shrink-0 [&>svg]:size-3 gap-1 [&>svg]:pointer-events-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive transition-[color,box-shadow] overflow-hidden"
     combined-classes (merge-classes base-classes (variant-classes variant) class)]
    (into [:>
           component
           (-> props
               (assoc :data-slot "badge" :class combined-classes)
               (dissoc :class-name :variant :as-child))]
          children)))
