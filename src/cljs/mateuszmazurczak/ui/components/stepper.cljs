(ns mateuszmazurczak.ui.components.stepper
  "Pure presentational stepper components using React Context.

  Design:
  - Context-based: stepper provider shares state with child components
  - Composable: small components that work together
  - UI copied 1:1 from shadcn/ui stepper example
  - Purely presentational: parent manages current-step state"
  (:require
   ["react"                               :as react]
   [mateuszmazurczak.ui.components.button :as ui-button]
   [mateuszmazurczak.utils.styles         :refer [merge-classes]]
   [reagent.core                          :as    r
                                          :refer [defc]]
   [reagent.hooks                         :as rhooks]))

(def ^:private stepper-context (react/createContext nil))

(defn- step-state
  "Calculate display state for a step: active, completed, or inactive.
   When reverse-progress? is true, steps after current are completed (for newest-first lists).
   When current-idx is negative (nothing selected), all steps are inactive."
  [current-idx step-idx reverse-progress?]
  (cond
    (neg? current-idx) "inactive"
    (= current-idx step-idx) "active"
    (and reverse-progress? (< current-idx step-idx)) "completed"
    (and (not reverse-progress?) (> current-idx step-idx)) "completed"
    :else "inactive"))

;; =============================================================================
;; Public components
;; =============================================================================

(defn stepper
  "Root stepper component. Provides context to child components.
  
  Props:
  - :current-step - ID of current/active step (required)
  - :on-step-change - (fn [step-id]) callback when step is clicked
  - :variant - :horizontal | :vertical | :circle (default: :horizontal)
  - :label-orientation - :horizontal | :vertical (default: :horizontal)
  - :reverse-progress? - When true, steps after current are 'completed' (for newest-first lists)
  - :class - Additional CSS classes
  
  Children: stepper-navigation, stepper-panel, stepper-controls, etc."
  [{:keys [current-step on-step-change variant label-orientation reverse-progress? class]
    :or {variant :horizontal
         label-orientation :horizontal
         reverse-progress? false}}
   &
   children]
  (let [context (clj->js {:currentStep current-step
                          :onStepChange on-step-change
                          :variant (name variant)
                          :labelOrientation (name label-orientation)
                          :reverseProgress reverse-progress?})]
    (into [:>
           (.-Provider stepper-context)
           {:value context}
           [:div {:data-component "stepper"
                  :data-variant (name variant)
                  :data-label-orientation (name label-orientation)
                  :class (merge-classes "w-full" class)}]]
          children)))

(defn stepper-title
  "Title component for step labels.
  
  Props:
  - :class - Additional CSS classes
  
  Children: Title text or elements"
  [{:keys [class]} & children]
  (into [:h4 {:data-component "stepper-step-title"
              :class (merge-classes "text-base font-medium" class)}]
        children))

(defn stepper-description
  "Description component for step labels.
  
  Props:
  - :class - Additional CSS classes
  
  Children: Description text or elements"
  [{:keys [class]} & children]
  (into [:span {:data-component "stepper-step-description"
                :class (merge-classes "text-sm text-muted-foreground" class)}]
        children))

(defn stepper-controls
  "Navigation controls container for prev/next buttons.
  
  Props:
  - :class - Additional CSS classes
  
  Children: Button components or other navigation controls"
  [{:keys [class]} & children]
  (into [:div {:data-component "stepper-controls"
               :class (merge-classes "flex justify-end gap-4" class)}]
        children))

;; =============================================================================
;; Internal UI components (private helpers)
;; =============================================================================

