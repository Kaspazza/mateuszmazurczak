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
            :source-code (embed-source "mateuszmazurczak.ui.components.admin")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/admin.cljs"
            :filename "admin.cljs"}])

(defscene
 api-reference
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
       :description "Admin header badge with logout action."
       :props [[":text" "map, required - Must include keys :admin-mode and :admin-logout."]
               [":on-logout" "function, required - Callback (fn []) triggered on logout click."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "delete-solution-button"
       :description "Destructive admin action with confirmation prompt."
       :props [[":solution-id" "string | number, required - Solution identifier passed to delete callback."]
               [":text" "map, required - Must include keys :confirm-delete and :delete."]
               [":on-delete" "function, required - Callback (fn [solution-id]) after confirmation."]]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "The :text map is part of public contract; missing expected keys will result in missing labels."]
       [:li "Keep :on-delete idempotent and server-validated; this control is only a UI guard, not authorization."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[:div {:class \"space-y-2\"}\n [admin-badge {:text {:admin-mode \"Admin mode\"\n                      :admin-logout \"Logout\"}\n               :on-logout #(js/console.log \"logout\")} ]\n [delete-solution-button {:solution-id \"sol-123\"\n                          :text {:confirm-delete \"Delete this solution?\"\n                                 :delete \"Delete\"}\n                          :on-delete #(js/console.log %)}]]"]]]]]]))

(defscene
 admin-badge
 "Admin badge with logout action.
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