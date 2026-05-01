(ns mateuszmazurczak.ui.components.empty
  "Empty state component with support for displaying empty states, no data, or placeholder content.
  Composed of multiple sub-components for flexible layout.

  Version: 1.0.0
  Last updated: 2026-02-06

  Custom component implementation."
  (:refer-clojure :exclude [empty])
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn empty
  "Main container for empty state displays. Use with empty-header, empty-content, etc.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty {}
    [empty-header {}
      [empty-media {:variant :icon} [:> SomeIcon]]
      [empty-title {} \"No items found\"]
      [empty-description {} \"Get started by creating a new item.\"]]
    [empty-content {}
      [button {:variant :default} \"Create Item\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let
    [base-classes
     "flex min-w-0 flex-1 flex-col items-center justify-center gap-6 rounded-lg border-dashed p-6 text-center text-balance md:p-12"
     combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty" :class combined-classes)
               (dissoc :class-name))]
          children)))

(defn empty-header
  "Header section for empty state, typically containing media, title, and description.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty-header {}
    [empty-media {:variant :icon} [:> FolderIcon]]
    [empty-title {} \"No documents\"]
    [empty-description {} \"Upload your first document to get started.\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [base-classes "flex max-w-sm flex-col items-center gap-2 text-center"
        combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty-header" :class combined-classes)
               (dissoc :class-name))]
          children)))

(defn- media-variant-classes
  "Returns Tailwind classes for the given media variant.
  
  Variants:
  - `:default` - Transparent background, no special styling
  - `:icon` - Muted background with icon styling (rounded, padded)"
  [variant]
  (case variant
    :default "bg-transparent"
    :icon
    "bg-muted text-foreground flex size-10 shrink-0 items-center justify-center rounded-lg [&_svg:not([class*='size-'])]:size-6"
    ;; default fallback
    "bg-transparent"))

(defn empty-media
  "Media container for icons or illustrations in empty state.
  
  Props:
  - `:variant` - Media variant (default: `:default`)
    - `:default` - Transparent background
    - `:icon` - Styled icon container with muted background
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty-media {:variant :icon}
    [:> FolderIcon]]"
  [{:keys [variant class]
    :or {variant :default}
    :as props}
   &
   children]
  (let
    [base-classes
     "flex shrink-0 items-center justify-center mb-2 [&_svg]:pointer-events-none [&_svg]:shrink-0"
     combined-classes (merge-classes base-classes (media-variant-classes variant) class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty-icon" :data-variant (name variant) :class combined-classes)
               (dissoc :class-name :variant))]
          children)))

(defn empty-title
  "Title text for empty state.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty-title {} \"No results found\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [base-classes "text-lg font-medium tracking-tight"
        combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty-title" :class combined-classes)
               (dissoc :class-name))]
          children)))

(defn empty-description
  "Description text for empty state. Supports links with automatic styling.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty-description {}
    \"No items to display. \"
    [:a {:href \"/create\"} \"Create one now\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let
    [base-classes
     "text-muted-foreground [&>a:hover]:text-primary text-sm/relaxed [&>a]:underline [&>a]:underline-offset-4"
     combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty-description" :class combined-classes)
               (dissoc :class-name))]
          children)))

(defn empty-content
  "Content container for action buttons or additional content in empty state.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [empty-content {}
    [button {:variant :default} \"Create New\"]
    [button {:variant :outline} \"Learn More\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [base-classes "flex w-full max-w-sm min-w-0 flex-col items-center gap-4 text-sm text-balance"
        combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :data-slot "empty-content" :class combined-classes)
               (dissoc :class-name))]
          children)))
