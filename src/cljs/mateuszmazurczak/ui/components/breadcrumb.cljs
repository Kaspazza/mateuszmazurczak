(ns mateuszmazurczak.ui.components.breadcrumb
  "Breadcrumb navigation component for displaying hierarchical page location.
  https://www.w3.org/WAI/ARIA/apg/patterns/breadcrumb/"
  (:require
   ["@radix-ui/react-slot" :refer [Slot]]
   ["lucide-react"         :refer [ChevronRight MoreHorizontal]]
   [mateuszmazurczak.utils.styles      :refer [merge-classes]]
   [reagent.core           :as r]))

(defn breadcrumb
  "Breadcrumb root component (nav element).
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:separator` - Optional custom separator component (defaults to ChevronRight)
  
  Example:
  [breadcrumb {}
    [breadcrumb-list {}
      [breadcrumb-item {}
        [breadcrumb-link {:href \"/\"} \"Home\"]]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:nav
         (-> props
             (assoc :aria-label "breadcrumb" :data-slot "breadcrumb" :class class)
             (dissoc :class-name :separator))]
        children))

(defn breadcrumb-list
  "Breadcrumb list component (ol element). Container for breadcrumb items.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [breadcrumb-list {}
    [breadcrumb-item {} ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:ol
    (-> props
        (assoc
         :data-slot "breadcrumb-list"
         :class
         (merge-classes
          "flex flex-wrap items-center gap-1.5 break-words text-sm text-muted-foreground sm:gap-2.5"
          class))
        (dissoc :class-name))]
   children))

(defn breadcrumb-item
  "Breadcrumb item component (li element). Wrapper for a single breadcrumb.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [breadcrumb-item {}
    [breadcrumb-link {:href \"/docs\"} \"Docs\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:li
         (-> props
             (assoc :data-slot "breadcrumb-item"
                    :class (merge-classes "inline-flex items-center gap-1.5" class))
             (dissoc :class-name))]
        children))

(defn breadcrumb-link
  "Breadcrumb link component (a element by default).
  
  Props:
  - `:href` - Link URL
  - `:as-child` - When true, uses Radix Slot for polymorphic rendering
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [breadcrumb-link {:href \"/docs\"} \"Documentation\"]
  [breadcrumb-link {:href \"/\" :as-child true}
    [:a {} \"Home\"]]"
  [{:keys [class as-child]
    :as props}
   &
   children]
  (let [component (if as-child Slot "a")]
    (into [:>
           component
           (-> props
               (assoc :data-slot "breadcrumb-link"
                      :class (merge-classes "transition-colors hover:text-foreground" class))
               (dissoc :class-name :as-child))]
          children)))

(defn breadcrumb-page
  "Breadcrumb page component (span element). Represents the current page.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [breadcrumb-page {} \"Current Page\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:span
         (-> props
             (assoc :role "link"
                    :aria-disabled "true"
                    :aria-current "page"
                    :data-slot "breadcrumb-page"
                    :class (merge-classes "font-normal text-foreground" class))
             (dissoc :class-name))]
        children))

(defn breadcrumb-separator
  "Breadcrumb separator component (li element). Visual separator between items.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:children` - Custom separator content (defaults to ChevronRight icon)
  
  Example:
  [breadcrumb-separator {}]
  [breadcrumb-separator {} \"/\"]"
  [{:keys [class]
    :as props}
   &
   children]
  [:li
   (-> props
       (assoc :role "presentation"
              :aria-hidden "true"
              :data-slot "breadcrumb-separator"
              :class (merge-classes "[&>svg]:w-3.5 [&>svg]:h-3.5" class))
       (dissoc :class-name))
   (if (seq children) (first children) (r/as-element [:> ChevronRight]))])

(defn breadcrumb-ellipsis
  "Breadcrumb ellipsis component (span element). Used for collapsed breadcrumbs.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [breadcrumb-ellipsis {}]"
  [{:keys [class]
    :as props}]
  [:span
   (-> props
       (assoc :role "presentation"
              :aria-hidden "true"
              :data-slot "breadcrumb-ellipsis"
              :class (merge-classes "flex h-9 w-9 items-center justify-center" class))
       (dissoc :class-name))
   (r/as-element [:> MoreHorizontal {:class "h-4 w-4"}])
   [:span {:class "sr-only"}
    "More"]])
