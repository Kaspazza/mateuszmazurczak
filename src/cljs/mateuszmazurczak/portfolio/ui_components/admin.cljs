(ns mateuszmazurczak.portfolio.ui-components.admin
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.admin :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Admin"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Admin UI components."
            :npm-install "npm install lucide-react"
            :source-code (embed-source mateuszmazurczak.ui.components.admin)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/admin.cljs"
            :filename "admin.cljs"}])

(defscene api-reference
          "Complete reference for all Admin component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Admin components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "admin-badge"
                :description "Admin badge component"
                :props [[":text" "any, optional - Component prop"]
                        [":on-logout" "any, optional - Component prop"]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "delete-solution-button"
                :description "Delete solution button component"
                :props [[":solution-id" "any, optional - Component prop"]
                        [":text" "any, optional - Component prop"]
                        [":on-delete" "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[admin-badge {}]"]]]]]]))

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