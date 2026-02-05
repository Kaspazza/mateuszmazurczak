(ns mateuszmazurczak.portfolio.ui-components.textarea
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.textarea :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Textarea"})

(defscene
 basic-textarea
 "Textarea for multi-line input." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-md"}
   [sut/textarea {:placeholder "Share your thoughts..."}]]))