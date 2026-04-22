(ns mateuszmazurczak.portfolio.application.navigation
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.navigation :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :application
                   :title "Navigation"})

(defscene installation
  "Install dependencies and copy the component code into your project."
  []
  [mm-portfolio-utils/installation-scene
   {:description "navigation component."
    :npm-install "No external dependencies"
    :source-code (embed-source "mateuszmazurczak.ui.components.navigation")
    :namespace-path "src/cljs/mateuszmazurczak/ui/components/navigation.cljs"
    :filename "navigation.cljs"}])

(defscene api-reference
  "Complete reference for all Navigation component props and usage patterns."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 max-w-4xl"}
    [:div {:class "space-y-6"}
     [:div
      [:p {:class "text-sm text-muted-foreground"}
       "All available props for Navigation components."]]
     [:div {:class "space-y-4"}
      [mm-portfolio-utils/api-component-card
       {:component-name "navigation"
        :description "Forward navigation link. Accepts href or on-click (either/or) plus label text."
        :props [{:name ":text"     :type "string"   :default nil :description "Link label."}
                {:name ":href"     :type "string"   :default nil :description "Anchor target URL."}
                {:name ":on-click" :type "function" :default nil :description "Click callback for client-side navigation."}
                {:name "note"      :type "info"     :default nil :description "Provide at least one of :href or :on-click."}]}]
      [mm-portfolio-utils/api-component-card
       {:component-name "back-navigation"
        :description "Back-link variant with leading arrow and optional dark text style."
        :props [{:name ":text"     :type "string"   :default nil    :description "Link label."}
                {:name ":href"     :type "string"   :default nil    :description "Anchor target URL."}
                {:name ":on-click" :type "function" :default nil    :description "Click callback for client-side navigation."}
                {:name ":dark?"    :type "boolean"  :default "false" :description "Uses dark-surface text color."}
                {:name "note"      :type "info"     :default nil    :description "Provide at least one of :href or :on-click."}]}]
      [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
       [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
       [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
        [:li "If both :href and :on-click are provided, href wins in current implementation."]
        [:li "Use :on-click for SPA routing when you don't want full-page navigation."]]]
      [:div {:class "border rounded-lg p-4 bg-muted/50"}
       [:h4 {:class "text-sm font-semibold mb-2"}
        "Usage Example"]
       [:pre {:class "text-xs overflow-x-auto"}
        [:code "[:div {:class \"space-y-2\"}\n [navigation {:href \"/docs\" :text \"Go to Docs\"}]\n [back-navigation {:on-click #(js/history.back)\n                   :text \"Back\"\n                   :dark? false}]]"]]]]]]))

(defscene
  navigation-forward
  "Forward navigation link.
  Simple anchor for primary navigation.

  Use :href or :on-click for routing."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      [sut/navigation {:href "#"
                                                       :text "Go to Docs"}]]))

(defscene
  navigation-back
  "Back navigation link with arrow.
  Useful for returning to parent pages."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      [sut/back-navigation {:href "#"
                                                            :text "Back to Home"
                                                            :dark? false}]]))

(defscene
  navigation-dark-mode
  "Back navigation in dark mode variant.
  Use :dark? true to switch text color for dark backgrounds."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-neutral-900"}
                                      [sut/back-navigation {:href "#"
                                                            :text "Back to Home"
                                                            :dark? true}]]))
