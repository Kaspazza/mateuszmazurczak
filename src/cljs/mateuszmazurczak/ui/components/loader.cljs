(ns mateuszmazurczak.ui.components.loader
  "Comprehensive loader/spinner components with multiple variants and sizes.
  Provides visual feedback for loading states with various animation styles.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn- size-classes
  "Maps size keywords to Tailwind size classes."
  [size]
  (case size
    :sm "size-4"
    :md "size-5"
    :lg "size-6"
    "size-5"))

(defn circular-loader
  "Circular spinning loader with transparent top border.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [circular-loader {:size :lg}]"
  [{:keys [size class]
    :or {size :md}}]
  [:div {:class (merge-classes
                 "border-primary animate-spin rounded-full border-2 border-t-transparent"
                 (size-classes size)
                 class)}
   [:span {:class "sr-only"}
    "Loading"]])

(defn classic-loader
  "Classic 12-bar radial loader with fade animation.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [classic-loader {:size :md}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [bar-sizes {:sm {:height "6px"
                        :width "1.5px"}
                   :md {:height "8px"
                        :width "2px"}
                   :lg {:height "10px"
                        :width "2.5px"}}
        bar-size (get bar-sizes size)
        margin-left (case size
                      :sm "-0.75px"
                      :lg "-1.25px"
                      "-1px")
        transform-origin (case size
                           :sm "0.75px 10px"
                           :lg "1.25px 14px"
                           "1px 12px")]
    [:div {:class (merge-classes "relative" (size-classes size) class)}
     [:div {:class "absolute h-full w-full"}
      (for [i (range 12)]
        [:div {:key i
               :class "bg-primary absolute animate-[spinner-fade_1.2s_linear_infinite] rounded-full"
               :style {:top "0"
                       :left "50%"
                       :margin-left margin-left
                       :transform-origin transform-origin
                       :transform (str "rotate(" (* i 30) "deg)")
                       :opacity 0
                       :animation-delay (str (* i 0.1) "s")
                       :height (:height bar-size)
                       :width (:width bar-size)}}])]
     [:span {:class "sr-only"}
      "Loading"]]))

(defn pulse-loader
  "Pulsing ring loader with expanding/contracting animation.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [pulse-loader {:size :sm}]"
  [{:keys [size class]
    :or {size :md}}]
  [:div {:class (merge-classes "relative" (size-classes size) class)}
   [:div
    {:class
     "border-primary absolute inset-0 animate-[thin-pulse_1.5s_ease-in-out_infinite] rounded-full border-2"}]
   [:span {:class "sr-only"}
    "Loading"]])

(defn pulse-dot-loader
  "Single pulsing dot loader.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [pulse-dot-loader {}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [dot-sizes {:sm "size-1"
                   :md "size-2"
                   :lg "size-3"}]
    [:div {:class (merge-classes
                   "bg-primary animate-[pulse-dot_1.2s_ease-in-out_infinite] rounded-full"
                   (get dot-sizes size)
                   class)}
     [:span {:class "sr-only"}
      "Loading"]]))

(defn dots-loader
  "Three bouncing dots loader.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [dots-loader {:size :lg}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [dot-sizes {:sm "h-1.5 w-1.5"
                   :md "h-2 w-2"
                   :lg "h-2.5 w-2.5"}
        container-sizes {:sm "h-4"
                         :md "h-5"
                         :lg "h-6"}]
    [:div {:class (merge-classes "flex items-center space-x-1" (get container-sizes size) class)}
     (for [i (range 3)]
       [:div {:key i
              :class (merge-classes
                      "bg-primary animate-[bounce-dots_1.4s_ease-in-out_infinite] rounded-full"
                      (get dot-sizes size))
              :style {:animation-delay (str (* i 160) "ms")}}])
     [:span {:class "sr-only"}
      "Loading"]]))

