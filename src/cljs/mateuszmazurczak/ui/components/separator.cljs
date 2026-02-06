(ns mateuszmazurczak.ui.components.separator
  "Separator component for visual dividers.
  
  A thin line that visually or semantically separates content.
  Built on @radix-ui/react-separator for accessibility.
  
  Docs: https://www.radix-ui.com/docs/primitives/components/separator

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/separator"
  (:require
   ["@radix-ui/react-separator"   :as SeparatorPrimitive]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn separator
  "Renders a visual separator line (horizontal or vertical).
  
  Props:
  - `:orientation` - Direction of separator: `:horizontal` (default) or `:vertical`
  - `:decorative`  - Whether separator is decorative (true, default) or semantic (false)
                     When true, separator is hidden from screen readers
                     When false, separator is announced as a semantic divider
  - `:class`       - Additional Tailwind classes
  - `:as-child`    - Merge props into child element (Radix Slot pattern)
  
  Features:
  - Horizontal: 1px height, full width
  - Vertical: full height, 1px width
  - Semantic HTML with proper ARIA roles
  - Flexible shrink-0 behavior for layout
  
  Examples:
  
  Horizontal separator (default):
  ```clojure
  [separator]
  ```
  
  Vertical separator:
  ```clojure
  [:div {:class \"flex h-4 items-center\"}
   [:span \"Item 1\"]
   [separator {:orientation :vertical
               :class \"mx-2\"}]
   [:span \"Item 2\"]]
  ```
  
  Semantic separator for screen readers:
  ```clojure
  [separator {:decorative false}]
  ```
  
  Custom styling:
  ```clojure
  [separator {:class \"bg-red-500\"}]
  ```
  
  In a card layout:
  ```clojure
  [:div {:class \"rounded-lg border p-6\"}
   [:h2 \"Header\"]
   [separator {:class \"my-4\"}]
   [:p \"Content\"]]
  ```"
  [{:keys [class orientation decorative]
    :or {orientation :horizontal
         decorative true}
    :as props}]
  [:>
   (.-Root SeparatorPrimitive)
   (-> props
       (assoc :data-slot "separator"
              :orientation (name orientation)
              :decorative decorative
              :class (merge-classes "shrink-0 bg-border"
                                    (case orientation
                                      :horizontal "h-[1px] w-full"
                                      :vertical "h-full w-px"
                                      "h-[1px] w-full") ; fallback to horizontal
                                    class))
       (dissoc :class-name))])
