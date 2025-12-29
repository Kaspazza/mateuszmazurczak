(ns mateuszmazurczak.ui.components.sidebar
  "Pure presentational sidebar components.
   
   Components are data-driven - no internal state management:
   - Consumer manages open/collapsed state
   - Consumer handles mobile detection
   - Consumer provides callbacks for interactions
   
   Usage:
   [sidebar {:open? @sidebar-open?
             :is-mobile @is-mobile
             :side \"left\"
             :variant \"sidebar\"
             :collapsible \"offcanvas\"}
     [sidebar-header {}
       [:button {:on-click #(reset! sidebar-open? (not @sidebar-open?))}
         \"Toggle\"]]
     [sidebar-content {}
       ;; menu items
       ]]"
  (:require
   ["@radix-ui/react-slot"                   :refer [Slot]]
   ["lucide-react"                           :refer [PanelLeft]]
   [mateuszmazurczak.ui.components.button    :as mateuszmazurczak-button]
   [mateuszmazurczak.ui.components.input     :as mateuszmazurczak-input]
   [mateuszmazurczak.ui.components.separator :as mateuszmazurczak-separator]
   [mateuszmazurczak.ui.components.sheet     :as mateuszmazurczak-sheet]
   [mateuszmazurczak.ui.components.skeleton  :as mateuszmazurczak-skeleton]
   [mateuszmazurczak.ui.components.tooltip   :as mateuszmazurczak-tooltip]
   [mateuszmazurczak.utils.styles            :refer [merge-classes]]))

;; -----------------------------------------------------------------------------
;; Constants
;; -----------------------------------------------------------------------------
(def ^:private SIDEBAR_WIDTH "16rem")
(def ^:private SIDEBAR_WIDTH_MOBILE "18rem")
(def ^:private SIDEBAR_WIDTH_ICON "3rem")

;; -----------------------------------------------------------------------------
;; Sidebar root
;; -----------------------------------------------------------------------------
(defn sidebar
  "Pure presentational sidebar component. Renders based on props, no internal state.

  Required Props:
  - :open? (boolean) - Whether sidebar is open/expanded
  - :is-mobile (boolean) - Whether to render mobile variant
  
  Optional Props:
  - :on-open-change (fn [boolean]) - Callback when mobile sheet requests state change
  - :side \"left\" | \"right\" (default: \"left\")
  - :variant \"sidebar\" | \"floating\" | \"inset\" (default: \"sidebar\")
  - :collapsible \"offcanvas\" | \"icon\" | \"none\" (default: \"offcanvas\")
  - :class - Additional CSS classes
  - :style - Additional inline styles (merged with CSS vars)"
  [{:keys [open? is-mobile on-open-change side variant collapsible class style]
    :or {side "left"
         variant "sidebar"
         collapsible "offcanvas"}}
   &
   children]
  (let [state (if open? "expanded" "collapsed")
        css-vars (merge {"--sidebar-width" SIDEBAR_WIDTH
                         "--sidebar-width-icon" SIDEBAR_WIDTH_ICON}
                        style)]
    (cond
      (= collapsible "none")
      (into [:div
             {:class
              (merge-classes
               "flex h-full w-[var(--sidebar-width)] flex-col bg-sidebar text-sidebar-foreground"
               class)
              :style css-vars}]
            children)
      is-mobile
      [:>
       mateuszmazurczak-sheet/sheet
       {:open open?
        :on-open-change (or on-open-change identity)}
       [mateuszmazurczak-sheet/sheet-content
        {:data-sidebar "sidebar"
         :data-mobile "true"
         :class (merge-classes
                 "w-[var(--sidebar-width)] bg-sidebar p-0 text-sidebar-foreground [&>button]:hidden"
                 class)
         :style {"--sidebar-width" SIDEBAR_WIDTH_MOBILE}
         :side (keyword side)}
        [mateuszmazurczak-sheet/sheet-header {:class "sr-only"}
         [mateuszmazurczak-sheet/sheet-title {}
          "Sidebar"]
         [mateuszmazurczak-sheet/sheet-description {}
          "Displays the mobile sidebar."]]
        (into [:div {:class "flex h-full w-full flex-col"}]
              children)]]
      ;; Desktop: fixed sidebar with collapsible behavior
      :else
      [:div {:class "group peer hidden text-sidebar-foreground md:block"
             :data-state state
             :data-collapsible (when (= state "collapsed") collapsible)
             :data-variant variant
             :data-side side
             :style css-vars}
       ;; Gap handler (spacer that adapts to sidebar width)
       [:div
        {:class
         (merge-classes
          "relative w-[var(--sidebar-width)] bg-transparent transition-[width] duration-200 ease-linear"
          "group-data-[collapsible=offcanvas]:w-0"
          "group-data-[side=right]:rotate-180"
          (if (or (= variant "floating") (= variant "inset"))
            "group-data-[collapsible=icon]:w-[calc(var(--sidebar-width-icon)_+_theme(spacing.4))]"
            "group-data-[collapsible=icon]:w-[var(--sidebar-width-icon)]"))}]
       ;; Fixed sidebar container
       [:div
        {:class
         (merge-classes
          "fixed inset-y-0 z-10 hidden h-svh w-[var(--sidebar-width)] transition-[left,right,width] duration-200 ease-linear md:flex"
          (if (= side "left")
            "left-0 group-data-[collapsible=offcanvas]:left-[calc(var(--sidebar-width)*-1)]"
            "right-0 group-data-[collapsible=offcanvas]:right-[calc(var(--sidebar-width)*-1)]")
          (if (or (= variant "floating") (= variant "inset"))
            "p-2 group-data-[collapsible=icon]:w-[calc(var(--sidebar-width-icon)_+_theme(spacing.4)_+_2px)]"
            "group-data-[collapsible=icon]:w-[var(--sidebar-width-icon)] group-data-[side=left]:border-r group-data-[side=right]:border-l")
          class)}
        (into
         [:div
          {:data-sidebar "sidebar"
           :class
           "flex h-full w-full flex-col bg-sidebar group-data-[variant=floating]:rounded-lg group-data-[variant=floating]:border group-data-[variant=floating]:border-sidebar-border group-data-[variant=floating]:shadow"}]
         children)]])))

;; -----------------------------------------------------------------------------
;; Trigger & Rail - Simple button components
;; -----------------------------------------------------------------------------
(defn sidebar-trigger
  "Simple toggle button for sidebar. Consumer provides :on-click handler.
  
  Props:
  - :on-click (fn [event]) - Click handler (e.g., toggle sidebar state)
  - :class - Additional CSS classes
  - Other button props"
  [{:keys [class on-click]
    :as props}]
  [mateuszmazurczak-button/button
   (-> props
       (dissoc :class)
       (assoc :data-sidebar "trigger"
              :variant :ghost
              :size :icon
              :class (merge-classes "h-7 w-7" class)
              :on-click on-click))
   [:> PanelLeft]
   [:span {:class "sr-only"}
    "Toggle Sidebar"]])

(defn sidebar-rail
  "Sidebar rail button (edge hover area). Consumer provides :on-click handler.
  
  Props:
  - :on-click (fn [event]) - Click handler (e.g., toggle sidebar state)
  - :class - Additional CSS classes"
  [{:keys [class on-click]
    :as props}]
  [:button
   (->
     props
     (dissoc :class)
     (assoc
      :data-sidebar "rail"
      :aria-label "Toggle Sidebar"
      :tab-index -1
      :on-click on-click
      :title "Toggle Sidebar"
      :class
      (merge-classes
       (str
        "absolute inset-y-0 z-20 hidden w-4 -translate-x-1/2 transition-all ease-linear "
        "after:absolute after:inset-y-0 after:left-1/2 after:w-[2px] hover:after:bg-sidebar-border "
        "group-data-[side=left]:-right-4 group-data-[side=right]:left-0 sm:flex ")
       "[[data-side=left]_&]:cursor-w-resize [[data-side=right]_&]:cursor-e-resize"
       "[[data-side=left][data-state=collapsed]_&]:cursor-e-resize [[data-side=right][data-state=collapsed]_&]:cursor-w-resize"
       (str
        "group-data-[collapsible=offcanvas]:translate-x-0 group-data-[collapsible=offcanvas]:after:left-full "
        "group-data-[collapsible=offcanvas]:hover:bg-sidebar ")
       "[[data-side=left][data-collapsible=offcanvas]_&]:-right-2"
       "[[data-side=right][data-collapsible=offcanvas]_&]:-left-2"
       class)))])

;; -----------------------------------------------------------------------------
;; Inset & simple sections
;; -----------------------------------------------------------------------------
(defn sidebar-inset
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:main
    (->
      props
      (dissoc :class)
      (assoc
       :class
       (merge-classes
        (str
         "relative flex h-svh w-full flex-1 flex-col overmateuszmazurczak-hidden bg-background "
         "md:peer-data-[variant=inset]:m-2 md:peer-data-[state=collapsed]:peer-data-[variant=inset]:ml-2 "
         "md:peer-data-[variant=inset]:ml-0 md:peer-data-[variant=inset]:rounded-xl md:peer-data-[variant=inset]:shadow")
        class)))]
   children))

(defn sidebar-input
  [{:keys [class]
    :as props}]
  [mateuszmazurczak-input/input
   (-> props
       (dissoc :class)
       (assoc
        :data-sidebar "input"
        :class
        (merge-classes
         "h-8 w-full bg-background shadow-none focus-visible:ring-2 focus-visible:ring-sidebar-ring"
         class)))])

(defn sidebar-header
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "header" :class (merge-classes "flex flex-col gap-2 p-2" class)))]
        children))

