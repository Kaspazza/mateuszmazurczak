(ns mateuszmazurczak.portfolio.ui-components.field
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field    :as sut]
   [mateuszmazurczak.ui.components.input    :as input]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Field"})

(defscene
 basic-field
 "Form field layout with description."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-md"}
   [sut/field-set {}
    [sut/field-legend {} "Profile"]
    [sut/field {}
     [sut/field-label {:html-for "name"} "Name"]
     [sut/field-content {}
      [input/input {:id "name"
                    :placeholder "Jane Doe"}]
      [sut/field-description {}
       "Use your full name for display purposes."]]]]]))