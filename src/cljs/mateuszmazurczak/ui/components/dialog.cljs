(ns mateuszmazurczak.ui.components.dialog
  "Dialog (modal) component with overlay and content area.
  https://www.radix-ui.com/primitives/docs/components/dialog"
  (:require
   ["@radix-ui/react-dialog" :as RadixDialog]
   ["lucide-react"           :refer [XIcon]]
   [ui.utils.styles        :refer [merge-classes]]))

(defn dialog
  "Root dialog component. Controls open/closed state.
  
  Props:
  - `:open` - Controlled open state (boolean)
  - `:defaultOpen` - Default open state for uncontrolled usage
  - `:onOpenChange` - Callback when open state changes (fn [open?])
  - `:modal` - Whether to render as modal (default: true)
  
  Example:
  [dialog {:open @open? :onOpenChange #(reset! open? %)}
   [dialog-trigger {} [button {} \"Open\"]]
   [dialog-content {}
    [dialog-header {}
     [dialog-title {} \"Title\"]
     [dialog-description {} \"Description\"]]
    [:div \"Content\"]]]"
  [props & children]
  (into [:> RadixDialog/Root (assoc props :data-slot "dialog")] children))

(defn dialog-trigger
  "Trigger button that opens the dialog.
  
  Props:
  - `:asChild` - Compose with child component (default: true for buttons)
  
  Example:
  [dialog-trigger {}
   [button {} \"Open Dialog\"]]"
  [props & children]
  (into [:> RadixDialog/Trigger (assoc props :data-slot "dialog-trigger")] children))

(defn dialog-portal
  "Portal component that renders dialog in a portal.
  Usually used internally by dialog-content."
  [props & children]
  (into [:> RadixDialog/Portal (assoc props :data-slot "dialog-portal")] children))

(defn dialog-close
  "Close button component that closes the dialog.
  
  Example:
  [dialog-close {}
   [button {:variant :outline} \"Cancel\"]]"
  [props & children]
  (into [:> RadixDialog/Close (assoc props :data-slot "dialog-close")] children))

(defn dialog-overlay
  "Overlay backdrop that appears behind the dialog.
  Usually used internally by dialog-content.
  
  Props:
  - `:class` - Additional Tailwind classes"
  [{:keys [class]
    :as props}]
  [:>
   RadixDialog/Overlay
   (-> props
    (assoc
     :data-slot "dialog-overlay"
     :class (merge-classes ["data-[state=open]:animate-in data-[state=closed]:animate-out"
                            "data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0"
                            "fixed inset-0 z-50 bg-black/50"] class))
    (dissoc :class-name))])

(defn dialog-content
  "Dialog content container with overlay and optional close button.
  
  Props:
  - `:class` - Additional Tailwind classes
  - `:showCloseButton` - Show X close button in top-right (default: true)
  
  Features:
  - Centered modal with backdrop overlay
  - Animated entry/exit
  - Responsive width (full width on mobile, max-w-lg on desktop)
  - Focus trap and scroll lock when open
  - ESC to close, click outside to close
  
  Example:
  [dialog-content {}
   [dialog-header {}
    [dialog-title {} \"Confirm Action\"]
    [dialog-description {} \"Are you sure?\"]]
   [:div {:class \"space-y-4\"}
    \"Dialog content goes here\"]
   [dialog-footer {}
    [button {:variant :outline} \"Cancel\"]
    [button {} \"Confirm\"]]]"
  [{:keys [class showCloseButton]
    :or {showCloseButton true}
    :as props}
   &
   children]
  [dialog-portal {}
   (dialog-overlay {})
   (into [:>
          RadixDialog/Content
          (-> props
           (assoc :data-slot "dialog-content" :class
            (merge-classes
             ["bg-background data-[state=open]:animate-in data-[state=closed]:animate-out"
              "data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0"
              "data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95"
              "fixed top-[50%] left-[50%] z-50 grid w-full"
              "max-w-[calc(100%-2rem)] translate-x-[-50%] translate-y-[-50%]"
              "gap-4 rounded-lg border p-6 shadow-lg duration-200"
              "sm:max-w-lg"] class))
           (dissoc :class-name :showCloseButton))]
    (concat children
     (when showCloseButton
       [[:>
         RadixDialog/Close
         {:data-slot "dialog-close"
          :class
          (merge-classes "ring-offset-background focus:ring-ring"
           "data-[state=open]:bg-accent data-[state=open]:text-muted-foreground"
           "absolute top-4 right-4 rounded-xs opacity-70" "transition-opacity hover:opacity-100"
           "focus:ring-2 focus:ring-offset-2 focus:outline-hidden" "disabled:pointer-events-none"
           "[&_svg]:pointer-events-none [&_svg]:shrink-0" "[&_svg:not([class*='size-'])]:size-4")}
         [:> XIcon]
         [:span {:class "sr-only"}
          "Close"]]])))])

(defn dialog-header
  "Header section for dialog (title + description).
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Example:
  [dialog-header {}
   [dialog-title {} \"Delete Item\"]
   [dialog-description {} \"This action cannot be undone.\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
          (assoc :data-slot "dialog-header" :class
           (merge-classes "flex flex-col gap-2 text-center sm:text-left" class))
          (dissoc :class-name))] children))

(defn dialog-footer
  "Footer section for dialog (action buttons).
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Layout:
  - Mobile: Stacked buttons (reverse order)
  - Desktop: Row with buttons aligned to the right
  
  Example:
  [dialog-footer {}
   [button {:variant :outline :on-click #(close-dialog)} \"Cancel\"]
   [button {:on-click #(save-changes)} \"Save\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
          (assoc :data-slot "dialog-footer" :class
           (merge-classes "flex flex-col-reverse gap-2 sm:flex-row sm:justify-end" class))
          (dissoc :class-name))] children))

(defn dialog-title
  "Dialog title component (automatically labeled for accessibility).
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Note: Radix automatically uses this for aria-labelledby on the dialog.
  
  Example:
  [dialog-title {} \"Confirm Deletion\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         RadixDialog/Title
         (-> props
          (assoc :data-slot "dialog-title" :class (merge-classes
                                                   "text-lg leading-none font-semibold" class))
          (dissoc :class-name))] children))

(defn dialog-description
  "Dialog description component (automatically used for accessibility).
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Note: Radix automatically uses this for aria-describedby on the dialog.
  
  Example:
  [dialog-description {}
   \"This action is permanent and cannot be undone.\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         RadixDialog/Description
         (-> props
          (assoc :data-slot "dialog-description" :class (merge-classes
                                                         "text-muted-foreground text-sm" class))
          (dissoc :class-name))] children))
