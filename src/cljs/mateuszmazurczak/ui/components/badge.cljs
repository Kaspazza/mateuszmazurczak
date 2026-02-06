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
  - `:outline` - Outlined badge with border
  - `:ghost` - Ghost badge with no background
  - `:link` - Link-styled badge with underline on hover"
  [variant]
  (case variant
    :default "bg-primary text-primary-foreground [&_a]:hover:bg-primary/80"
    :secondary "bg-secondary text-secondary-foreground [&_a]:hover:bg-secondary/80"
    :destructive
    "bg-destructive/10 text-destructive [&_a]:hover:bg-destructive/20 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/20"
    :outline "border-border text-foreground [&_a]:hover:bg-muted [&_a]:hover:text-muted-foreground"
    :ghost "[&_a]:hover:bg-accent [&_a]:hover:text-accent-foreground"
    :link "text-primary underline-offset-4 [&_a]:hover:underline"
    "bg-primary text-primary-foreground [&_a]:hover:bg-primary/80"))

(defn badge
  "Badge component with support for multiple variants and polymorphic rendering.
  
  Props:
  - `:variant` - Badge variant (default: `:default`)
    - `:default` - Primary badge with solid background
    - `:secondary` - Secondary badge
    - `:destructive` - Destructive/error badge (red)
    - `:outline` - Outlined badge with border
    - `:ghost` - Ghost badge with no background
    - `:link` - Link-styled badge with underline on hover
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
     "h-5 gap-1 rounded-4xl border border-transparent px-2 py-0.5 text-xs font-medium transition-all has-data-[icon=inline-end]:pr-1.5 has-data-[icon=inline-start]:pl-1.5 [&>svg]:size-3! inline-flex items-center justify-center w-fit whitespace-nowrap shrink-0 [&>svg]:pointer-events-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive overflow-hidden group/badge"
     combined-classes (merge-classes base-classes (variant-classes variant) class)]
    (into [:>
           component
           (-> props
               (assoc :data-slot "badge" :data-variant (name variant) :class combined-classes)
               (dissoc :class-name :variant :as-child))]
          children)))
