(ns mateuszmazurczak.portfolio.ui-components.table
  (:require
   [mateuszmazurczak.portfolio.utils   :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.table :as sut]
   [portfolio.reagent-18               :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Table"})

(defscene
 basic-table
 "Table primitives with header and rows." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/table {}
    [sut/table-header {}
     [sut/table-row {}
      [sut/table-head {} "Name"]
      [sut/table-head {} "Role"]
      [sut/table-head {} "Status"]]]
    [sut/table-body {}
     [sut/table-row {}
      [sut/table-cell {} "Alex"]
      [sut/table-cell {} "Engineer"]
      [sut/table-cell {} "Active"]]
     [sut/table-row {}
      [sut/table-cell {} "Jamie"]
      [sut/table-cell {} "Designer"]
      [sut/table-cell {} "Offline"]]]]]))