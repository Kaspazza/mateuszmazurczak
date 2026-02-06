(ns mateuszmazurczak.ui.components.table
  "Table component primitives for building data tables.
  Provides semantic HTML table elements with consistent Tailwind styling.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn table
  "Table wrapper component with overflow container.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the table element
  
  Example:
  [table {:class \"my-4\"}
   [table-header ...]
   [table-body ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    [:div {:data-slot "table-container"
           :class "relative w-full overflow-x-auto"}
     (into [:table
            (merge {:data-slot "table"
                    :class (merge-classes "w-full caption-bottom text-sm" class)}
                   rest-props)]
           children)]))

(defn table-header
  "Table header (thead) component.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the thead element
  
  Example:
  [table-header {}
   [table-row {}
    [table-head {} \"Column 1\"]
    [table-head {} \"Column 2\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into [:thead
           (merge {:data-slot "table-header"
                   :class (merge-classes "[&_tr]:border-b" class)}
                  rest-props)]
          children)))

(defn table-body
  "Table body (tbody) component.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the tbody element
  
  Example:
  [table-body {}
   [table-row {}
    [table-cell {} \"Data 1\"]
    [table-cell {} \"Data 2\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into [:tbody
           (merge {:data-slot "table-body"
                   :class (merge-classes "" class)}
                  rest-props)]
          children)))

(defn table-footer
  "Table footer (tfoot) component.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the tfoot element
  
  Example:
  [table-footer {}
   [table-row {}
    [table-cell {:col-span 2} \"Total: 100\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into [:tfoot
           (merge {:data-slot "table-footer"
                   :class (merge-classes "bg-muted/50 border-t font-medium [&>tr]:last:border-b-0"
                                         class)}
                  rest-props)]
          children)))

(defn table-row
  "Table row (tr) component with hover and selection states.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the tr element
  
  Features:
  - Hover effect (hover:bg-muted/50)
  - Selection state styling via data-state attribute
  - Border between rows
  
  Example:
  [table-row {:data-state \"selected\"}
   [table-cell {} \"Data 1\"]
   [table-cell {} \"Data 2\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into [:tr
           (merge {:data-slot "table-row"
                   :class
                   (merge-classes
                    "hover:bg-muted/50 data-[state=selected]:bg-muted border-b transition-colors"
                    class)}
                  rest-props)]
          children)))

(defn table-head
  "Table header cell (th) component.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the th element
  
  Features:
  - Left-aligned text
  - Medium font weight
  - Proper spacing for checkboxes
  - No text wrapping by default
  
  Example:
  [table-head {} \"Column Name\"]
  [table-head {:class \"text-right\"} \"Amount\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into
     [:th
      (merge
       {:data-slot "table-head"
        :class
        (merge-classes
         "text-foreground h-10 px-2 text-left align-middle font-medium whitespace-nowrap [&:has([role=checkbox])]:pr-0 [&>[role=checkbox]]:translate-y-[2px]"
         class)}
       rest-props)]
     children)))

(defn table-cell
  "Table cell (td) component.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the td element
  
  Features:
  - Consistent padding
  - Middle vertical alignment
  - Proper spacing for checkboxes
  - No text wrapping by default
  
  Example:
  [table-cell {} \"Cell content\"]
  [table-cell {:class \"text-right font-mono\"} \"$1,234\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into
     [:td
      (merge
       {:data-slot "table-cell"
        :class
        (merge-classes
         "p-2 align-middle whitespace-nowrap [&:has([role=checkbox])]:pr-0 [&>[role=checkbox]]:translate-y-[2px]"
         class)}
       rest-props)]
     children)))

(defn table-caption
  "Table caption component for describing the table.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are forwarded to the caption element
  
  Example:
  [table {}
   [table-caption {} \"List of users in the system\"]
   [table-header ...]
   [table-body ...]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [rest-props (dissoc props :class)]
    (into [:caption
           (merge {:data-slot "table-caption"
                   :class (merge-classes "text-muted-foreground mt-4 text-sm" class)}
                  rest-props)]
          children)))
