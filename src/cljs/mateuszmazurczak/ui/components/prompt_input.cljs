(ns mateuszmazurczak.ui.components.prompt-input
  "Prompt input component for chat interfaces with auto-resizing textarea.
  Provides a context-based system for sharing state between input and action buttons.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["react"                                 :as react]
   [mateuszmazurczak.ui.components.textarea :as mateuszmazurczak-textarea]
   [mateuszmazurczak.ui.components.tooltip  :as mateuszmazurczak-tooltip]
   [mateuszmazurczak.utils.styles           :refer [merge-classes]]
   [reagent.core                            :as    r
                                            :refer [defc]]
   [reagent.hooks                           :as rhooks]))

;; Context for sharing state between prompt input components
(def ^:private prompt-input-context (react/createContext nil))

(defn- use-prompt-input
  "Hook to access prompt input context.
  Returns context map with :is-loading?, :value, :set-value, :max-height, :on-submit, :disabled?, :textarea-ref"
  []
  (let [context (react/useContext prompt-input-context)]
    (when-not context
      (throw (js/Error. "usePromptInput must be used within a PromptInput component")))
    ;; Keep textarea-ref as raw JS object, convert the rest
    {:is-loading? (.-isLoading context)
     :value (.-value context)
     :set-value (.-setValue context)
     :max-height (.-maxHeight context)
     :on-submit (.-onSubmit context)
     :disabled? (.-disabled context)
     :textarea-ref (.-textareaRef context)}))

