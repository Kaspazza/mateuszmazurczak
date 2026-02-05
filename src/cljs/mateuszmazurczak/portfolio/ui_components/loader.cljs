(ns mateuszmazurczak.portfolio.ui-components.loader
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.loader :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Loader"})

(defscene
 loader-variants
 "Loader variants for different contexts."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-wrap items-center gap-6 p-6"}
   [sut/circular-loader {:size :md}]
   [sut/dots-loader {:size :md}]
   [sut/typing-loader {:size :md}]
   [sut/wave-loader {:size :md}]]))