(defn- circle-step-indicator
  "SVG-based circular progress indicator for circle variant."
  [{:keys [current-step total-steps size stroke-width]
    :or {size 80
         stroke-width 6}}]
  (let [radius (/ (- size stroke-width) 2)
        circumference (* radius 2 js/Math.PI)
        fill-perc (* (/ current-step total-steps) 100)
        dash-offset (- circumference (/ (* circumference fill-perc) 100))]
    [:div {:data-component "stepper-step-indicator"
           :role "progressbar"
           :aria-valuenow current-step
           :aria-valuemin 1
           :aria-valuemax total-steps
           :tab-index -1
           :class "relative inline-flex items-center justify-center"}
     [:svg {:width size
            :height size}
      [:title "Step Indicator"]
      [:circle {:cx (/ size 2)
                :cy (/ size 2)
                :r radius
                :fill "none"
                :stroke "currentColor"
                :stroke-width stroke-width
                :class "text-muted-foreground"}]
      [:circle {:cx (/ size 2)
                :cy (/ size 2)
                :r radius
                :fill "none"
                :stroke "currentColor"
                :stroke-width stroke-width
                :stroke-dasharray circumference
                :stroke-dashoffset dash-offset
                :class "text-primary transition-all duration-500 ease-in-out"
                :transform (str "rotate(-90 " (/ size 2) " " (/ size 2) ")")}]]
     [:div {:class "absolute inset-0 flex items-center justify-center"}
      [:span {:class "text-sm font-medium"
              :aria-live "polite"}
       current-step
       " of "
       total-steps]]]))

(defn- stepper-separator
  "Visual separator line between steps."
  [{:keys [orientation label-orientation state disabled? is-last?]}]
  (when-not is-last?
    [:div
     (cond-> {:data-component "stepper-separator"
              :data-orientation orientation
              :data-state state
              :role "separator"
              :tab-index -1
              :class
              (merge-classes
               "bg-muted"
               "data-[state=completed]:bg-primary"
               "data-[disabled]:opacity-50"
               "transition-all duration-300 ease-in-out"
               (case (keyword orientation)
                 :horizontal "h-0.5 flex-1"
                 :vertical "h-full w-0.5"
                 "h-0.5 flex-1")
               (when (= label-orientation "vertical")
                 "absolute left-[calc(50%+30px)] right-[calc(-50%+20px)] top-5 block shrink-0"))}
       disabled? (assoc :data-disabled true))]))

(defc stepper-step
 "Individual step button/indicator. Receives index/total from navigation.
  
  Props:
  - :id - Step ID (required)
  - :index - Auto-injected by stepper-navigation
  - :total - Auto-injected by stepper-navigation
  - :current-idx - Auto-injected by stepper-navigation
  - :disabled? - Disable this step
  - :icon - Custom icon (overrides number)
  - :class - Additional CSS classes
  
  Children: stepper-title, stepper-description, or other content"
 [{:keys [id index total current-idx disabled? icon class]} & children]
 (let [ctx (rhooks/use-context stepper-context)
       variant (.-variant ctx)
       label-orientation (.-labelOrientation ctx)
       current-step (.-currentStep ctx)
       on-step-change (.-onStepChange ctx)
       reverse-progress? (.-reverseProgress ctx)
       active? (= id current-step)
       is-last? (= index (dec total))
       state (step-state current-idx index reverse-progress?)]
   (if (= variant "circle")
     ;; Circle variant: render all steps, but only show active one
     [:li {:data-component "stepper-step"
           :class (merge-classes "flex shrink-0 items-center gap-4 rounded-md"
                                 "transition-opacity duration-100 ease-in-out"
                                 (if active? "opacity-100" "opacity-0 absolute")
                                 class)}
      [circle-step-indicator {:current-step (inc current-idx)
                              :total-steps total}]
      (into [:div {:data-component "stepper-step-content"
                   :class "flex flex-col items-start gap-1"}]
            children)]
     ;; Horizontal/vertical variants
     [:<>
      [:li
       (cond-> {:data-component "stepper-step"
                :class (merge-classes "group peer relative flex items-center gap-2 cursor-pointer"
                                      "data-[variant=vertical]:flex-row"
                                      "data-[label-orientation=vertical]:w-full"
                                      "data-[label-orientation=vertical]:flex-col"
                                      "data-[label-orientation=vertical]:justify-center" class)
                :data-variant variant
                :data-label-orientation label-orientation
                :data-state state
                :on-click #(when (and (not disabled?) on-step-change) (on-step-change id))}
         disabled? (assoc :data-disabled true))
       (ui-button/button {:id (str "step-" id)
                          :data-component "stepper-step-indicator"
                          :type "button"
                          :role "tab"
                          :tab-index (if (not= state "inactive") 0 -1)
                          :class "rounded-full"
                          :variant (if (not= state "inactive") :default :secondary)
                          :size :icon
                          :disabled disabled?
                          :aria-controls (str "step-panel-" id)
                          :aria-current (when active? "step")
                          :aria-posinset (inc index)
                          :aria-setsize total
                          :aria-selected active?
                          :on-click #(when (and (not disabled?) on-step-change)
                                       (on-step-change id))}
                         (or icon (inc index)))
       (when (and (= variant "horizontal") (= label-orientation "vertical"))
         [stepper-separator {:orientation "horizontal"
                             :label-orientation label-orientation
                             :state state
                             :disabled? disabled?
                             :is-last? is-last?}])
       (into [:div {:data-component "stepper-step-content"
                    :class "flex flex-col items-start"}]
             children)]
      ;; Horizontal separator (outside li)
      (when (and (= variant "horizontal") (= label-orientation "horizontal"))
        [stepper-separator {:orientation "horizontal"
                            :label-orientation label-orientation
                            :state state
                            :disabled? disabled?
                            :is-last? is-last?}])
      ;; Vertical variant: separator and panel wrapper structure
      (when (= variant "vertical")
        (let [;; For reverse progress, separator should be colored if next step is completed
              sep-state
              (if reverse-progress? (step-state current-idx (inc index) reverse-progress?) state)]
          [:div {:class "flex gap-4"}
           (when-not is-last?
             [:div {:class "flex justify-center ps-[calc(var(--spacing)_*_4.5_-_1px)]"}
              [stepper-separator {:orientation "vertical"
                                  :state sep-state
                                  :disabled? disabled?
                                  :is-last? is-last?}]])
           [:div {:class "my-3 flex-1 ps-4"}]]))])))