(defc prompt-input
 "Root prompt input component. Provides context for textarea and actions.
  
  Props:
  - `:is-loading?` - Whether the prompt is being processed (default: false)
  - `:value` - Controlled value for the textarea
  - `:on-value-change` - Callback when value changes: (fn [new-value] ...)
  - `:max-height` - Maximum height for textarea in pixels or CSS value (default: 240)
  - `:on-submit` - Callback when Enter is pressed (without Shift): (fn [] ...)
  - `:disabled?` - Whether input is disabled (default: false)
  - `:on-click` - Click handler for the container
  - `:class` - Additional Tailwind classes
  
  Children: prompt-input-textarea, prompt-input-actions
  
  Example:
  [prompt-input {:value @message
                 :on-value-change #(reset! message %)
                 :on-submit send-message
                 :is-loading? @sending?}
    [prompt-input-textarea {:placeholder \"Type a message...\"}]
    [prompt-input-actions
      [prompt-input-action {:tooltip \"Send\"}
        [button {:on-click send-message} \"Send\"]]]]"
 [{:keys [is-loading? value on-value-change max-height on-submit disabled? on-click class]
   :or {is-loading? false
        max-height 240
        disabled? false}
   :as props}
  &
  children]
 (let [[internal-value set-internal-value] (rhooks/use-state (or value ""))
       textarea-ref (rhooks/use-ref nil)
       handle-change (rhooks/use-callback (fn [new-value]
                                            (set-internal-value new-value)
                                            (when on-value-change (on-value-change new-value)))
                                          [on-value-change])
       handle-click (rhooks/use-callback (fn [e]
                                           (when-not disabled?
                                             (when-let [textarea (.-current textarea-ref)]
                                               (.focus textarea)))
                                           (when on-click (on-click e)))
                                         [disabled? on-click])
       context-value (clj->js {:isLoading is-loading?
                               :value (or value internal-value)
                               :setValue (or on-value-change handle-change)
                               :maxHeight max-height
                               :onSubmit on-submit
                               :disabled disabled?
                               :textareaRef textarea-ref})]
   [:>
    mateuszmazurczak-tooltip/tooltip-provider-component
    {}
    [:>
     (.-Provider prompt-input-context)
     {:value context-value}
     (into [:div
            (-> props
                (assoc :on-click handle-click
                       :class
                       (merge-classes
                        "border-input bg-background cursor-text rounded-3xl border p-2 shadow-xs"
                        (when disabled? "cursor-not-allowed opacity-60")
                        class))
                (dissoc :is-loading?
                        :value
                        :on-value-change
                        :max-height
                        :on-submit
                        :disabled?
                        :class-name))]
           children)]]))

(defc prompt-input-textarea
 "Auto-resizing textarea for prompt input.
  Must be used within a prompt-input component.
  
  Props:
  - `:disable-autosize?` - Disable automatic height adjustment (default: false)
  - `:placeholder` - Placeholder text
  - `:on-key-down` - Additional key down handler
  - `:class` - Additional Tailwind classes
  - All other props passed to textarea component
  
  Features:
  - Auto-resizes based on content
  - Respects max-height from parent context
  - Submit on Enter (without Shift)
  - New line on Shift+Enter
  
  Example:
  [prompt-input-textarea {:placeholder \"Type a message...\"}]
  
  Example with custom handler:
  [prompt-input-textarea {:placeholder \"Enter text\"
                          :on-key-down #(js/console.log \"key pressed\")}]"
 [{:keys [disable-autosize? on-key-down class]
   :or {disable-autosize? false}
   :as props}]
 (let [context (use-prompt-input)
       {:keys [value set-value max-height on-submit disabled? textarea-ref]} context
       adjust-height
       (fn [el]
         (when (and el (not disable-autosize?))
           (set! (.. el -style -height) "auto")
           (if (number? max-height)
             (set! (.. el -style -height) (str (min (.-scrollHeight el) max-height) "px"))
             (set! (.. el -style -height) (str "min(" (.-scrollHeight el) "px, " max-height ")")))))
       handle-ref (fn [el] (set! (.-current textarea-ref) el) (adjust-height el))
       handle-change (rhooks/use-callback
                      (fn [e] (adjust-height (.-target e)) (set-value (.. e -target -value)))
                      [set-value disable-autosize?])
       handle-key-down (rhooks/use-callback (fn [e]
                                              (when (and (= (.-key e) "Enter") (not (.-shiftKey e)))
                                                (.preventDefault e)
                                                (when on-submit (on-submit)))
                                              (when on-key-down (on-key-down e)))
                                            [on-submit on-key-down])]
   ;; Layout effect to adjust height when value changes
   (rhooks/use-layout-effect
    (fn []
      (when-let [el (.-current textarea-ref)]
        (when-not disable-autosize?
          (set! (.. el -style -height) "auto")
          (if (number? max-height)
            (set! (.. el -style -height) (str (min (.-scrollHeight el) max-height) "px"))
            (set! (.. el -style -height) (str "min(" (.-scrollHeight el) "px, " max-height ")")))))
      js/undefined)
    [value max-height disable-autosize?])
   [mateuszmazurczak-textarea/textarea
    (->
      props
      (assoc
       :ref handle-ref
       :value value
       :on-change handle-change
       :on-key-down handle-key-down
       :rows 1
       :disabled disabled?
       :class
       (merge-classes
        "text-base min-h-[44px] w-full resize-none border-none bg-transparent shadow-none outline-none focus-visible:ring-0 focus-visible:ring-offset-0"
        class))
      (dissoc :disable-autosize? :class-name))]))

(defn prompt-input-actions
  "Container for prompt input action buttons.
  
  Props:
  - `:class` - Additional Tailwind classes
  - All other props passed to div element
  
  Children: prompt-input-action components
  
  Example:
  [prompt-input-actions {}
    [prompt-input-action {:tooltip \"Send\"}
      [button {} \"Send\"]]
    [prompt-input-action {:tooltip \"Attach\"}
      [button {} \"Attach\"]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :class (merge-classes "flex items-center gap-2" class))
             (dissoc :class-name))]
        children))

(defc prompt-input-action
 "Individual action button with tooltip for prompt input.
  Must be used within a prompt-input component to respect disabled state.
  
  Props:
  - `:tooltip` - Tooltip content (required)
  - `:side` - Tooltip side: :top, :bottom, :left, :right (default: :top)
  - `:class` - Additional Tailwind classes for tooltip content
  - All other props passed to tooltip component
  
  Children: Button or interactive element
  
  Example:
  [prompt-input-action {:tooltip \"Send message\"}
    [button {:variant :ghost :size :icon :on-click send-fn}
      [:> send-icon]]]
  
  Example with custom side:
  [prompt-input-action {:tooltip \"Attach file\" :side :left}
    [button {:variant :outline :size :sm}
      [:> paperclip-icon]]]"
 [{:keys [tooltip side class]
   :or {side :top}
   :as props}
  &
  children]
 (let [context (use-prompt-input)
       {:keys [disabled?]} context
       child (first children)
       ;; Wrap child with stop propagation and disabled state
       enhanced-child (if (vector? child)
                        (update child
                                1
                                (fn [child-props]
                                  (let [original-on-click (:on-click child-props)]
                                    (assoc child-props
                                           :disabled (or (:disabled child-props) disabled?)
                                           :on-click (fn [e]
                                                       (.stopPropagation e)
                                                       (when original-on-click
                                                         (original-on-click e)))))))
                        child)]
   [mateuszmazurczak-tooltip/tooltip
    (-> props
        (assoc :trigger (r/as-element enhanced-child)
               :content tooltip
               :side side
               :content-class class
               :trigger-as-child? true
               :disable-hoverable-content? disabled?)
        (dissoc :tooltip :class :class-name))]))
