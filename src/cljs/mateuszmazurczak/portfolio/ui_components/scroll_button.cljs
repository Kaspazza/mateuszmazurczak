(ns mateuszmazurczak.portfolio.ui-components.scroll-button
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as chat-container]
   [mateuszmazurczak.ui.components.scroll-button  :as sut]
   [portfolio.reagent-18                          :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Scroll Button"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Scroll-to-bottom button that appears when not at the bottom of a scrollable container."
            :npm-install "npm install lucide-react use-stick-to-bottom"
            :source-code (embed-source "mateuszmazurczak.ui.components.scroll_button")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/scroll_button.cljs"
            :filename "scroll_button.cljs"}])

(defscene api-reference
          "Complete reference for all Scroll Button component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground mb-4"}
               "Scroll button primitive adapted from Prompt Kit chat patterns. All available props for Scroll Button components."]
              [:div {:class "flex flex-wrap gap-2"}
               [:a {:href "https://www.prompt-kit.com/docs/scroll-button"
                    :target "_blank"
                    :rel "noopener noreferrer"
                    :class "inline-flex items-center text-sm text-primary hover:underline"}
                "Prompt Kit Scroll Button Docs →"]]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "scroll-button"
                :description "Floating action button that appears when chat content is not pinned to the bottom. Integrates with use-stick-to-bottom context and scrolls smoothly to the newest message."
                :props [{:name ":variant"        :type "keyword" :default ":outline" :description "Button variant forwarded to button component."}
                        {:name ":size"           :type "keyword" :default ":sm"      :description "Button size forwarded to button component."}
                        {:name ":class"          :type "string"  :default nil        :description "Additional Tailwind classes merged with default visibility/position classes."}
                        {:name "additional props" :type "map entries" :default nil   :description "Forwarded to the underlying button component."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Must be rendered inside chat-container-root; otherwise useStickToBottom context is unavailable."]
                [:li "Include chat-container-scroll-anchor as the last child of chat-container-content for correct bottom detection."]
                [:li "Visibility is managed automatically via transform/opacity classes based on scroll position."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[chat-container-root {:class \"relative h-64\"}\n  [chat-container-content {}\n    ;; messages\n    [chat-container-scroll-anchor {}]]\n  [scroll-button {:class \"absolute bottom-4 right-4\"}]]"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                [:a {:href "https://www.npmjs.com/package/use-stick-to-bottom"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "use-stick-to-bottom Docs →"]]]]]]))

(defscene
 scroll-button-chat
 "Scroll-to-bottom button inside chat container.
  Uses use-stick-to-bottom context from chat-container.

  Appears when user scrolls away from bottom."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-64 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 20)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Line " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-4 right-4"}]]]))

(defscene
 scroll-button-custom
 "Scroll button with custom styling.
  Use class overrides for position or style changes."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-64 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 16)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Message " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-4 right-4 bg-primary text-primary-foreground"}]]]))

(defscene
 scroll-button-standalone
 "Scroll button placement in custom layout.
  Place inside any stick-to-bottom container."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-40 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 12)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Update " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-3 right-3"}]]]))