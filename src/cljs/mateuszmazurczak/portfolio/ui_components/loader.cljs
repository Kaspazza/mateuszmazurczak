(ns mateuszmazurczak.portfolio.ui-components.loader
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.loader :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Loader"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Comprehensive loader/spinner components with multiple variants and sizes."
            :npm-install "npm install 10px 6px 8px"
            :source-code (embed-source mateuszmazurczak.ui.components.loader)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/loader.cljs"
            :filename "loader.cljs"}])

(defscene
 api-reference
 "Complete reference for all Loader component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Loader components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "circular-loader"
                                             :description "Circular loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "classic-loader"
                                             :description "Classic loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "pulse-loader"
                                             :description "Pulse loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "pulse-dot-loader"
                                             :description "Pulse dot loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "dots-loader"
                                             :description "Dots loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "typing-loader"
                                             :description "Typing loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "wave-loader"
                                             :description "Wave loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "bars-loader"
                                             :description "Bars loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "terminal-loader"
                                             :description "Terminal loader component"
                                             :props [[":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "text-blink-loader"
                                             :description "Text blink loader component"
                                             :props [[":text" "string, optional (default 'Thinking') - Text label"]
                                                     [":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "text-shimmer-loader"
                                             :description "Text shimmer loader component"
                                             :props [[":text" "string, optional (default 'Thinking') - Text label"]
                                                     [":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "text-dots-loader"
                                             :description "Text dots loader component"
                                             :props [[":text" "string, optional (default 'Thinking') - Text label"]
                                                     [":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "loader"
                                             :description "Loader component"
                                             :props [[":variant" "keyword, optional (default :circular). One of: :circular | :classic | :pulse | :pulse-dot | :dots | :typing | :wave | :bars | :terminal | :text-blink | :text-shimmer | :loading-dots"]
                                                     [":size" "keyword, optional (default :md). One of: :sm | :md | :lg"]
                                                     [":text" "string, optional (default 'Thinking') - Text label"]
                                                     [":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[circular-loader {}]"]]]]]]))

(defscene
 loader-all-variants
 "All loader variants in a single grid.

  Custom component — not from shadcn/ui.
  Built with pure CSS animations and Tailwind classes.

  Variants: :circular, :classic, :pulse, :pulse-dot, :dots, :typing,
  :wave, :bars, :terminal, :text-blink, :text-shimmer, :loading-dots."
 []
 (let [variants [:circular
                 :classic
                 :pulse
                 :pulse-dot
                 :dots
                 :typing
                 :wave
                 :bars
                 :terminal
                 :text-blink
                 :text-shimmer
                 :loading-dots]]
   (mm-portfolio-utils/wrap-component
    [:div {:class "p-6 grid gap-4 sm:grid-cols-3"}
     (for [variant variants]
       ^{:key variant}
       [:div {:class "flex flex-col items-center gap-2 rounded-md border p-4"}
        [sut/loader {:variant variant
                     :text "Thinking"}]
        [:span {:class "text-xs text-muted-foreground"}
         (name variant)]])])))

(defscene
 loader-sizes
 "Loader size comparison.

  Custom component — not from shadcn/ui.
  All loaders support :sm, :md, :lg sizes."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-6"}
                                     [sut/loader {:variant :circular
                                                  :size :sm}]
                                     [sut/loader {:variant :circular
                                                  :size :md}]
                                     [sut/loader {:variant :circular
                                                  :size :lg}]]))

(defscene
 loader-text-variants
 "Text-based loaders with custom text.

  Custom component — not from shadcn/ui.
  Text variants: :text-blink, :text-shimmer, :loading-dots."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-3"}
                                     [sut/loader {:variant :text-blink
                                                  :text "Thinking"
                                                  :size :md}]
                                     [sut/loader {:variant :text-shimmer
                                                  :text "Processing"
                                                  :size :md}]
                                     [sut/loader {:variant :loading-dots
                                                  :text "Loading"
                                                  :size :md}]]))

(defscene
 loader-component
 "Unified loader component with :variant prop.

  Custom component — not from shadcn/ui.
  Use the single `loader` function to switch animations per state."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-6"}
                                     [sut/loader {:variant :pulse}]
                                     [sut/loader {:variant :typing}]
                                     [sut/loader {:variant :terminal}]]))