(ns mateuszmazurczak.portfolio.ui-components.message
  (:require
   ["lucide-react"                         :refer [Copy Trash2]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.message :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Message"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Message component for chat interfaces."
            :npm-install "No external dependencies"
            :source-code (embed-source "mateuszmazurczak.ui.components.message")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/message.cljs"
            :filename "message.cljs"}])

(defscene
 api-reference
 "Complete reference for all Message component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground mb-4"}
      "Message primitives adapted from Prompt Kit chat patterns. All available props for Message components."]
     [:div {:class "flex flex-wrap gap-2"}
      [:a {:href "https://www.prompt-kit.com/docs/message"
           :target "_blank"
           :rel "noopener noreferrer"
           :class "inline-flex items-center text-sm text-primary hover:underline"}
       "Prompt Kit Message Docs →"]]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "message"
       :description "Root chat message row container. Additional props are forwarded to the wrapper element."
       :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to wrapper element."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "message-avatar"
       :description "Avatar renderer for message sender identity."
       :props [{:name ":src"      :type "string" :default nil :description "Avatar image URL."}
               {:name ":alt"      :type "string" :default nil :description "Avatar alt text."}
               {:name ":fallback" :type "string" :default nil :description "Fallback initials/text."}
               {:name ":delay-ms" :type "number" :default nil :description "Delay before fallback appears."}
               {:name ":class"    :type "string" :default nil :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "message-content"
       :description "Message body container with optional markdown rendering mode."
       :props [{:name ":markdown?"      :type "boolean"      :default "false" :description "Render children/content as markdown."}
               {:name ":class"          :type "string"       :default nil     :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil     :description "Forwarded to content container."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "message-actions"
       :description "Action row container for per-message controls (copy/delete/etc.)."
       :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to action row container."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "message-action"
       :description "Tooltip-wrapped message action slot."
       :props [{:name ":tooltip"        :type "string | hiccup" :default nil   :description "Tooltip content."}
               {:name ":side"           :type "keyword"         :default ":top" :description ":top | :right | :bottom | :left."}
               {:name ":class"          :type "string"          :default nil   :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries"    :default nil   :description "Forwarded to tooltip wrapper."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Compose message-avatar + message-content consistently for predictable row alignment."]
       [:li "Use message-content {:markdown? true} only for trusted/escaped content paths."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[message {:class \"items-start gap-3\"}\n [message-avatar {:src \"https://placehold.co/40x40\" :alt \"Assistant\" :fallback \"AI\"}]\n [:div {:class \"space-y-2\"}\n  [message-content {:markdown? true} \"**Hello** from the assistant\"]\n  [message-actions {}\n   [message-action {:tooltip \"Copy\"} [button {:size :icon} [:> Copy]]]]]]" ]]]]]]))

(defscene
 message-basic
 "Basic message with avatar and content.
  Uses avatar + markdown composition internally.

  Use for simple chat messages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/message {}
                                      [sut/message-avatar {:src "https://placehold.co/40x40/png"
                                                           :alt "User"
                                                           :fallback "JD"}]
                                      [sut/message-content {}
                                       "Hello! This is a basic message."]]]))

(defscene
 message-avatar-fallback
 "Message showing avatar fallback.
  Useful when image URLs fail or are missing."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/message {}
                                      [sut/message-avatar {:src ""
                                                           :alt "User"
                                                           :fallback "AL"}]
                                      [sut/message-content {}
                                       "Fallback initials are shown."]]]))

(defscene
 message-markdown
 "Message with markdown rendering.
  Uses the Markdown component internally.

  Useful for rich assistant responses."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/message {}
    [sut/message-avatar {:src "https://placehold.co/40x40/png"
                         :alt "Assistant"
                         :fallback "AI"}]
    [sut/message-content {:markdown? true}
     "**Markdown** supports lists:\n\n- First\n- Second\n- Third"]]]))

(defscene
 message-actions
 "Message with action buttons.
  Actions are wrapped with tooltips.

  Use for copy, delete, or feedback actions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/message {}
    [sut/message-avatar {:src "https://placehold.co/40x40/png"
                         :alt "User"
                         :fallback "JD"}]
    [:div {:class "flex flex-col gap-2"}
     [sut/message-content {}
      "Here is a message with actions."]
     [sut/message-actions {}
      [sut/message-action {:tooltip "Copy"}
       (button/button {:variant :ghost
                       :size :icon}
                      [:> Copy {:class "size-4"}])]
      [sut/message-action {:tooltip "Delete"}
       (button/button {:variant :ghost
                       :size :icon}
                      [:> Trash2 {:class "size-4"}])]]]]]))

(defscene
 message-user-vs-assistant
 "User vs assistant message styling.
  Use classes to align and style different roles.

  Helpful for chat UIs with role-based presentation."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-4"}
                                     [sut/message {:class "flex-row-reverse text-right"}
                                      [sut/message-avatar {:src "https://placehold.co/40x40/png"
                                                           :alt "User"
                                                           :fallback "ME"}]
                                      [sut/message-content {:class
                                                            "bg-primary text-primary-foreground"}
                                       "User message aligned right."]]
                                     [sut/message {}
                                      [sut/message-avatar {:src "https://placehold.co/40x40/png"
                                                           :alt "Assistant"
                                                           :fallback "AI"}]
                                      [sut/message-content {}
                                       "Assistant response aligned left."]]]))