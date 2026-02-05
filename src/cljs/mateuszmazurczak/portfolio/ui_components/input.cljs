(ns mateuszmazurczak.portfolio.ui-components.input
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.input :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Input"})

(defscene
 basic-inputs
 "Text and email input examples."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-4 p-6 max-w-sm"}
   [sut/input {:placeholder "Your name"}]
   [sut/input {:type "email"
               :placeholder "you@example.com"}]
   [sut/input {:type "password"
               :placeholder "Password"}]]))