(ns mateuszmazurczak.portfolio.ui-components.speech-recognition-button
  (:require
   [mateuszmazurczak.portfolio.utils                         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.prompt-input              :as prompt-input]
   [mateuszmazurczak.ui.components.speech-recognition-button :as sut]
   [portfolio.reagent-18                                     :refer-macros [defscene
                                                                            configure-scenes]]
   [reagent.core                                             :as r]))

(configure-scenes {:collection :ui-components
                   :title "Speech Recognition Button"})

(defscene
 speech-recognition-standalone
 "Standalone speech recognition action.

  Custom component — not from shadcn/ui.
  Wraps react-speech-recognition for microphone input.

  Use inside prompt-input actions for best UX."
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 space-y-2"}
       [:div {:class "text-sm text-muted-foreground"}
        "Transcript: "
        (or @value "(empty)")]
       [prompt-input/prompt-input {:value @value
                                   :on-value-change #(reset! value %)}
        [prompt-input/prompt-input-textarea {:placeholder "Try voice input..."}]
        [prompt-input/prompt-input-actions {}
         [sut/speech-recognition-button {:on-transcript-change #(reset! value %)}]]]]))))

(defscene
 speech-recognition-in-prompt
 "Speech recognition inside prompt input.

  Custom component — not from shadcn/ui.
  Demonstrates composition with prompt-input."
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl"}
       [prompt-input/prompt-input {:value @value
                                   :on-value-change #(reset! value %)}
        [prompt-input/prompt-input-textarea {:placeholder "Speak or type a message..."}]
        [prompt-input/prompt-input-actions {}
         [sut/speech-recognition-button {:on-transcript-change #(reset! value %)}]]]]))))

(defscene
 speech-recognition-transcript
 "Transcript display with live updates.

  Custom component — not from shadcn/ui.
  Shows how to mirror transcript into the UI."
 []
 (let [value (r/atom "")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 space-y-3"}
       [:div {:class "rounded-md border bg-muted/50 p-3 text-sm"}
        "Current transcript: "
        (if (seq @value) @value "(none)")]
       [prompt-input/prompt-input {:value @value
                                   :on-value-change #(reset! value %)}
        [prompt-input/prompt-input-textarea {:placeholder "Start talking..."}]
        [prompt-input/prompt-input-actions {}
         [sut/speech-recognition-button {:on-transcript-change #(reset! value %)}]]]]))))