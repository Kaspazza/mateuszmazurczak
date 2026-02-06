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
            :source-code (embed-source mateuszmazurczak.ui.components.message)
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
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Message components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "message"
                                             :description "Message component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "message-avatar"
                                             :description "Message avatar component"
                                             :props [[":src" "string, required - Avatar image URL"]
                                                     [":alt" "string, required - Avatar alt text"]
                                                     [":fallback" "string, optional - Avatar fallback text (e.g. initials)"]
                                                     [":delay-ms" "number, optional - Delay before fallback appears (ms)"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "message-content"
                                             :description "Message content component"
                                             :props [[":markdown?" "boolean, optional (default false) - Render content as markdown"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "message-actions"
                                             :description "Message actions component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "message-action"
                                             :description "Message action component"
                                             :props [[":tooltip" "string | hiccup, required - Tooltip content"]
                                                     [":side" "keyword, optional (default :top). One of: :top | :right | :bottom | :left"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[message {}]"]]]]]]))

(defscene
 message-basic
 "Basic message with avatar and content.

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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