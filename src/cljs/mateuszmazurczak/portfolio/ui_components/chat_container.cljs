(ns mateuszmazurczak.portfolio.ui-components.chat-container
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as sut]
   [mateuszmazurczak.ui.components.scroll-button  :as scroll-button]
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
            :source-code (embed-source "mateuszmazurczak.ui.components.chat_container")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/chat_container.cljs"
            :filename "chat_container.cljs"}])

(defscene
 api-reference
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-4"}
    [mm-portfolio-utils/api-component-card
     {:component-name "chat-container-root"
      :link {:href "https://www.prompt-kit.com/docs/chat-container" :label "Prompt Kit Chat Container Docs"}
      :description "Root scroll container powered by use-stick-to-bottom context."
      :props [{:name ":class"   :type "string" :default nil        :description "Additional Tailwind classes"}
              {:name ":resize"  :type "string" :default "\"smooth\"" :description "One of: 'smooth' | 'instant'"}
              {:name ":initial" :type "string" :default "\"instant\"" :description "One of: 'instant' | 'smooth'"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "chat-container-content"
       :description "Message list/content region within chat-container-root."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "chat-container-scroll-anchor"
       :description "Anchor marker used for stick-to-bottom behavior and scroll targeting."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Always keep chat-container-scroll-anchor as the last child of chat-container-content."]
       [:li "scroll-button and other stick-to-bottom consumers must be nested inside chat-container-root."]
       [:li "For accessibility, consider setting role=\"log\" and aria-live semantics on content wrappers when needed."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[chat-container-root {:class \"h-64 border rounded-md\"}\n [chat-container-content {:class \"p-4 space-y-2\"}\n  [:div \"Message 1\"]\n  [:div \"Message 2\"]\n  [chat-container-scroll-anchor {}]]\n [scroll-button {:class \"absolute bottom-4 right-4\"}]]" ]]]]]]))

(defscene
 chat-container-basic
 "Chat container with scroll anchor.
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
 "Chat container with many messages and scroll-to-bottom button.
  Demonstrates overflow, stick-to-bottom behavior, and interactive recovery when scrolled up."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/chat-container-root {:class
                                                               "relative h-64 w-full rounded-md border"}
                                      [sut/chat-container-content {:class "p-4 space-y-3"}
                                       (for [idx (range 1 25)]
                                         ^{:key idx}
                                         [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
                                          (str "Log line " idx " — status update.")])
                                       [sut/chat-container-scroll-anchor {}]]
                                      [scroll-button/scroll-button {:class "absolute bottom-4 right-4"}]]]))

(defscene
 chat-container-composition
 "Chat container with header and footer content.
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