(defn- flatten-children
  "Flatten children to handle both direct children and sequences from for/map."
  [children]
  (reduce (fn [acc child]
            (cond
              ;; It's a stepper-step vector
              (and (vector? child)
                   (or (identical? (first child) stepper-step) (= (first child) stepper-step)))
              (conj acc child)
              ;; It's a sequence (from for/map) - flatten it
              (sequential? child) (into acc (flatten-children child))
              ;; Something else, keep it
              :else (conj acc child)))
          []
          children))

(defc stepper-navigation
 "Navigation container for steps. Auto-numbers steps and injects context.
  
  Props:
  - :class - Additional CSS classes
  
  Children: stepper-step components"
 [{:keys [class]} & children]
 (let [ctx (rhooks/use-context stepper-context)
       variant (.-variant ctx)
       current-step (.-currentStep ctx)
       ;; Flatten children to handle for/map sequences
       flat-children (flatten-children children)
       step-ids (keep (fn [child]
                        (when (and (vector? child)
                                   (or (identical? (first child) stepper-step)
                                       (= (first child) stepper-step)))
                          (let [props (second child)] (when (map? props) (:id props)))))
                      flat-children)
       current-idx
       (if current-step (first (keep-indexed #(when (= %2 current-step) %1) step-ids)) -1)
       total (count step-ids)
       numbered-children
       (map-indexed
        (fn [idx child]
          (if (and (vector? child)
                   (or (identical? (first child) stepper-step) (= (first child) stepper-step)))
            (let [[component props & rest] child
                  props (if (map? props) props {})]
              (into [component (assoc props :index idx :total total :current-idx current-idx)]
                    rest))
            child))
        flat-children)]
   [:nav {:data-component "stepper-navigation"
          :aria-label "Stepper Navigation"
          :role "tablist"
          :class class}
    (into [:ol {:data-component "stepper-navigation-list"
                :class (merge-classes "flex gap-2"
                                      (case variant
                                        "horizontal" "flex-row items-center justify-between"
                                        "vertical" "flex-col"
                                        "circle" "flex-row items-center justify-between"
                                        "flex-row items-center justify-between"))}]
          numbered-children)]))

(defc stepper-panel
 "Panel component for step content. Only renders if this is the active step.
  
  Props:
  - :id - Step ID this panel belongs to (required)
  - :class - Additional CSS classes
  
  Children: Panel content"
 [{:keys [id class]} & children]
 (let [ctx (rhooks/use-context stepper-context)
       current-step (.-currentStep ctx)]
   (when (= id current-step)
     (into [:div {:id (str "step-panel-" id)
                  :data-component "stepper-step-panel"
                  :role "tabpanel"
                  :aria-labelledby (str "step-" id)
                  :class class}]
           children))))