(defn sidebar-footer
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "footer" :class (merge-classes "flex flex-col gap-2 p-2" class)))]
        children))

(defn sidebar-separator
  [{:keys [class]
    :as props}]
  [mateuszmazurczak-separator/separator
   (-> props
       (dissoc :class)
       (assoc :data-sidebar "separator"
              :class (merge-classes "mx-2 w-auto bg-sidebar-border" class)))])

(defn sidebar-content
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:div
    (->
      props
      (dissoc :class)
      (assoc
       :data-sidebar "content"
       :class
       (merge-classes
        "flex min-h-0 flex-1 flex-col gap-2 overmateuszmazurczak-auto group-data-[collapsible=icon]:overmateuszmazurczak-hidden"
        class)))]
   children))

(defn sidebar-group
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "group"
                    :class (merge-classes "relative flex w-full min-w-0 flex-col p-2" class)))]
        children))

(defn sidebar-group-label
  [{:keys [class as-child]
    :as props}
   &
   children]
  (let [Comp (if as-child Slot "div")]
    (into [:>
           Comp
           (-> props
               (dissoc :class :as-child)
               (assoc :data-sidebar "group-label"
                      :class
                      (merge-classes
                       (str
                        "flex h-8 shrink-0 items-center rounded-md px-2 text-xs font-medium "
                        "text-sidebar-foreground/70 outline-none ring-sidebar-ring "
                        "transition-[margin,opacity] duration-200 ease-linear focus-visible:ring-2 "
                        "[&>svg]:size-4 [&>svg]:shrink-0 ")
                       "group-data-[collapsible=icon]:-mt-8 group-data-[collapsible=icon]:opacity-0"
                       class)))]
          children)))

