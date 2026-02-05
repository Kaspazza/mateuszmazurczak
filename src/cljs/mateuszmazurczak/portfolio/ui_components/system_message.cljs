(ns mateuszmazurczak.portfolio.ui-components.system-message
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.system-message :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "System Message"})

(defscene
 system-message-variants
 "System message variants with optional CTA." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-4 p-6"}
   [sut/system-message {}
    "Informational message for the user."]
   [sut/system-message {:variant :warning}
    "Warning: please review your inputs."]
   [sut/system-message {:variant :error
                        :cta {:label "Retry"
                              :on-click #(js/console.log "retry")}}
    "Something went wrong while saving."]]))