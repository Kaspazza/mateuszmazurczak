(ns mateuszmazurczak.portfolio.ui-components.system-message
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.system-message :as sut]
   [portfolio.reagent-18                          :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "System Message"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "System message component for displaying notifications, alerts, and status messages."
            :npm-install "npm install lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.system_message")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/system_message.cljs"
            :filename "system_message.cljs"}])

(defscene api-reference
          "Complete reference for all System Message component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground mb-4"}
               "System message primitive adapted from Prompt Kit chat patterns. All available props for System Message components."]
              [:div {:class "flex flex-wrap gap-2"}
               [:a {:href "https://www.prompt-kit.com/docs/system-message"
                    :target "_blank"
                    :rel "noopener noreferrer"
                    :class "inline-flex items-center text-sm text-primary hover:underline"}
                "Prompt Kit System Message Docs →"]]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "system-message"
                :description "System message component"
                :props [[":variant"
                         "keyword, optional (default :action). One of: :action | :error | :warning"]
                        [":fill" "boolean, optional (default false) - Filled background style"]
                        [":icon" "react-component | hiccup, optional - Custom icon component"]
                        [":icon-hidden?" "boolean, optional (default false) - Hide icon"]
                        [":cta" "map, optional - CTA config {:label string :on-click fn}"]
                        [":class" "string, optional - Additional Tailwind classes"]
                        ["children" "required - Message content/body text or hiccup."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Variants currently documented are :action, :error, :warning (no :info variant in public contract)."]
                [:li "Use :fill true for stronger visual emphasis in high-salience alerts."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[system-message {:variant :action\n                 :icon [:span \"ℹ️\"]\n                 :fill true}\n \"Your workspace sync is up to date.\"]" ]]]]]]))

(defscene
 system-message-default
 "Default system message.
  Used for informational or action messages.

  Supports optional CTA buttons via :cta prop."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {}
                                      "This is an informational system message."]]))

(defscene
 system-message-warning
 "Warning system message.
  Use :variant :warning for cautionary messages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :warning}
                                      "Warning: please review your inputs."]]))

(defscene
 system-message-error
 "Error system message.
  Use :variant :error for failure states."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :error}
                                      "Something went wrong while saving."]]))

(defscene
 system-message-cta
 "System message with CTA button.
  Use :cta to provide an inline action."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :error
                                                          :cta {:label "Retry"
                                                                :on-click #(js/console.log
                                                                            "retry")}}
                                      "A network error occurred."]]))

(defscene
 system-message-no-icon
 "System message without icon.
  Set :icon-hidden? true for a cleaner layout."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:icon-hidden? true}
                                      "Minimal message without leading icon."]]))

(defscene
 system-message-filled
 "Filled system message style.
  Use :fill true for stronger visual emphasis."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :warning
                                                          :fill true}
                                      "Maintenance window starts in 10 minutes."]]))

(defscene
 system-message-custom-icon
 "System message with custom icon.
  Use :icon to override the default icon per message context."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/system-message {:variant :action
                                                          :icon [:span {:aria-hidden true}
                                                                 "🔔"]}
                                      "You have new workspace invitations."]]))