(ns mateuszmazurczak.ui.components.chat-container
  "Chat container component with auto-scroll-to-bottom functionality.
  Uses use-stick-to-bottom library for smooth scrolling behavior.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["use-stick-to-bottom"         :refer [StickToBottom]]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn chat-container-root
  "Root container for chat interface with auto-scroll-to-bottom behavior.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - `:resize` - Resize behavior: 'smooth' (default) or 'instant'
  - `:initial` - Initial scroll behavior: 'instant' (default) or 'smooth'
  - All other props are passed to the underlying StickToBottom component
  
  Example:
  [chat-container-root
    [chat-container-content
      ;; chat messages here
      [chat-container-scroll-anchor]]]"
  [{:keys [class resize initial]
    :or {resize "smooth"
         initial "instant"}
    :as props}
   &
   children]
  (let [base-classes "flex flex-col"
        combined-classes (merge-classes base-classes class)]
    (into [:>
           StickToBottom
           (-> props
               (assoc :className combined-classes :resize resize :initial initial :role "log")
               (dissoc :class))]
          children)))

(defn chat-container-content
  "Content wrapper for chat container.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying StickToBottom.Content component
  
  Example:
  [chat-container-content
    [message {:key 1} \"Hello\"]
    [message {:key 2} \"World\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [base-classes "flex w-full flex-col"
        combined-classes (merge-classes base-classes class)
        Content (.-Content StickToBottom)]
    (into [:>
           Content
           (-> props
               (assoc :className combined-classes)
               (dissoc :class))]
          children)))

(defn chat-container-scroll-anchor
  "Scroll anchor element for chat container.
  Used to mark the bottom scroll position.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [chat-container-scroll-anchor]"
  [{:keys [class]
    :as props}]
  (let [base-classes "h-px w-full shrink-0 scroll-mt-4"
        combined-classes (merge-classes base-classes class)]
    [:div
     (-> props
         (assoc :class combined-classes :aria-hidden "true")
         (dissoc :class-name))]))