(defn sidebar-group-action
  [{:keys [class as-child]
    :as props}
   &
   children]
  (let [Comp (if as-child Slot "button")]
    (into [:>
           Comp
           (->
             props
             (dissoc :class :as-child)
             (assoc
              :data-sidebar "group-action"
              :class
              (merge-classes
               (str
                "absolute right-3 top-3.5 flex aspect-square w-5 items-center justify-center "
                "rounded-md p-0 text-sidebar-foreground outline-none ring-sidebar-ring "
                "transition-transform hover:bg-sidebar-accent hover:text-sidebar-accent-foreground "
                "focus-visible:ring-2 [&>svg]:size-4 [&>svg]:shrink-0 ")
               "after:absolute after:-inset-2 after:md:hidden"
               "group-data-[collapsible=icon]:hidden"
               class)))]
          children)))

(defn sidebar-group-content
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "group-content" :class (merge-classes "w-full text-sm" class)))]
        children))

;; -----------------------------------------------------------------------------
;; Menu
;; -----------------------------------------------------------------------------
(defn sidebar-menu
  [{:keys [class]
    :as props}
   &
   children]
  (into [:ul
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "menu"
                    :class (merge-classes "flex w-full min-w-0 flex-col gap-1" class)))]
        children))

(defn sidebar-menu-item
  [{:keys [class]
    :as props}
   &
   children]
  (into [:li
         (-> props
             (dissoc :class)
             (assoc :data-sidebar "menu-item"
                    :class (merge-classes "group/menu-item relative" class)))]
        children))

