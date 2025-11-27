(ns mateuszmazurczak.ui.components.admin
  "Admin UI components."
  (:require
   ["lucide-react"                        :refer [Shield Trash2]]
   [mateuszmazurczak.ui.components.button :as button]))

(defn admin-badge
  "Display admin mode indicator with logout button in header.
   
   Props:
   - :text - Map of translated text
   - :on-logout - Logout handler function"
  [{:keys [text on-logout]}]
  [:div
   {:class
    "flex items-center gap-2 bg-primary text-primary-foreground px-4 py-2 rounded-lg border-2 border-primary-foreground/20"}
   [:> Shield {:class "size-4"}]
   [:span {:class "font-semibold text-sm"}
    (:admin-mode text)]
   [button/button {:variant :ghost
                   :size :xs
                   :class "text-primary-foreground hover:bg-primary-foreground/20"
                   :on-click on-logout}
    (:admin-logout text)]])

(defn delete-solution-button
  "Delete button for solution card (admin only).
   
   Props:
   - :solution-id - Solution ID to delete
   - :text - Map of translated text
   - :on-delete - Delete handler (fn [solution-id])"
  [{:keys [solution-id text on-delete]}]
  [button/button {:variant :destructive
                  :size :xs
                  :on-click #(when (js/confirm (:confirm-delete text)) (on-delete solution-id))}
   [:> Trash2 {:class "size-4"}]
   (:delete text)])
