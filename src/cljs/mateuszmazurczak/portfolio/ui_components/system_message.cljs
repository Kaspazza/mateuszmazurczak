(ns mateuszmazurczak.portfolio.ui-components.system-message
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.system-message :as sut]
   [portfolio.reagent-18                          :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "System Message"})

(defscene
 system-message-default
 "Default system message.

  Custom component — not from shadcn/ui.
  Used for informational or action messages.

  Supports optional CTA buttons via :cta prop."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {}
                                      "This is an informational system message."]]))

(defscene
 system-message-warning
 "Warning system message.

  Custom component — not from shadcn/ui.
  Use :variant :warning for cautionary messages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :warning}
                                      "Warning: please review your inputs."]]))

(defscene
 system-message-error
 "Error system message.

  Custom component — not from shadcn/ui.
  Use :variant :error for failure states."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :error}
                                      "Something went wrong while saving."]]))

(defscene
 system-message-cta
 "System message with CTA button.

  Custom component — not from shadcn/ui.
  Use :cta to provide an inline action."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :error
                                                          :cta {:label "Retry"
                                                                :on-click #(js/console.log
                                                                            "retry")}}
                                      "A network error occurred."]]))

(defscene
 system-message-no-icon
 "System message without icon.

  Custom component — not from shadcn/ui.
  Set :icon-hidden? true for a cleaner layout."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:icon-hidden? true}
                                      "Minimal message without leading icon."]]))