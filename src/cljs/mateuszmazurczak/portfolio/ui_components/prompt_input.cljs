(ns mateuszmazurczak.portfolio.ui-components.prompt-input
  (:require
   ["lucide-react"                              :refer [Mic Paperclip Send]]
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button       :as button]
   [mateuszmazurczak.ui.components.prompt-input :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]
   [reagent.core                                :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Prompt Input"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Prompt input component for chat interfaces with auto-resizing textarea."
            :npm-install "npm install react"
            :source-code (embed-source "mateuszmazurczak.ui.components.prompt_input")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/prompt_input.cljs"
            :filename "prompt_input.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "prompt-input"
               :link {:href "https://www.prompt-kit.com/docs/prompt-input" :label "Prompt Kit Prompt Input Docs"}
               :description "Root composition component for chat-like input. Provides context used by prompt-input-textarea and prompt-input-action children, including disabled/loading and submit behavior. Additional props are forwarded to the wrapper div."
                :props [{:name ":is-loading?"    :type "boolean"     :default "false" :description "Visual loading/disabled state for the whole prompt input."}
                        {:name ":value"          :type "string"      :default nil     :description "Controlled textarea value."}
                        {:name ":on-value-change" :type "function"   :default nil     :description "Called when text changes: (fn [new-value] ...)."}
                        {:name ":max-height"     :type "number | string" :default "240" :description "Maximum textarea height before scrolling."}
                        {:name ":on-submit"      :type "function"    :default nil     :description "Triggered on Enter (without Shift)."}
                        {:name ":disabled?"      :type "boolean"     :default "false" :description "Disables interactions and applies muted styles."}
                        {:name ":on-click"       :type "function"    :default nil     :description "Click handler for root container (focus behavior is preserved)."}
                        {:name ":class"          :type "string"      :default nil     :description "Additional Tailwind classes for root container."}
                        {:name "additional props" :type "map entries" :default nil    :description "Forwarded to root div."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "prompt-input-textarea"
                :description "Auto-resizing textarea that consumes prompt-input context. Must be nested inside prompt-input. Additional props are forwarded to textarea component."
                :props [{:name ":disable-autosize?" :type "boolean"    :default "false" :description "Disables automatic textarea height adjustment."}
                        {:name ":placeholder"    :type "string"      :default nil     :description "Placeholder text."}
                        {:name ":on-key-down"    :type "function"    :default nil     :description "Additional keydown handler (runs after submit handling)."}
                        {:name ":class"          :type "string"      :default nil     :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil    :description "Forwarded to textarea."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "prompt-input-actions"
                :description "Horizontal container for action buttons (send, attach, mic, etc.). Additional props are forwarded to container div."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to container div."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "prompt-input-action"
                :description "Tooltip-wrapped action slot. Enhances first child with stopPropagation and disabled state from prompt-input context."
                :props [{:name ":tooltip"       :type "string | hiccup" :default nil  :description "Tooltip content."}
                        {:name ":side"          :type "keyword"     :default ":top"  :description "Tooltip side: :top | :bottom | :left | :right."}
                        {:name ":class"         :type "string"      :default nil     :description "Classes for tooltip content container."}
                        {:name "additional props" :type "map entries" :default nil   :description "Forwarded to tooltip component."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "prompt-input-textarea and prompt-input-action require prompt-input context and will throw if used standalone."]
                [:li "Enter submits via :on-submit, while Shift+Enter inserts a newline."]
                [:li "If you pass :value (controlled mode), keep it synchronized with :on-value-change to avoid stale UI."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [value (r/atom \"\")]\n  [prompt-input {:value @value\n                 :on-value-change #(reset! value %)\n                 :on-submit #(js/console.log \"submit\" @value)}\n   [prompt-input-textarea {:placeholder \"Type your message...\"}]\n   [prompt-input-actions {}\n    [prompt-input-action {:tooltip \"Attach\"} [button {:size :icon} ...]]\n    [prompt-input-action {:tooltip \"Send\"} [button {:size :icon} ...]]]])"]]]]]]))

(defscene
 prompt-input-basic
 "Basic prompt input with send action.
  Provides shared context for textarea + actions.

  Useful for chat or command input fields."
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl"}
       [sut/prompt-input {:value @value
                          :on-value-change #(reset! value %)
                          :on-submit #(js/console.log "submit" @value)}
        [sut/prompt-input-textarea {:placeholder "Type your message..."}]
        [sut/prompt-input-actions {}
         [sut/prompt-input-action {:tooltip "Send"}
          (button/button {:variant :outline
                          :size :icon}
                         [:> Send {:class "size-4"}])]]]]))))

(defscene
 prompt-input-multiple-actions
 "Prompt input with multiple actions.
  Use prompt-input-action to wrap action buttons with tooltips."
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl"}
       [sut/prompt-input {:value @value
                          :on-value-change #(reset! value %)}
        [sut/prompt-input-textarea {:placeholder "Ask a question..."}]
        [sut/prompt-input-actions {}
         [sut/prompt-input-action {:tooltip "Attach file"}
          (button/button {:variant :outline
                          :size :icon}
                         [:> Paperclip {:class "size-4"}])]
         [sut/prompt-input-action {:tooltip "Voice input"}
          (button/button {:variant :outline
                          :size :icon}
                         [:> Mic {:class "size-4"}])]
         [sut/prompt-input-action {:tooltip "Send"}
          (button/button {:variant :outline
                          :size :icon}
                         [:> Send {:class "size-4"}])]]]]))))

(defscene
 prompt-input-disabled
 "Disabled prompt input state.
  Use :disabled? or :is-loading? for disabled styling."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-xl"}
                                     [sut/prompt-input {:value "Saving..."
                                                        :disabled? true}
                                      [sut/prompt-input-textarea {:placeholder "Disabled"}]
                                      [sut/prompt-input-actions {}
                                       [sut/prompt-input-action {:tooltip "Send"}
                                        (button/button {:variant :outline
                                                        :size :icon}
                                                       [:> Send {:class "size-4"}])]]]]))

(defscene
 prompt-input-loading
 "Prompt input with loading state.
  Use :is-loading? when responses are pending."
 []
 (let [value (r/atom "Working on it...")]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-xl"}
                                         [sut/prompt-input {:value @value
                                                            :is-loading? true
                                                            :on-value-change #(reset! value %)}
                                          [sut/prompt-input-textarea {:placeholder "Loading"}]
                                          [sut/prompt-input-actions {}
                                           [sut/prompt-input-action {:tooltip "Send"}
                                            (button/button {:variant :outline
                                                            :size :icon}
                                                           [:> Send {:class "size-4"}])]]]]))))