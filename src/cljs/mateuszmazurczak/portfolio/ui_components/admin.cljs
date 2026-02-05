(ns mateuszmazurczak.portfolio.ui-components.admin
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.admin      :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Admin"})

(defscene
 admin-components
 "Admin badge and delete button examples."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/admin-badge {:text {:admin-mode "Admin Mode"
                            :admin-logout "Logout"}
                     :on-logout #(js/console.log "logout")}]
   [sut/delete-solution-button {:solution-id "solution-123"
                                :text {:confirm-delete "Delete this solution?"
                                       :delete "Delete"}
                                :on-delete #(js/console.log "delete" %)}]]))