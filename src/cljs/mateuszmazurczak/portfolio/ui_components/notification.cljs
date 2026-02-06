(ns mateuszmazurczak.portfolio.ui-components.notification
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button       :as button]
   [mateuszmazurczak.ui.components.notification :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Notification"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Toast notification component using Sonner."
            :npm-install "npm install lucide-react sonner"
            :source-code (embed-source mateuszmazurczak.ui.components.notification)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/notification.cljs"
            :filename "notification.cljs"}])

(defscene
 api-reference
 "Complete reference for all Notification component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Notification components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "toaster"
                                             :description "Toaster component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-toast"
       :description "Show toast component"
       :props
       [[":description" "string, optional - Secondary toast message"]
        [":action" "map, optional - Action config {:label string :on-click fn}"]
        [":duration" "number, optional - Toast duration in milliseconds"]
        [":position"
         "string, optional (default 'top-right'). One of: 'top-left' | 'top-center' | 'top-right' | 'bottom-left' | 'bottom-center' | 'bottom-right'"]
        [":cancel" "map, optional - Cancel config {:label string :on-click fn}"]
        [":id" "string | number, optional - Custom toast id"]
        [":important" "boolean, optional - Prevent dismissal"]
        [":on-dismiss" "function, optional - Callback (fn []) on dismiss"]
        [":on-auto-close" "function, optional - Callback (fn []) on auto-close"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "show-success"
                                             :description "Show success component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "show-error"
                                             :description "Show error component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "show-info"
                                             :description "Show info component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "show-warning"
                                             :description "Show warning component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "show-loading"
                                             :description "Show loading component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "show-promise"
                                             :description "Show promise component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "dismiss-toast"
                                             :description "Dismiss toast component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "custom-toast"
                                             :description "Custom toast component"
                                             :props []}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[toaster {}]"]]]]]]))

(defscene
 toast-basic
 "Basic toast notification.

  Custom component — not from shadcn/ui.
  Uses Sonner for toast rendering.

  Call show-toast with a simple message."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn [] (sut/show-toast "Event has been created"))} "Show Toast")]))

(defscene
 toast-with-description
 "Toast with description text.

  Custom component — not from shadcn/ui.
  Use :description to provide context."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn []
                               (sut/show-toast "Event created"
                                               {:description "Sunday, December 3 at 9:00 AM"}))}
                  "Show Detailed Toast")]))

(defscene
 toast-with-action
 "Toast with action button.

  Custom component — not from shadcn/ui.
  Use :action for undo or follow-up steps."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn []
                               (sut/show-toast "File deleted"
                                               {:action {:label "Undo"
                                                         :on-click (fn []
                                                                     (js/console.log "undo"))}}))}
                  "Show Action Toast")]))

(defscene
 toast-duration
 "Toast with custom duration.

  Custom component — not from shadcn/ui.
  Use :duration for longer or shorter visibility."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn [] (sut/show-toast "Auto closes in 10 seconds" {:duration 10000}))}
                  "Show Long Toast")]))