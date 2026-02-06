(ns mateuszmazurczak.portfolio.ui-components.speech-recognition-button
  (:require
   [mateuszmazurczak.portfolio.utils                         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.prompt-input              :as prompt-input]
   [mateuszmazurczak.ui.components.speech-recognition-button :as sut]
   [portfolio.reagent-18                                     :refer-macros [defscene
                                                                            configure-scenes]]
   [reagent.core                                             :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Speech Recognition Button"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Microphone button component with speech recognition functionality."
            :npm-install "npm install lucide-react react-speech-recognition"
            :source-code (embed-source mateuszmazurczak.ui.components.speech_recognition_button)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/speech_recognition_button.cljs"
            :filename "speech_recognition_button.cljs"}])

(defscene api-reference
          "Complete reference for all Speech Recognition Button component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Speech Recognition Button components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card {:component-name "component"
                                                      :description "Component"
                                                      :props []}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[speech_recognition_button {}]"]]]]]]))

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