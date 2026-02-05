(ns mateuszmazurczak.portfolio.ui-components.loader
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.loader :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Loader"})

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