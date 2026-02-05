(ns mateuszmazurczak.portfolio.ui-components.code-block
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.code-block :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Code Block"})

(defscene
 basic-code-block
 "Code block with syntax highlighting."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/code-block {}
    [sut/code-block-group {:class "px-4 py-2 border-b text-xs text-muted-foreground"}
     [:span "hello.cljs"]]
    [sut/code-block-code {:language "clojure"
                          :code "(defn greet [name]\n  (str \"Hello, \" name \"!\"))"}]]]))