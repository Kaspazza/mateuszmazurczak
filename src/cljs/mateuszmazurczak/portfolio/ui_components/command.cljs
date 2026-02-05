(ns mateuszmazurczak.portfolio.ui-components.command
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.command  :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Command"})

(defscene
 basic-command
 "Command palette surface with groups."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-md"}
   [sut/command {}
    [sut/command-input {:placeholder "Search actions..."}]
    [sut/command-list {}
     [sut/command-empty {} "No results found."]
     [sut/command-group {:heading "Suggestions"}
      [sut/command-item {} "Calendar"]
      [sut/command-item {} "Search Emoji"]
      [sut/command-item {} "Calculator"]]
     [sut/command-separator {}]
     [sut/command-group {:heading "Settings"}
      [sut/command-item {} "Profile"]
      [sut/command-item {} "Billing"]]]]]))