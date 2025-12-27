(ns mateuszmazurczak.ui.components.speech-recognition-button
  "Microphone button component with speech recognition functionality."
  (:require
   ["lucide-react"                              :refer [Mic]]
   ["react-speech-recognition"                  :default SpeechRecognition
                                                :refer   [useSpeechRecognition]]
   [mateuszmazurczak.ui.components.button       :as mateuszmazurczak-button]
   [mateuszmazurczak.ui.components.loader       :as loader]
   [mateuszmazurczak.ui.components.prompt-input :as prompt-input]
   [reagent.core                                :as    r
                                                :refer [defc]]
   [reagent.hooks                               :as rhooks]))

(defc speech-recognition-button
 "Microphone button with speech recognition that updates text on change.
  
  Props:
  - `:on-transcript-change` - Callback function called with transcript text (fn [text] ...)
  - `:language` - Language code for speech recognition (default: \"en-US\")
                  Examples: \"en-US\", \"pl-PL\", \"es-ES\", \"fr-FR\", \"de-DE\"
  - `:continuous` - Whether to continue listening after speech stops (default: true)
  - `:class` - Additional CSS classes for the button wrapper"
 [{:keys [on-transcript-change language continuous class]
   :or {language "en-US"
        continuous true}}]
 (let [;; Speech recognition hook
       speech-recognition (useSpeechRecognition)
       transcript (.-transcript speech-recognition)
       listening? (.-listening speech-recognition)
       browser-supports? (.-browserSupportsSpeechRecognition speech-recognition)
       reset-transcript! (.-resetTranscript speech-recognition)
       _ (rhooks/use-effect (fn []
                              (when (and (seq transcript) on-transcript-change)
                                (on-transcript-change transcript))
                              js/undefined)
                            [transcript on-transcript-change])
       handle-mic-toggle (rhooks/use-callback (fn []
                                                (if listening?
                                                  (.stopListening SpeechRecognition)
                                                  (do (reset-transcript!)
                                                      (.startListening SpeechRecognition
                                                                       #js {:continuous continuous
                                                                            :language language}))))
                                              [listening? reset-transcript! continuous language])]
   (if browser-supports?
     [prompt-input/prompt-input-action {:tooltip (if listening? "Stop recording" "Voice input")
                                        :class class}
      [mateuszmazurczak-button/button {:variant :outline
                                       :size :icon
                                       :class "size-9 rounded-full"
                                       :on-click handle-mic-toggle}
       (if listening?
         [loader/loader {:variant :wave
                         :size :sm}]
         [:> Mic {:size 18}])]]
     [prompt-input/prompt-input-action {:tooltip "Speech recognition not supported in this browser"
                                        :class class}
      [mateuszmazurczak-button/button {:variant :outline
                                       :size :icon
                                       :disabled true
                                       :class "size-9 rounded-full"}
       [:> Mic {:size 18}]]])))
