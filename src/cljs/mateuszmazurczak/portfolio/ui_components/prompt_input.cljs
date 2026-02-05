(ns mateuszmazurczak.portfolio.ui-components.prompt-input
  (:require
   ["lucide-react"                               :refer [Send]]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.prompt-input  :as sut]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]
   [reagent.core                                 :as r]))

(configure-scenes {:collection :ui-components
                   :title "Prompt Input"})

(defscene
 basic-prompt-input
 "Prompt input with textarea and actions." 
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