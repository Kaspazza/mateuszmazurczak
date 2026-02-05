(ns mateuszmazurczak.portfolio.ui-components.admin
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.admin :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Admin"})

(defscene
 admin-badge
 "Admin badge with logout action.

  Custom component — not from shadcn/ui.
  Used in admin-only headers.

  Pass translated text via :text map."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/admin-badge {:text {:admin-mode "Admin Mode"
                                                              :admin-logout "Logout"}
                                                       :on-logout #(js/console.log "logout")}]]))

(defscene
 admin-delete-button
 "Admin delete button with confirmation.

  Custom component — not from shadcn/ui.
  Wraps a destructive button with confirm prompt.

  Use :text for localized copy."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/delete-solution-button
                                      {:solution-id "solution-123"
                                       :text {:confirm-delete "Delete this solution?"
                                              :delete "Delete"}
                                       :on-delete #(js/console.log "delete" %)}]]))

(defscene
 admin-panel
 "Combined admin controls.

  Custom component — not from shadcn/ui.
  Demonstrates using admin badge and delete button together."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-4"}
                                     [sut/admin-badge {:text {:admin-mode "Admin Mode"
                                                              :admin-logout "Logout"}
                                                       :on-logout #(js/console.log "logout")}]
                                     [sut/delete-solution-button
                                      {:solution-id "solution-456"
                                       :text {:confirm-delete "Remove this record?"
                                              :delete "Delete"}
                                       :on-delete #(js/console.log "delete" %)}]]))