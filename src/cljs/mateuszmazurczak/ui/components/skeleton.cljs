(ns mateuszmazurczak.ui.components.skeleton
  "Skeleton component for loading placeholders.

  A simple animated placeholder component that displays a pulsing effect while content
  is loading. Uses Tailwind's `animate-pulse` utility for the animation effect.

  This is one of the simplest shadcn/ui components - just a styled div with no
  external dependencies (no Radix primitives needed).

  ## Usage

  Basic skeleton:
  ```clojure
  [skeleton]
  ```

  Custom sizing:
  ```clojure
  [skeleton {:class \"h-12 w-12 rounded-full\"}]  ; Avatar skeleton
  [skeleton {:class \"h-4 w-[250px]\"}]           ; Text line skeleton
  [skeleton {:class \"h-[125px] w-full rounded-xl\"}] ; Card skeleton
  ```

  Multiple skeletons for a card layout:
  ```clojure
  [:div {:class \"flex items-center space-x-4\"}
   [skeleton {:class \"h-12 w-12 rounded-full\"}]
   [:div {:class \"space-y-2\"}
    [skeleton {:class \"h-4 w-[250px]\"}]
    [skeleton {:class \"h-4 w-[200px]\"}]]]
  ```

  With custom HTML attributes:
  ```clojure
  [skeleton {:class \"h-20 w-20\"
             :role \"status\"
             :aria-label \"Loading...\"}]
  ```"
  (:require
   [ui.utils.styles :refer [merge-classes]]))

(defn skeleton
  "Loading placeholder with pulse animation.

  A styled div that displays an animated pulsing effect, useful for showing loading
  states. The component applies a default gray background with rounded corners and
  a pulse animation.

  ## Props

  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed through to the underlying div element

  ## Default Styling

  - `animate-pulse` - Pulsing animation
  - `rounded-md` - Medium rounded corners
  - `bg-muted` - Muted background color (theme-aware)

  ## Accessibility

  Consider adding ARIA attributes for better screen reader support:
  - `:role \"status\"` - Indicates dynamic content
  - `:aria-label \"Loading...\"` - Descriptive label
  - `:aria-live \"polite\"` - Announces changes

  ## Examples

  ```clojure
  ;; Simple skeleton
  [skeleton]

  ;; Avatar skeleton
  [skeleton {:class \"h-12 w-12 rounded-full\"}]

  ;; Text line skeleton
  [skeleton {:class \"h-4 w-[250px]\"}]

  ;; Card skeleton with custom height
  [skeleton {:class \"h-[125px] w-full rounded-xl\"}]

  ;; Accessible skeleton
  [skeleton {:class \"h-20 w-full\"
             :role \"status\"
             :aria-label \"Loading content\"}]
  ```"
  [{:keys [class]
    :as props}]
  [:div
   (-> props
    (assoc :data-slot "skeleton" :class (merge-classes "animate-pulse rounded-md bg-muted" class))
    (dissoc :class-name))])
