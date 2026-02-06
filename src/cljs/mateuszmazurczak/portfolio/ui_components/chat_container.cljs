(ns mateuszmazurczak.portfolio.ui-components.chat-container
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as sut]
   [portfolio.reagent-18                          :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Chat Container"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Chat container component with auto-scroll-to-bottom functionality."
            :npm-install "npm install use-stick-to-bottom"
            :source-code (embed-source mateuszmazurczak.ui.components.chat_container)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/chat_container.cljs"
            :filename "chat_container.cljs"}])

(defscene
 api-reference
 "Complete reference for all Chat Container component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Chat Container components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "chat-container-root"
                                             :description "Chat container root component"
                                             :props [[":class" "any, optional - Component prop"]
                                                     [":resize" "any, optional - Component prop"]
                                                     [":initial"
                                                      "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "chat-container-content"
                                             :description "Chat container content component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "chat-container-scroll-anchor"
                                             :description "Chat container scroll anchor component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[chat-container-root {}]"]]]]]]))

(defscene
 chat-container-basic
 "Chat container with scroll anchor.

  Custom component — not from shadcn/ui.
  Built on use-stick-to-bottom for smooth scrolling.

  Use chat-container-scroll-anchor for auto-scroll behavior."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/chat-container-root {:class
                                                               "h-64 w-full rounded-md border"}
                                      [sut/chat-container-content {:class "p-4 space-y-3"}
                                       (for [idx (range 1 8)]
                                         ^{:key idx}
                                         [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
                                          (str "Message " idx)])
                                       [sut/chat-container-scroll-anchor {}]]]]))

(defscene
 chat-container-long
 "Chat container with many messages.

  Custom component — not from shadcn/ui.
  Demonstrates overflow and scroll behavior with longer content."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/chat-container-root {:class
                                                               "h-64 w-full rounded-md border"}
                                      [sut/chat-container-content {:class "p-4 space-y-3"}
                                       (for [idx (range 1 25)]
                                         ^{:key idx}
                                         [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
                                          (str "Log line " idx " — status update.")])
                                       [sut/chat-container-scroll-anchor {}]]]]))

(defscene
 chat-container-composition
 "Chat container with header and footer content.

  Custom component — not from shadcn/ui.
  Use additional elements around the scroll region for composition."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "rounded-md border"}
                                      [:div {:class "border-b px-4 py-2 text-sm font-medium"}
                                       "Team Chat"]
                                      [sut/chat-container-root {:class "h-56"}
                                       [sut/chat-container-content {:class "p-4 space-y-3"}
                                        (for [idx (range 1 10)]
                                          ^{:key idx}
                                          [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
                                           (str "Message " idx)])
                                        [sut/chat-container-scroll-anchor {}]]]
                                      [:div {:class
                                             "border-t px-4 py-2 text-xs text-muted-foreground"}
                                       "Typing indicator goes here."]]]))