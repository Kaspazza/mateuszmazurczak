(ns mateuszmazurczak.ui.components.code-block
  "Code block component with syntax highlighting using Shiki.
  Provides a styled container for displaying code with proper highlighting."
  (:require
   ["shiki"                       :refer [codeToHtml]]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]
   [reagent.core                  :as    r
                                  :refer [defc]]
   [reagent.hooks                 :as rhooks]))

(defn code-block
  "Root container for code blocks with styling.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Example:
  [code-block
    [code-block-code {:code \"const x = 42;\" :language \"javascript\"}]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let
    [base-classes
     "not-prose flex w-full flex-col overflow-clip border border-border bg-card text-card-foreground rounded-xl"
     combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :class combined-classes)
               (dissoc :class-name))]
          children)))


(defn highlight
  [code language theme set-highlighted-html]
  (let [code (if (or (nil? code) (empty? code))
               (js/Promise.resolve "<pre><code></code></pre>")
               (codeToHtml code
                           #js {:lang language
                                :theme theme}))]
    (-> code
        (.then set-highlighted-html)
        (.catch (fn [err] (set-highlighted-html (str "<pre><code>" code "</code></pre>")))))))

(defc code-block-code
 "Code block with syntax highlighting using Shiki.
  
  Props:
  - `:code` - The code string to highlight (required)
  - `:language` - Language for syntax highlighting (default: \"tsx\")
  - `:theme` - Shiki theme to use (default: \"github-light\")
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Example:
  [code-block-code {:code \"const x = 42;\" :language \"javascript\"}]
  
  Example with custom theme:
  [code-block-code {:code \"(defn hello [] ...)\" 
                    :language \"clojure\" 
                    :theme \"github-dark\"}]"
 [{:keys [code language theme class]
   :or {language "tsx"
        theme "github-light"}
   :as props}]
 (let [[highlighted-html set-highlighted-html] (rhooks/use-state nil)]
   (rhooks/use-effect (fn [] (highlight code language theme set-highlighted-html) js/undefined)
                      [code language theme])
   (let [base-classes "w-full overflow-x-auto text-[13px] [&>pre]:px-4 [&>pre]:py-4"
         combined-classes (merge-classes base-classes class)]
     (if highlighted-html
       [:div
        (-> props
            (assoc :class combined-classes
                   :dangerouslySetInnerHTML (r/unsafe-html highlighted-html))
            (dissoc :code :language :theme :class-name))]
       [:div
        (-> props
            (assoc :class combined-classes)
            (dissoc :code :language :theme :class-name))
        [:pre [:code code]]]))))

(defn code-block-group
  "Group container for code block elements (e.g., header with actions).
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  - All other props are passed to the underlying div element
  
  Example:
  [code-block
    [code-block-group
      [:span \"example.js\"]
      [button {:size :sm} \"Copy\"]]
    [code-block-code {:code \"...\" :language \"javascript\"}]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [base-classes "flex items-center justify-between"
        combined-classes (merge-classes base-classes class)]
    (into [:div
           (-> props
               (assoc :class combined-classes)
               (dissoc :class-name))]
          children)))