(defn- menu-button-classes
  [{:keys [variant size]}]
  (let
    [base
     "peer/menu-button flex w-full items-center gap-2 overmateuszmazurczak-hidden rounded-md p-2 text-left text-sm outline-none ring-sidebar-ring transition-[width,height,padding] hover:bg-sidebar-accent hover:text-sidebar-accent-foreground focus-visible:ring-2 active:bg-sidebar-accent active:text-sidebar-accent-foreground disabled:pointer-events-none disabled:opacity-50 group-has-[[data-sidebar=menu-action]]/menu-item:pr-8 aria-disabled:pointer-events-none aria-disabled:opacity-50 data-[active=true]:bg-sidebar-accent data-[active=true]:font-medium data-[active=true]:text-sidebar-accent-foreground data-[state=open]:hover:bg-sidebar-accent data-[state=open]:hover:text-sidebar-accent-foreground group-data-[collapsible=icon]:!size-8 group-data-[collapsible=icon]:!p-2 [&>span:last-child]:truncate [&>svg]:size-4 [&>svg]:shrink-0"
     v
     (case variant
       :outline
       "bg-background shadow-[0_0_0_1px_hsl(var(--sidebar-border))] hover:bg-sidebar-accent hover:text-sidebar-accent-foreground hover:shadow-[0_0_0_1px_hsl(var(--sidebar-accent))]"
       :default "hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
       (str))
     s (case size
         :sm "h-7 text-xs"
         :lg "h-12 text-sm group-data-[collapsible=icon]:!p-0"
         :default "h-8 text-sm")]
    (merge-classes base v s)))

(defn sidebar-menu-button
  "Menu button component. Optionally shows tooltip when collapsed.
  
  Props:
  - :as-child (boolean) - Use Slot component for composition
  - :is-active? (boolean) - Active state styling
  - :tooltip (string | hiccup) - Tooltip text or content (shown when collapsed on desktop)
  - :variant (:default | :outline) - Button variant
  - :size (:default | :sm | :lg) - Button size
  - :collapsed? (boolean) - Whether sidebar is collapsed (for tooltip visibility)
  - :is-mobile (boolean) - Whether on mobile (hides tooltip)
  - :class - Additional CSS classes"
  [{:keys [class as-child is-active? tooltip variant size collapsed? is-mobile]
    :or {variant :default
         size :default}
    :as props}
   &
   children]
  (let [Comp (if as-child Slot "button")
        button-el
        (into
         [:>
          Comp
          (-> props
              (dissoc :class :as-child :is-active? :tooltip :variant :size :collapsed? :is-mobile)
              (assoc :data-sidebar "menu-button"
                     :data-size (name size)
                     :data-active (boolean is-active?)
                     :class (merge-classes (menu-button-classes {:variant variant
                                                                 :size size})
                                           class)))]
         children)]
    (if (or (not tooltip) (not collapsed?) is-mobile)
      button-el
      [mateuszmazurczak-tooltip/tooltip {:trigger button-el
                                         :content tooltip
                                         :side :right
                                         :align :center
                                         :trigger-as-child? true}])))

(defn sidebar-menu-action
  [{:keys [class as-child show-on-hover?]
    :as props}
   &
   children]
  (let [Comp (if as-child Slot "button")]
    (into
     [:>
      Comp
      (->
        props
        (dissoc :class :as-child :show-on-hover?)
        (assoc
         :data-sidebar "menu-action"
         :class
         (merge-classes
          (str
           "absolute right-1 top-1.5 flex aspect-square w-5 items-center justify-center rounded-md p-0 "
           "text-sidebar-foreground outline-none ring-sidebar-ring transition-transform "
           "hover:bg-sidebar-accent hover:text-sidebar-accent-foreground focus-visible:ring-2 "
           "peer-hover/menu-button:text-sidebar-accent-foreground [&>svg]:size-4 [&>svg]:shrink-0 ")
          "after:absolute after:-inset-2 after:md:hidden"
          "peer-data-[size=sm]/menu-button:top-1"
          "peer-data-[size=default]/menu-button:top-1.5"
          "peer-data-[size=lg]/menu-button:top-2.5"
          "group-data-[collapsible=icon]:hidden"
          (when show-on-hover?
            (str
             "group-focus-within/menu-item:opacity-100 group-hover/menu-item:opacity-100 data-[state=open]:opacity-100 "
             "peer-data-[active=true]/menu-button:text-sidebar-accent-foreground md:opacity-0"))
          class)))]
     children)))

