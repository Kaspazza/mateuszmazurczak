(ns mateuszmazurczak.portfolio.ui-components.speech-recognition-button
  (:require
   [mateuszmazurczak.portfolio.utils                       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.prompt-input            :as prompt-input]
   [mateuszmazurczak.ui.components.speech-recognition-button :as sut]
   [portfolio.reagent-18                                   :refer-macros [defscene configure-scenes]]
   [reagent.core                                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Speech Recognition Button"})

(defscene
 speech-recognition-action
 "Speech recognition button inside prompt input." 
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl"}
       [prompt-input/prompt-input {:value @value
                                   :on-value-change #(reset! value %)}
        [prompt-input/prompt-input-textarea {:placeholder "Try voice input..."}]
        [prompt-input/prompt-input-actions {}
         [sut/speech-recognition-button {:on-transcript-change #(reset! value %)}]]]]))))