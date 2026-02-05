(ns mateuszmazurczak.portfolio.ui-components.markdown
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.markdown :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Markdown"})

(defscene
 basic-markdown
 "Markdown rendering with code blocks." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown {:children "# Hello Markdown\n\nThis is **bold** text with `inline code`.\n\n```clojure\n(defn greet [name]\n  (str \"Hello, \" name))\n```"}]]))