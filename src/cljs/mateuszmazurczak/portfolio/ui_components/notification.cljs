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
            :source-code (embed-source "mateuszmazurczak.ui.components.notification")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/notification.cljs"
            :filename "notification.cljs"}])

(defscene
 api-reference
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-4"}
    [mm-portfolio-utils/api-component-card
     {:component-name "toaster"
      :link {:href "https://sonner.emilkowal.ski" :label "Sonner Docs"}
      :description "Sonner provider component. Mount once near app root before calling show-* helpers."
       :props [{:name "arguments"         :type "none"   :default nil :description "(toaster) or (toaster {:position ... :richColors ...})"}
               {:name "supported options" :type "any"    :default nil :description "All Sonner Toaster props are accepted and forwarded."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-toast"
       :description "Generic toast helper around sonner/toast."
       :props [{:name "arity"               :type "none"            :default nil          :description "(show-toast message) or (show-toast message options)"}
               {:name "message"             :type "string"          :default nil          :description "Primary toast text."}
               {:name "options/:description" :type "string"         :default nil          :description "Secondary text."}
               {:name "options/:action"      :type "map"            :default nil          :description "{:label string :on-click fn}."}
               {:name "options/:cancel"      :type "map"            :default nil          :description "{:label string :on-click fn}."}
               {:name "options/:duration"    :type "number"         :default nil          :description "Duration in ms."}
               {:name "options/:position"    :type "string"         :default "\"top-right\"" :description "Sonner position."}
               {:name "options/:id"          :type "string | number" :default nil         :description "Custom toast id."}
               {:name "options/:important"   :type "boolean"        :default nil          :description "Prevent dismissal."}
               {:name "options/:on-dismiss"  :type "function"       :default nil          :description "Callback (fn [])."}
               {:name "options/:on-auto-close" :type "function"     :default nil          :description "Callback (fn [])."}
               {:name "returns"             :type "any"             :default nil          :description "toast id from Sonner."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-success"
       :description "Success-themed toast helper."
       :props [{:name "arity"   :type "none"   :default nil :description "(show-success message) or (show-success message options)"}
               {:name "message" :type "string" :default nil :description "Toast title text."}
               {:name "options" :type "map"    :default nil :description "Sonner toast options."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-error"
       :description "Error-themed toast helper."
       :props [{:name "arity"   :type "none"   :default nil :description "(show-error message) or (show-error message options)"}
               {:name "message" :type "string" :default nil :description "Toast title text."}
               {:name "options" :type "map"    :default nil :description "Sonner toast options."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-info"
       :description "Info-themed toast helper."
       :props [{:name "arity"   :type "none"   :default nil :description "(show-info message) or (show-info message options)"}
               {:name "message" :type "string" :default nil :description "Toast title text."}
               {:name "options" :type "map"    :default nil :description "Sonner toast options."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-warning"
       :description "Warning-themed toast helper."
       :props [{:name "arity"   :type "none"   :default nil :description "(show-warning message) or (show-warning message options)"}
               {:name "message" :type "string" :default nil :description "Toast title text."}
               {:name "options" :type "map"    :default nil :description "Sonner toast options."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-loading"
       :description "Loading toast helper (returns id for later dismissal/update)."
       :props [{:name "arity"   :type "none"   :default nil :description "(show-loading message) or (show-loading message options)"}
               {:name "message" :type "string" :default nil :description "Loading text."}
               {:name "options" :type "map"    :default nil :description "Sonner toast options."}
               {:name "returns" :type "any"    :default nil :description "toast id."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "show-promise"
       :description "Promise lifecycle toast helper (loading/success/error)."
       :props [{:name "arity"    :type "none"            :default nil :description "(show-promise promise messages options)"}
               {:name "promise"  :type "js/Promise"      :default nil :description "js/Promise or thenable."}
               {:name "messages" :type "map"             :default nil :description "{:loading :success :error} labels/content."}
               {:name "options"  :type "map"             :default nil :description "Sonner options."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dismiss-toast"
       :description "Dismiss one toast by id, or all toasts when called without args."
       :props [{:name "arity"    :type "none"            :default nil :description "(dismiss-toast) or (dismiss-toast toast-id)"}
               {:name "toast-id" :type "string | number" :default nil :description "Toast id to dismiss."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "custom-toast"
       :description "Render custom Reagent/React content in a toast."
       :props [{:name "arity"     :type "none"   :default nil :description "(custom-toast component options)"}
               {:name "component" :type "hiccup" :default nil :description "hiccup vector or React element."}
               {:name "options"   :type "map"    :default nil :description "Sonner options."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Mount [toaster] once in app root; helper functions won't render anything by themselves."]
       [:li "These APIs are function calls (show-toast, dismiss-toast, etc.), not components."]
       [:li "Action/cancel callbacks are wrapped for Sonner; keep side effects idempotent."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[:<>\n [toaster {:position \"top-right\"}]\n [button {:on-click #(show-success \"Saved\" {:description \"Profile updated\"})}\n  \"Notify\"]\n [button {:on-click #(let [id (show-loading \"Uploading...\")]\n                       (js/setTimeout #(dismiss-toast id) 1500))}\n  \"Loading demo\"]]"]]
      ]]]]))

(defscene
 toast-basic
 "Basic toast notification.
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
  Use :duration for longer or shorter visibility."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn [] (sut/show-toast "Auto closes in 10 seconds" {:duration 10000}))}
                  "Show Long Toast")]))