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
            :source-code (embed-source mateuszmazurczak.ui.components.prompt_input)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/prompt_input.cljs"
            :filename "prompt_input.cljs"}])

(defscene api-reference
          "Complete reference for all Prompt Input component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Prompt Input components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card {:component-name "prompt-input-actions"
                                                      :description "Prompt input actions component"
                                                      :props [[":class"
                                                               "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[prompt-input-actions {}]"]]]]]]))

(defscene
 prompt-input-basic
 "Basic prompt input with send action.

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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