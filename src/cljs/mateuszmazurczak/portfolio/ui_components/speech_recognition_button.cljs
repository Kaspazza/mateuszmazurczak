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
            :source-code (embed-source "mateuszmazurczak.ui.components.speech_recognition_button")
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
              [mm-portfolio-utils/api-component-card
               {:component-name "speech-recognition-button"
                :description "Microphone action button built on react-speech-recognition. Starts/stops listening and streams transcript text through a callback."
                :props [[":on-transcript-change" "function, optional - Called whenever transcript updates: (fn [text] ...)."]
                        [":language" "string, optional (default \"en-US\") - Speech recognition locale, e.g. \"pl-PL\" or \"en-US\"."]
                        [":continuous" "boolean, optional (default true) - Continue listening after pauses in speech."]
                        [":class" "string, optional - Additional classes applied to the prompt-input action wrapper."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Best used inside prompt-input-actions, because it renders a prompt-input-action wrapper internally."]
                [:li "When browser speech recognition is unsupported, the component renders a disabled button fallback."]
                [:li "For production UX, pair with visible text state so users can confirm recognized transcript."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [value (r/atom \"\")]\n  [prompt-input {:value @value\n                 :on-value-change #(reset! value %)}\n   [prompt-input-textarea {:placeholder \"Speak or type...\"}]\n   [prompt-input-actions {}\n    [speech-recognition-button {:language \"en-US\"\n                                :on-transcript-change #(reset! value %)}]]])"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                [:a {:href "https://www.npmjs.com/package/react-speech-recognition"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "react-speech-recognition Docs →"]]]]]]))

(defscene
 speech-recognition-standalone
 "Standalone speech recognition action.
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