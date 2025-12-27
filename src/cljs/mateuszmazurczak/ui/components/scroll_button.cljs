(ns mateuszmazurczak.ui.components.scroll-button
  "Scroll-to-bottom button that appears when not at the bottom of a scrollable container.
  Works with chat-container component via use-stick-to-bottom context."
  (:require
   ["lucide-react"                        :refer [ChevronDown]]
   ["use-stick-to-bottom"                 :refer [useStickToBottomContext]]
   [mateuszmazurczak.ui.components.button :as mateuszmazurczak-button]
   [mateuszmazurczak.utils.styles         :refer [merge-classes]]
   [reagent.core                          :as    r
                                          :refer [defc]]))

(defc scroll-button
 "Scroll-to-bottom button that appears when user scrolls up in a chat container.
  Must be used within a chat-container-root component to access scroll context.
  
  Automatically shows/hides with smooth transitions based on scroll position.
  When clicked, smoothly scrolls to the bottom of the container.
  
  Props:
  - `:variant` - Button variant (default: `:outline`)
  - `:size` - Button size (default: `:sm`)
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying button component
  
  Example (inside chat container):
  [chat-container-root
    [chat-container-content
      ;; messages here
      [chat-container-scroll-anchor]]
    [scroll-button {:class \"absolute bottom-4 right-4\"}]]
  
  Example with custom variant:
  [scroll-button {:variant :secondary :size :default}]"
 [{:keys [variant size class]
   :or {variant :outline
        size :sm}
   :as props}]
 (let [context (useStickToBottomContext)
       is-at-bottom? (.-isAtBottom context)
       scroll-to-bottom (.-scrollToBottom context)
       base-classes "h-10 w-10 rounded-full transition-all duration-150 ease-out"
       visibility-classes (if-not is-at-bottom?
                            "translate-y-0 scale-100 opacity-100"
                            "pointer-events-none translate-y-4 scale-95 opacity-0")
       combined-classes (merge-classes base-classes visibility-classes class)]
   [mateuszmazurczak-button/button
    (-> props
        (assoc :variant variant
               :size size
               :class combined-classes
               :on-click (fn [_e] (scroll-to-bottom)))
        (dissoc :class-name))
    [:> ChevronDown {:class "h-5 w-5"}]]))
