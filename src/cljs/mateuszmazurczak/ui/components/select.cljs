(ns mateuszmazurczak.ui.components.select
  "https://www.radix-ui.com/primitives/docs/components/select

Version: 1.0.0
Last updated: 2026-02-06

Based on Radix UI primitives.
Documentation: https://www.radix-ui.com/primitives/docs/components/select"
  (:require
   ["@radix-ui/react-select"      :as RadixSelect]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]
   [reagent.core                  :as r]))

(defn- chevron-down-icon
  []
  [:svg {:class "size-4 opacity-50"
         :xmlns "http://www.w3.org/2000/svg"
         :width "24"
         :height "24"
         :view-box "0 0 24 24"
         :fill "none"
         :stroke "currentColor"
         :stroke-width "2"}
   [:path {:d "m6 9 6 6 6-6"}]])

(defn- chevron-up-icon
  []
  [:svg {:class "size-4"
         :xmlns "http://www.w3.org/2000/svg"
         :width "24"
         :height "24"
         :view-box "0 0 24 24"
         :fill "none"
         :stroke "currentColor"
         :stroke-width "2"}
   [:path {:d "m18 15-6-6-6 6"}]])

(defn- check-icon
  []
  [:svg {:class "size-4"
         :xmlns "http://www.w3.org/2000/svg"
         :width "24"
         :height "24"
         :view-box "0 0 24 24"
         :fill "none"
         :stroke "currentColor"
         :stroke-width "2"}
   [:path {:d "M20 6 9 17l-5-5"}]])

(defn select
  [props & children]
  (into [:> RadixSelect/Root (assoc props :data-slot "select")] children))

(defn select-group
  [props & children]
  (into [:> RadixSelect/Group (assoc props :data-slot "select-group")] children))

(defn select-value [props] [:> RadixSelect/Value (assoc props :data-slot "select-value")])

(defn select-trigger
  [{:keys [class size]
    :or {size "default"}
    :as props}
   &
   children]
  (into [:>
         RadixSelect/Trigger
         (-> props
             (assoc :data-slot "select-trigger"
                    :data-size size
                    :class (merge-classes ["border-input data-[placeholder]:text-muted-foreground"
                                           "[&_svg:not([class*='text-'])]:text-muted-foreground"
                                           "focus-visible:border-ring focus-visible:ring-ring/50"
                                           "aria-invalid:ring-destructive/20"
                                           "dark:aria-invalid:ring-destructive/40"
                                           "aria-invalid:border-destructive dark:bg-input/30"
                                           "dark:hover:bg-input/50"
                                           "flex w-fit items-center justify-between gap-2"
                                           "rounded-md border bg-transparent px-3 py-2 text-sm"
                                           "whitespace-nowrap shadow-xs"
                                           "transition-[color,box-shadow] outline-none"
                                           "focus-visible:ring-[3px]"
                                           "disabled:cursor-not-allowed disabled:opacity-50"
                                           "data-[size=default]:h-9 data-[size=sm]:h-8"
                                           "*:data-[slot=select-value]:line-clamp-1"
                                           "*:data-[slot=select-value]:flex"
                                           "*:data-[slot=select-value]:items-center"
                                           "*:data-[slot=select-value]:gap-2"
                                           "[&_svg]:pointer-events-none [&_svg]:shrink-0"
                                           "[&_svg:not([class*='size-'])]:size-4"]
                                          class))
             (dissoc :class-name :size))]
        (concat children
                [[:> RadixSelect/Icon {:asChild true} (r/as-element [chevron-down-icon])]])))

(defn- select-scroll-up-button
  [{:keys [class]}]
  [:>
   RadixSelect/ScrollUpButton
   {:data-slot "select-scroll-up-button"
    :class (merge-classes "flex cursor-default items-center justify-center py-1" class)}
   (r/as-element [chevron-up-icon])])

(defn- select-scroll-down-button
  [{:keys [class]}]
  [:>
   RadixSelect/ScrollDownButton
   {:data-slot "select-scroll-down-button"
    :class (merge-classes "flex cursor-default items-center justify-center py-1" class)}
   (r/as-element [chevron-down-icon])])

(defn select-content
  [{:keys [class position align]
    :or {position "popper"
         align "center"}}
   &
   children]
  [:>
   RadixSelect/Portal
   (into
    [:>
     RadixSelect/Content
     {:data-slot "select-content"
      :position position
      :align align
      :class (merge-classes "bg-popover text-popover-foreground"
                            "data-[state=open]:animate-in"
                            "data-[state=closed]:animate-out"
                            "data-[state=closed]:fade-out-0"
                            "data-[state=open]:fade-in-0"
                            "data-[state=closed]:zoom-out-95"
                            "data-[state=open]:zoom-in-95"
                            "data-[side=bottom]:slide-in-from-top-2"
                            "data-[side=left]:slide-in-from-right-2"
                            "data-[side=right]:slide-in-from-left-2"
                            "data-[side=top]:slide-in-from-bottom-2"
                            "relative z-50"
                            "max-h-(--radix-select-content-available-height)"
                            "min-w-[8rem]"
                            "origin-(--radix-select-content-transform-origin)"
                            "overflow-x-hidden overflow-y-auto rounded-md border"
                            "shadow-md"
                            (when (= position "popper")
                              (merge-classes
                               "data-[side=bottom]:translate-y-1" "data-[side=left]:-translate-x-1"
                               "data-[side=right]:translate-x-1" "data-[side=top]:-translate-y-1"))
                            class)}]
    (concat [[select-scroll-up-button {}]
             (into [:>
                    RadixSelect/Viewport
                    {:class (merge-classes "p-1"
                                           (when (= position "popper")
                                             (merge-classes
                                              "h-[var(--radix-select-trigger-height)]" "w-full"
                                              "min-w-[var(--radix-select-trigger-width)]"
                                              "scroll-my-1")))}]
                   children)
             [select-scroll-down-button {}]]))])

(defn select-label
  [{:keys [class]} & children]
  (into [:>
         RadixSelect/Label
         {:data-slot "select-label"
          :class (merge-classes "text-muted-foreground px-2 py-1.5 text-xs" class)}]
        children))

(defn select-item
  [{:keys [class]
    :as props}
   &
   children]
  [:>
   RadixSelect/Item
   (-> props
       (assoc :data-slot "select-item"
              :class (merge-classes "focus:bg-accent focus:text-accent-foreground"
                                    "[&_svg:not([class*='text-'])]:text-muted-foreground"
                                    "relative flex w-full cursor-default items-center gap-2"
                                    "rounded-sm py-1.5 pr-8 pl-2 text-sm outline-hidden"
                                    "select-none" "data-[disabled]:pointer-events-none"
                                    "data-[disabled]:opacity-50"
                                    "[&_svg]:pointer-events-none [&_svg]:shrink-0"
                                    "[&_svg:not([class*='size-'])]:size-4"
                                    "*:[span]:last:flex *:[span]:last:items-center"
                                    "*:[span]:last:gap-2" class))
       (dissoc :class-name))
   [:span {:class "absolute right-2 flex size-3.5 items-center justify-center"}
    [:> RadixSelect/ItemIndicator (r/as-element [check-icon])]]
   (into [:> RadixSelect/ItemText] children)])

(defn select-separator
  [{:keys [class]}]
  [:>
   RadixSelect/Separator
   {:data-slot "select-separator"
    :class (merge-classes "bg-border pointer-events-none -mx-1 my-1 h-px" class)}])
