(ns mateuszmazurczak.ui.components.message
  "Message component for chat interfaces.
  Provides structured message display with avatar, content, and actions."
  (:require
   [mateuszmazurczak.ui.components.avatar   :as mateuszmazurczak-avatar]
   [mateuszmazurczak.ui.components.markdown :as mateuszmazurczak-markdown]
   [mateuszmazurczak.ui.components.tooltip  :as mateuszmazurczak-tooltip]
   [mateuszmazurczak.utils.styles           :refer [merge-classes]]
   [reagent.core                :as r]))

(defn message
  "Root message component. Container for message elements.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Example:
  [message {}
    [message-avatar {:src \"/avatar.png\" :alt \"User\"}]
    [message-content {} \"Hello, world!\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :class (merge-classes "flex gap-3" class))
             (dissoc :class-name))]
        children))

(defn message-avatar
  "Message avatar component. Displays user avatar with optional fallback.
  
  Props:
  - `:src` - Avatar image source URL (required)
  - `:alt` - Alt text for accessibility (required)
  - `:fallback` - Fallback text (typically initials) when image fails to load
  - `:delay-ms` - Delay in milliseconds before showing fallback
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [message-avatar {:src \"https://github.com/user.png\" 
                   :alt \"John Doe\"
                   :fallback \"JD\"}]"
  [{:keys [src alt fallback delay-ms class]}]
  [mateuszmazurczak-avatar/avatar {:class (merge-classes "h-8 w-8 shrink-0" class)}
   [mateuszmazurczak-avatar/avatar-image {:src src
                              :alt alt}]
   (when fallback
     [mateuszmazurczak-avatar/avatar-fallback {:delayMs delay-ms}
      fallback])])

(defn message-content
  "Message content component. Displays message text with optional markdown rendering.
  
  Props:
  - `:markdown?` - When true, renders children as markdown (default: false)
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div or Markdown component
  
  Example (plain text):
  [message-content {} \"This is a plain text message.\"]
  
  Example (markdown):
  [message-content {:markdown? true}
   \"# Hello\\n\\nThis is **bold** text.\"]"
  [{:keys [markdown? class]
    :or {markdown? false}
    :as props}
   &
   children]
  (let [base-classes "rounded-lg text-foreground bg-secondary prose break-words whitespace-normal"
        combined-classes (merge-classes base-classes class)
        content (first children)]
    (if markdown?
      [mateuszmazurczak-markdown/markdown
       (-> props
           (assoc :class combined-classes :children content)
           (dissoc :markdown? :class-name))]
      (into [:div
             (-> props
                 (assoc :class combined-classes)
                 (dissoc :markdown? :class-name))]
            children))))

(defn message-actions
  "Message actions container. Groups action buttons/links for a message.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Example:
  [message-actions {}
    [message-action {:tooltip \"Copy\"}
      [button {:variant :ghost :size :icon} [:> copy-icon]]]
    [message-action {:tooltip \"Delete\"}
      [button {:variant :ghost :size :icon} [:> trash-icon]]]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:div
         (-> props
             (assoc :class (merge-classes "text-muted-foreground flex items-center gap-2" class))
             (dissoc :class-name))]
        children))

(defn message-action
  "Message action component. Individual action button with tooltip.
  
  Props:
  - `:tooltip` - Tooltip content (required)
  - `:side` - Tooltip side: :top, :bottom, :left, :right (default: :top)
  - `:class` - Additional Tailwind classes for the tooltip content
  - All other props are passed to the tooltip component
  
  Children: Action button or interactive element
  
  Example:
  [message-action {:tooltip \"Copy message\"}
    [button {:variant :ghost :size :icon}
      [:> copy-icon {:class \"h-4 w-4\"}]]]
  
  Example with custom side:
  [message-action {:tooltip \"Delete\" :side :bottom}
    [button {:variant :ghost :size :icon :on-click delete-fn}
      [:> trash-icon {:class \"h-4 w-4\"}]]]"
  [{:keys [tooltip side class]
    :or {side :top}
    :as props}
   &
   children]
  [mateuszmazurczak-tooltip/tooltip
   (-> props
       (assoc :trigger (r/as-element (first children))
              :content tooltip
              :side side
              :content-class class
              :trigger-as-child? true)
       (dissoc :tooltip :class :class-name))])