(defn typing-loader
  "Three dots typing indicator animation.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [typing-loader {}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [dot-sizes {:sm "h-1 w-1"
                   :md "h-1.5 w-1.5"
                   :lg "h-2 w-2"}
        container-sizes {:sm "h-4"
                         :md "h-5"
                         :lg "h-6"}]
    [:div {:class (merge-classes "flex items-center space-x-1" (get container-sizes size) class)}
     (for [i (range 3)]
       [:div {:key i
              :class (merge-classes "bg-primary animate-[typing_1s_infinite] rounded-full"
                                    (get dot-sizes size))
              :style {:animation-delay (str (* i 250) "ms")}}])
     [:span {:class "sr-only"}
      "Loading"]]))

(defn wave-loader
  "Five bars with wave animation pattern.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [wave-loader {:size :md}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [bar-widths {:sm "w-0.5"
                    :md "w-0.5"
                    :lg "w-1"}
        container-sizes {:sm "h-4"
                         :md "h-5"
                         :lg "h-6"}
        heights {:sm ["6px" "9px" "12px" "9px" "6px"]
                 :md ["8px" "12px" "16px" "12px" "8px"]
                 :lg ["10px" "15px" "20px" "15px" "10px"]}]
    [:div {:class (merge-classes "flex items-center gap-0.5" (get container-sizes size) class)}
     (for [i (range 5)]
       [:div {:key i
              :class (merge-classes "bg-primary animate-[wave_1s_ease-in-out_infinite] rounded-full"
                                    (get bar-widths size))
              :style {:animation-delay (str (* i 100) "ms")
                      :height (get-in heights [size i])}}])
     [:span {:class "sr-only"}
      "Loading"]]))

(defn bars-loader
  "Three vertical bars with wave animation.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [bars-loader {}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [bar-widths {:sm "w-1"
                    :md "w-1.5"
                    :lg "w-2"}
        container-sizes {:sm "h-4 gap-1"
                         :md "h-5 gap-1.5"
                         :lg "h-6 gap-2"}]
    [:div {:class (merge-classes "flex" (get container-sizes size) class)}
     (for [i (range 3)]
       [:div {:key i
              :class (merge-classes
                      "bg-primary h-full animate-[wave-bars_1.2s_ease-in-out_infinite]"
                      (get bar-widths size))
              :style {:animation-delay (str (* i 0.2) "s")}}])
     [:span {:class "sr-only"}
      "Loading"]]))

(defn terminal-loader
  "Terminal-style prompt with blinking cursor.
  
  Props:
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [terminal-loader {:size :lg}]"
  [{:keys [size class]
    :or {size :md}}]
  (let [cursor-sizes {:sm "h-3 w-1.5"
                      :md "h-4 w-2"
                      :lg "h-5 w-2.5"}
        text-sizes {:sm "text-xs"
                    :md "text-sm"
                    :lg "text-base"}
        container-sizes {:sm "h-4"
                         :md "h-5"
                         :lg "h-6"}]
    [:div {:class (merge-classes "flex items-center space-x-1" (get container-sizes size) class)}
     [:span {:class (merge-classes "text-primary font-mono" (get text-sizes size))}
      ">"]
     [:div {:class (merge-classes "bg-primary animate-[blink_1s_step-end_infinite]"
                                  (get cursor-sizes size))}]
     [:span {:class "sr-only"}
      "Loading"]]))

