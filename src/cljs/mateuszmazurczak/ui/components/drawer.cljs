(ns mateuszmazurczak.ui.components.drawer
  "Drawer component based on Vaul drawer primitive.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["vaul"                        :refer [Drawer]]
   [mateuszmazurczak.utils.styles :as styles]))

(def drawer-root (.-Root Drawer))
(def drawer-trigger-primitive (.-Trigger Drawer))
(def drawer-portal-primitive (.-Portal Drawer))
(def drawer-close-primitive (.-Close Drawer))
(def drawer-overlay-primitive (.-Overlay Drawer))
(def drawer-content-primitive (.-Content Drawer))
(def drawer-title-primitive (.-Title Drawer))
(def drawer-description-primitive (.-Description Drawer))

(defn drawer
  [{:keys [open on-open-change direction should-scale-background modal]
    :or {direction :bottom
         modal true}}
   &
   children]
  (into [:>
         drawer-root
         (cond-> {:data-slot "drawer"}
           open (assoc :open open)
           on-open-change (assoc :onOpenChange on-open-change)
           direction (assoc :direction (name direction))
           (some? should-scale-background) (assoc :shouldScaleBackground should-scale-background)
           (some? modal) (assoc :modal modal))]
        children))

(defn drawer-trigger
  [{:keys [class]} & children]
  (into [:>
         drawer-trigger-primitive
         (cond-> {:data-slot "drawer-trigger"}
           class (assoc :className class))]
        children))

(defn drawer-close
  [{:keys [class]} & children]
  (into [:>
         drawer-close-primitive
         (cond-> {:data-slot "drawer-close"}
           class (assoc :className class))]
        children))

(defn drawer-overlay
  [{:keys [class]}]
  [:>
   drawer-overlay-primitive
   {:data-slot "drawer-overlay"
    :className
    (styles/merge-classes
     "data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 fixed inset-0 z-50 bg-black/50"
     class)}])

(defn drawer-content
  [{:keys [class]} & children]
  [:>
   drawer-portal-primitive
   {}
   [:>
    drawer-overlay-primitive
    {:data-slot "drawer-overlay"
     :className
     (styles/merge-classes
      "data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 fixed inset-0 z-50 bg-black/50"
      nil)}]
   (into
    [:>
     drawer-content-primitive
     {:data-slot "drawer-content"
      :className
      (styles/merge-classes
       "group/drawer-content bg-background fixed z-50 flex h-auto flex-col"
       "data-[vaul-drawer-direction=bottom]:inset-x-0 data-[vaul-drawer-direction=bottom]:bottom-0 data-[vaul-drawer-direction=bottom]:mt-24 data-[vaul-drawer-direction=bottom]:max-h-[80vh] data-[vaul-drawer-direction=bottom]:rounded-t-lg data-[vaul-drawer-direction=bottom]:border-t"
       "data-[vaul-drawer-direction=right]:inset-y-0 data-[vaul-drawer-direction=right]:right-0 data-[vaul-drawer-direction=right]:h-full data-[vaul-drawer-direction=right]:w-auto data-[vaul-drawer-direction=right]:rounded-l-lg data-[vaul-drawer-direction=right]:border-l"
       class)}
     ;; Handle bar
     [:div
      {:class
       "bg-muted mx-auto mt-4 hidden h-2 w-[100px] shrink-0 rounded-full group-data-[vaul-drawer-direction=bottom]/drawer-content:block"}]]
    children)])

(defn drawer-header
  [{:keys [class]} & children]
  (into [:div {:data-slot "drawer-header"
               :class (styles/merge-classes "flex flex-col gap-0.5 p-4 md:gap-1.5" class)}]
        children))

(defn drawer-footer
  [{:keys [class]} & children]
  (into [:div {:data-slot "drawer-footer"
               :class (styles/merge-classes "mt-auto flex flex-col gap-2 p-4" class)}]
        children))

(defn drawer-title
  [{:keys [class]} & children]
  (into [:>
         drawer-title-primitive
         {:data-slot "drawer-title"
          :className (styles/merge-classes "text-foreground font-semibold" class)}]
        children))

(defn drawer-description
  [{:keys [class]} & children]
  (into [:>
         drawer-description-primitive
         {:data-slot "drawer-description"
          :className (styles/merge-classes "text-muted-foreground text-sm" class)}]
        children))
