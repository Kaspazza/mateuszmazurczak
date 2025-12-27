(ns mateuszmazurczak.ui.components.tooltip
  "Self-contained tooltip component for displaying contextual information on hover/focus.

  A popup that displays information related to an element when the element receives
  keyboard focus or the mouse hovers over it. Built on Radix UI's Tooltip primitive,
  which provides excellent accessibility and positioning.

  ## Key Features

  - **Self-Contained**: No provider needed, each tooltip manages its own context
  - **Data-Driven**: Configured purely through a single map of options
  - **Accessible**: Proper ARIA attributes, keyboard navigation, screen reader support
  - **Smart Positioning**: Auto-repositions to stay in viewport (collision detection)
  - **Directional Animations**: Slides in from appropriate direction based on placement
  - **Portal Rendering**: Renders in portal to avoid z-index issues

  ## Usage

  Basic tooltip:
  ```clojure
  [tooltip
   {:trigger [:button \"Hover me\"]
    :content \"Helpful information\"}]
  ```

  With Reagent component as trigger:
  ```clojure
  ;; When using Reagent components, set trigger-as-child? false (default)
  [tooltip
   {:trigger [button {:variant :outline} \"Delete\"]
    :content \"This will permanently delete the item\"
    :side :top
    :side-offset 8
    :content-class \"max-w-xs\"
    :trigger-as-child? false}]  ; Required for Reagent components!
  ```

  With plain hiccup as trigger (can use asChild):
  ```clojure
  ;; When using plain hiccup, you can enable trigger-as-child? for prop merging
  [tooltip
   {:trigger [:button {:class \"px-4 py-2\"} \"Delete\"]
    :content \"This will permanently delete the item\"
    :trigger-as-child? true}]  ; Works with plain hiccup
  ```

  Rich content with Reagent component:
  ```clojure
  [tooltip
   {:trigger [button {:variant :ghost :size :icon}
              [:> info-icon {:class \"h-4 w-4\"}]]
    :content [:div
              [:p {:class \"font-semibold\"} \"Pro Tip\"]
              [:p {:class \"text-xs\"} \"Keyboard shortcuts available.\"]]
    :trigger-as-child? false}]  ; Required for Reagent components
  ```

  Controlled state (basic):
  ```clojure
  (let [open? (r/atom false)]
    [tooltip
     {:trigger [:button {:on-click #(swap! open? not)} \"Toggle\"]
      :content \"I'm controlled externally\"
      :open @open?
      :on-open-change #(reset! open? %)}])
  ```

  Controlled state (with external button to prevent hover conflicts):
  ```clojure
  (let [open? (r/atom false)
        force-closed? (r/atom false)]
    [:div
     ;; External button
     [button {:on-click (fn []
                          (if @open?
                            (do (reset! force-closed? true)
                                (reset! open? false)
                                (js/setTimeout #(reset! force-closed? false) 300))
                            (do (reset! force-closed? false)
                                (reset! open? true))))}
      (if @open? \"Close\" \"Open\")]
     ;; Tooltip with hover
     [tooltip
      {:trigger [:span \"Hover me\"]
       :content \"Controlled + hover-enabled\"
       :open @open?
       :on-open-change #(when-not (and % @force-closed?)
                         (reset! open? %))}]])
  ```"
  (:require
   ["@radix-ui/react-tooltip"     :as TooltipPrimitive]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

;;
;; Internal Radix primitives (not exported)
;;

(def ^:private tooltip-provider (.-Provider TooltipPrimitive))
(def ^:private tooltip-root (.-Root TooltipPrimitive))
(def ^:private tooltip-trigger (.-Trigger TooltipPrimitive))
(def ^:private tooltip-content-primitive (.-Content TooltipPrimitive))

;;
;; Public API - Exported provider for shared tooltip context
;;

(def tooltip-provider-component
  "TooltipProvider component for wrapping multiple tooltips.
  Allows tooltips to share delay and skip-delay behavior.
  
  Props:
  - `:delay-duration` - Time (ms) before tooltip shows (default: 700)
  - `:skip-delay-duration` - Time (ms) to skip delay between tooltips (default: 300)
  - `:disable-hoverable-content?` - Prevent tooltip from staying open on hover (default: false)
  
  Example:
  ```clojure
  [:> tooltip-provider-component {}
   [my-component-with-tooltips]]
  ```"
  tooltip-provider)

;;
;; Public API - Single data-driven component
;;

(defn tooltip
  "Self-contained tooltip component configured purely with data.

  A single function that accepts a configuration map and renders a complete tooltip
  with trigger and content. No provider or wrapper components needed.

  ## Configuration Map

  ### Required
  - `:trigger` - Hiccup element to trigger the tooltip (e.g., `[:button \"Hover me\"]`)
  - `:content` - Hiccup element or string for tooltip content

  ### Positioning
  - `:side` - Preferred side: `:top`, `:right`, `:bottom`, `:left` (default: `:top`)
  - `:side-offset` - Distance in pixels from trigger (default: 4)
  - `:align` - Alignment: `:start`, `:center`, `:end` (default: `:center`)
  - `:align-offset` - Offset in pixels from alignment edge (default: 0)
  - `:collision-padding` - Padding from viewport edges (default: 0)
  - `:avoid-collisions?` - Auto-reposition to stay in viewport (default: true)
  - `:sticky` - Keep aligned when trigger moves: `:partial`, `:always` (default: `:partial`)

  ### Timing
  - `:delay-duration` - Time (ms) before tooltip shows (default: 700)
  - `:skip-delay-duration` - Time (ms) to skip delay between tooltips (default: 300)

  ### State
  - `:open` - Controlled open state (boolean)
  - `:default-open` - Uncontrolled default open state (boolean)
  - `:on-open-change` - Callback when open state changes `(fn [open?] ...)`

  ### Styling
  - `:content-class` - Additional Tailwind classes for content
  - `:trigger-as-child?` - Merge trigger props into child element (default: false)
    - Set to `false` when using Reagent components as trigger (recommended)
    - Set to `true` only when using plain hiccup elements (e.g., `[:button ...]`)
  - `:disable-hoverable-content?` - Prevent tooltip from staying open on hover (default: false)

  ## Examples

  ```clojure
  ;; Simple tooltip with plain hiccup
  [tooltip
   {:trigger [:button \"Hover me\"]
    :content \"Helpful info\"}]

  ;; With Reagent component (most common)
  [tooltip
   {:trigger [button {:variant :outline} \"Delete\"]
    :content \"This will permanently delete the item\"
    :side :top
    :side-offset 8}]  ; trigger-as-child? defaults to false, perfect for components

  ;; Rich content with custom styling
  [tooltip
   {:trigger [button {:variant :ghost} \"Info\"]
    :content [:div
              [:p {:class \"font-semibold\"} \"Pro Tip\"]
              [:p {:class \"text-xs\"} \"Use Ctrl+K for shortcuts.\"]]
    :content-class \"max-w-xs\"}]

  ;; Controlled state (basic - trigger is the control)
  (let [open? (r/atom false)]
    [tooltip
     {:trigger [:button {:on-click #(swap! open? not)} \"Toggle\"]
      :content \"Controlled externally\"
      :open @open?
      :on-open-change #(reset! open? %)}])

  ;; Controlled with separate button (handles hover conflicts)
  (let [open? (r/atom false)
        force-closed? (r/atom false)]
    [:div
     [button {:on-click #(if @open?
                           (do (reset! force-closed? true)
                               (reset! open? false)
                               (js/setTimeout (fn [] (reset! force-closed? false)) 300))
                           (do (reset! force-closed? false)
                               (reset! open? true)))}
      (if @open? \"Close\" \"Open\")]
     [tooltip
      {:trigger [:span \"Hover me\"]
       :content \"Controlled + hover\"
       :open @open?
       :on-open-change #(when-not (and % @force-closed?)
                         (reset! open? %))}]])

  ;; With trigger-as-child? true for prop merging (plain hiccup only)
  [tooltip
   {:trigger [:button {:class \"custom-button\"} \"Hover\"]
    :content \"Props merged into button\"
    :trigger-as-child? true}]  ; Only use with plain hiccup elements
  ```"
  [{:keys [trigger
           content
           side
           side-offset
           align
           align-offset
           collision-padding
           avoid-collisions?
           sticky
           delay-duration
           skip-delay-duration
           open
           default-open
           on-open-change
           content-class
           content-hidden?
           trigger-as-child?
           disable-hoverable-content?]
    :or {side-offset 4
         trigger-as-child? false
         avoid-collisions? true
         delay-duration 700
         skip-delay-duration 300}
    :as _opts}]
  [:>
   tooltip-provider
   {:delayDuration delay-duration
    :skipDelayDuration skip-delay-duration
    :disableHoverableContent disable-hoverable-content?}
   [:>
    tooltip-root
    (cond-> {}
      (some? open) (assoc :open open)
      (some? default-open) (assoc :defaultOpen default-open)
      on-open-change (assoc :onOpenChange on-open-change))
    ;; Trigger
    [:> tooltip-trigger {:asChild trigger-as-child?} trigger]
    ;; Content
    [:>
     tooltip-content-primitive
     (cond-> {:sideOffset side-offset
              :className
              (merge-classes
               (str "z-50 overmateuszmazurczak-hidden rounded-md border bg-popover px-3 py-1.5 "
                    "text-sm text-popover-foreground shadow-md " "animate-in fade-in-0 zoom-in-95 "
                    "data-[state=closed]:animate-out " "data-[state=closed]:fade-out-0 "
                    "data-[state=closed]:zoom-out-95 " "data-[side=bottom]:slide-in-from-top-2 "
                    "data-[side=left]:slide-in-from-right-2 "
                    "data-[side=right]:slide-in-from-left-2 "
                    "data-[side=top]:slide-in-from-bottom-2 "
                    "origin-[--radix-tooltip-content-transform-origin]")
               content-class)}
       side (assoc :side (name side))
       align (assoc :align (name align))
       align-offset (assoc :alignOffset align-offset)
       collision-padding (assoc :collisionPadding collision-padding)
       (some? avoid-collisions?) (assoc :avoidCollisions avoid-collisions?)
       sticky (assoc :sticky (name sticky))
       (some? content-hidden?) (assoc :hidden content-hidden?))
     content]]])