(defn text-blink-loader
  "Text with blinking opacity animation.
  
  Props:
  - `:text` - Text to display (default: \"Thinking\")
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [text-blink-loader {:text \"Loading...\" :size :md}]"
  [{:keys [text size class]
    :or {text "Thinking"
         size :md}}]
  (let [text-sizes {:sm "text-xs"
                    :md "text-sm"
                    :lg "text-base"}]
    [:div {:class (merge-classes "animate-[text-blink_2s_ease-in-out_infinite] font-medium"
                                 (get text-sizes size)
                                 class)}
     text]))

(defn text-shimmer-loader
  "Text with animated shimmer gradient effect.
  
  Props:
  - `:text` - Text to display (default: \"Thinking\")
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [text-shimmer-loader {:text \"Processing\" :size :lg}]"
  [{:keys [text size class]
    :or {text "Thinking"
         size :md}}]
  (let [text-sizes {:sm "text-xs"
                    :md "text-sm"
                    :lg "text-base"}]
    [:div
     {:class
      (merge-classes
       "bg-[linear-gradient(to_right,var(--muted-foreground)_40%,var(--foreground)_60%,var(--muted-foreground)_80%)]"
       "bg-size-[200%_auto] bg-clip-text font-medium text-transparent"
       "animate-[shimmer_4s_infinite_linear]"
       (get text-sizes size)
       class)}
     text]))

(defn text-dots-loader
  "Text with animated trailing dots.
  
  Props:
  - `:text` - Text to display (default: \"Thinking\")
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:class` - Additional Tailwind classes
  
  Example:
  [text-dots-loader {:text \"Loading\" :size :sm}]"
  [{:keys [text size class]
    :or {text "Thinking"
         size :md}}]
  (let [text-sizes {:sm "text-xs"
                    :md "text-sm"
                    :lg "text-base"}]
    [:div {:class (merge-classes "inline-flex items-center" class)}
     [:span {:class (merge-classes "text-primary font-medium" (get text-sizes size))}
      text]
     [:span {:class "inline-flex"}
      [:span {:class "text-primary animate-[loading-dots_1.4s_infinite_0.2s]"}
       "."]
      [:span {:class "text-primary animate-[loading-dots_1.4s_infinite_0.4s]"}
       "."]
      [:span {:class "text-primary animate-[loading-dots_1.4s_infinite_0.6s]"}
       "."]]]))

(defn loader
  "Main loader component that renders different variants.
  
  Props:
  - `:variant` - Loader variant (default: :circular)
    - `:circular` - Spinning circle with transparent top
    - `:classic` - 12-bar radial spinner
    - `:pulse` - Pulsing ring
    - `:pulse-dot` - Single pulsing dot
    - `:dots` - Three bouncing dots
    - `:typing` - Typing indicator dots
    - `:wave` - Five bars with wave pattern
    - `:bars` - Three vertical bars
    - `:terminal` - Terminal prompt with cursor
    - `:text-blink` - Blinking text
    - `:text-shimmer` - Shimmer gradient text
    - `:loading-dots` - Text with trailing dots
  - `:size` - Loader size: :sm, :md, :lg (default: :md)
  - `:text` - Text for text-based variants (default: \"Thinking\")
  - `:class` - Additional Tailwind classes
  
  Examples:
  [loader {}]
  [loader {:variant :dots :size :lg}]
  [loader {:variant :text-shimmer :text \"Processing...\" :size :md}]"
  [{:keys [variant size text class]
    :or {variant :circular
         size :md
         text "Thinking"}}]
  (case variant
    :circular [circular-loader {:size size
                                :class class}]
    :classic [classic-loader {:size size
                              :class class}]
    :pulse [pulse-loader {:size size
                          :class class}]
    :pulse-dot [pulse-dot-loader {:size size
                                  :class class}]
    :dots [dots-loader {:size size
                        :class class}]
    :typing [typing-loader {:size size
                            :class class}]
    :wave [wave-loader {:size size
                        :class class}]
    :bars [bars-loader {:size size
                        :class class}]
    :terminal [terminal-loader {:size size
                                :class class}]
    :text-blink [text-blink-loader {:text text
                                    :size size
                                    :class class}]
    :text-shimmer [text-shimmer-loader {:text text
                                        :size size
                                        :class class}]
    :loading-dots [text-dots-loader {:text text
                                     :size size
                                     :class class}]
    ;; default fallback
    [circular-loader {:size size
                      :class class}]))
