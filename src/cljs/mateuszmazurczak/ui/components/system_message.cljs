(ns mateuszmazurczak.ui.components.system-message
  "System message component for displaying notifications, alerts, and status messages.
  Supports multiple variants (action, error, warning) with optional icons and CTAs."
  (:require
   ["lucide-react"            :refer [AlertCircle AlertTriangle Info]]
   [mateuszmazurczak.ui.components.button :as mateuszmazurczak-button]
   [mateuszmazurczak.utils.styles         :refer [merge-classes]]))

(defn- variant-classes
  "Returns Tailwind classes for the given variant and fill combination.
  
  Variants:
  - `:action` - General action/info messages (zinc colors)
  - `:error` - Error messages (red colors)
  - `:warning` - Warning messages (amber colors)
  
  Fill determines background style:
  - `true` - Filled background with transparent border
  - `false` - Transparent background with colored border"
  [variant fill]
  (case variant
    :error (if fill
             "text-red-700 dark:text-red-800 bg-red-100 dark:bg-red-900/20 border-transparent"
             "text-red-700 dark:text-red-800 border-red-600 dark:border-red-900")
    :warning
    (if fill
      "text-amber-700 dark:text-amber-700 bg-amber-100 dark:bg-amber-900/20 border-transparent"
      "text-amber-700 dark:text-amber-700 border-amber-600 dark:border-amber-900")
    :action (if fill
              "text-zinc-700 dark:text-zinc-300 bg-zinc-100 dark:bg-zinc-900 border-transparent"
              "text-zinc-700 dark:text-zinc-300 border-zinc-200 dark:border-zinc-800")
    ;; default fallback to action
    (if fill
      "text-zinc-700 dark:text-zinc-300 bg-zinc-100 dark:bg-zinc-900 border-transparent"
      "text-zinc-700 dark:text-zinc-300 border-zinc-200 dark:border-zinc-800")))

(defn- get-default-icon
  "Returns the default icon component for the given variant.
  Returns nil if icon should be hidden."
  [variant icon-hidden?]
  (when-not icon-hidden?
    (case variant
      :error AlertCircle
      :warning AlertTriangle
      :action Info
      ;; default fallback
      Info)))

(defn system-message
  "System message component for displaying notifications, alerts, and status messages.
  
  Props:
  - `:variant` - Message variant (default: `:action`)
    - `:action` - General action/info messages
    - `:error` - Error messages
    - `:warning` - Warning messages
  - `:fill` - Fill background (default: `false`)
    - `true` - Filled background with transparent border
    - `false` - Transparent background with colored border
  - `:icon` - Custom React icon component to display (optional)
  - `:icon-hidden?` - When true, hides the icon completely (default: `false`)
  - `:cta` - Call-to-action button configuration (optional)
    - Map with keys:
      - `:label` - Button text (required)
      - `:on-click` - Click handler function
      - `:variant` - Button variant (unused, always uses :default)
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Examples:
  ;; Simple action message
  [system-message {} \"This is an informational message.\"]
  
  ;; Error message with fill
  [system-message {:variant :error :fill true}
   \"An error occurred while processing your request.\"]
  
  ;; Warning message with custom icon
  [system-message {:variant :warning :icon [:> CustomIcon]}
   \"Please review your settings.\"]
  
  ;; Message with CTA button
  [system-message {:variant :action
                   :cta {:label \"Learn More\"
                         :on-click #(js/console.log \"clicked\")}}
   \"New features are available!\"]
  
  ;; Message without icon
  [system-message {:icon-hidden? true}
   \"Clean message without icon.\"]"
  [{:keys [variant fill icon icon-hidden? cta class]
    :or {variant :action
         fill false
         icon-hidden? false}
    :as props}
   &
   children]
  (let [default-icon (get-default-icon variant icon-hidden?)
        icon-to-show (or icon default-icon)
        show-icon? (some? icon-to-show)
        base-classes "flex flex-row items-center gap-3 rounded-[12px] border py-2 pr-2 pl-3"
        combined-classes (merge-classes base-classes (variant-classes variant fill) class)]
    [:div
     (-> props
         (assoc :class combined-classes)
         (dissoc :variant :fill :icon :icon-hidden? :cta :class-name))
     ;; Main content area
     [:div {:class "flex flex-1 flex-row items-center gap-3 leading-normal"}
      ;; Icon section
      (when show-icon?
        [:div {:class "flex h-[1lh] shrink-0 items-center justify-center self-start"}
         (if (vector? icon-to-show)
           ;; If icon is already hiccup (e.g., [:> CustomIcon])
           icon-to-show
           ;; Otherwise it's a React component
           [:> icon-to-show {:class "size-4"}])])
      ;; Text content
      [:div {:class (merge-classes "flex min-w-0 flex-1 items-center"
                                   (if show-icon? "gap-3" "gap-0"))}
       (into [:div {:class "text-sm"}]
             children)]]
     ;; CTA button
     (when cta
       [mateuszmazurczak-button/button {:variant :default
                            :size :sm
                            :on-click (:on-click cta)}
        (:label cta)])]))