(defn sidebar-menu-badge
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:div
    (->
      props
      (dissoc :class)
      (assoc
       :data-sidebar "menu-badge"
       :class
       (merge-classes
        (str
         "pointer-events-none absolute right-1 flex h-5 min-w-5 select-none items-center justify-center "
         "rounded-md px-1 text-xs font-medium tabular-nums text-sidebar-foreground ")
        "peer-hover/menu-button:text-sidebar-accent-foreground peer-data-[active=true]/menu-button:text-sidebar-accent-foreground"
        "peer-data-[size=sm]/menu-button:top-1"
        "peer-data-[size=default]/menu-button:top-1.5" "peer-data-[size=lg]/menu-button:top-2.5"
        "group-data-[collapsible=icon]:hidden" class)))]
   children))

(defn sidebar-menu-skeleton
  "Loading skeleton for menu items.
  
  Props:
  - :show-icon? (boolean) - Show icon skeleton
  - :class - Additional CSS classes"
  [{:keys [class show-icon?]
    :as props}]
  (let [width (str (+ 50 (rand-int 40)) "%")]
    [:div
     (-> props
         (dissoc :class :show-icon?)
         (assoc :data-sidebar "menu-skeleton"
                :class (merge-classes "flex h-8 items-center gap-2 rounded-md px-2" class)))
     (when show-icon?
       [mateuszmazurczak-skeleton/skeleton {:class "size-4 rounded-md"
                                            :data-sidebar "menu-skeleton-icon"}])
     [mateuszmazurczak-skeleton/skeleton {:class "h-4 max-w-[--skeleton-width] flex-1"
                                          :data-sidebar "menu-skeleton-text"
                                          :style {"--skeleton-width" width}}]]))

(defn sidebar-menu-sub
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:ul
    (->
      props
      (dissoc :class)
      (assoc
       :data-sidebar "menu-sub"
       :class
       (merge-classes
        "mx-3.5 flex min-w-0 translate-x-px flex-col gap-1 border-l border-sidebar-border px-2.5 py-0.5"
        "group-data-[collapsible=icon]:hidden"
        class)))]
   children))

(defn sidebar-menu-sub-item
  [{:keys [class]
    :as props}
   &
   children]
  (into [:li
         (-> props
             (dissoc :class)
             (assoc :class class))]
        children))

(defn sidebar-menu-sub-button
  [{:keys [class as-child size is-active?]
    :or {size :md}
    :as props}
   &
   children]
  (let [Comp (if as-child Slot "a")
        size-classes (case size
                       :sm "text-xs"
                       :md "text-sm"
                       "text-sm")]
    (into
     [:>
      Comp
      (->
        props
        (dissoc :class :as-child :size :is-active?)
        (assoc
         :data-sidebar "menu-sub-button"
         :data-size (name size)
         :data-active (boolean is-active?)
         :class
         (merge-classes
          (str
           "flex h-7 min-w-0 -translate-x-px items-center gap-2 overmateuszmazurczak-hidden rounded-md px-2 "
           "text-sidebar-foreground outline-none ring-sidebar-ring hover:bg-sidebar-accent hover:text-sidebar-accent-foreground "
           "focus-visible:ring-2 active:bg-sidebar-accent active:text-sidebar-accent-foreground disabled:pointer-events-none disabled:opacity-50 "
           "aria-disabled:pointer-events-none aria-disabled:opacity-50 [&>span:last-child]:truncate [&>svg]:size-4 [&>svg]:shrink-0 [&>svg]:text-sidebar-accent-foreground ")
          "data-[active=true]:bg-sidebar-accent data-[active=true]:text-sidebar-accent-foreground"
          size-classes
          "group-data-[collapsible=icon]:hidden" class)))]
     children)))

