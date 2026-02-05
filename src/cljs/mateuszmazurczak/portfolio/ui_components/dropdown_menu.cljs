(ns mateuszmazurczak.portfolio.ui-components.dropdown-menu
  (:require
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.dropdown-menu :as sut]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Dropdown Menu"})

(defscene
 basic-dropdown-menu
 "Dropdown menu with grouped items."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/dropdown-menu {}
    [sut/dropdown-menu-trigger {:as-child true}
     (button/button {:variant :outline} "Open Menu")]
    [sut/dropdown-menu-content {:align "start"}
     [sut/dropdown-menu-label {} "My Account"]
     [sut/dropdown-menu-item {} "Profile"]
     [sut/dropdown-menu-item {} "Billing"]
     [sut/dropdown-menu-separator {}]
     [sut/dropdown-menu-item {} "Log out"]]]]))