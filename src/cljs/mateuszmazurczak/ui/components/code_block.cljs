(ns mateuszmazurczak.ui.components.code-block
  "Code block component with syntax highlighting."
  (:require
   ["react-syntax-highlighter"                      :refer [Light]]
   ["react-syntax-highlighter/dist/esm/styles/hljs" :refer [paraisoDark paraisoLight]]
   [reagent.core                                     :refer [defc]]
   [reagent.hooks                                    :as hooks]))

(defn- copy-to-clipboard!
  "Copy text to clipboard."
  [text]
  (when-let [clipboard (.-clipboard js/navigator)] (.writeText clipboard text)))

(defn code-block
  "Display a code block with syntax highlighting.
   
   Props:
   - :text - The code text to display (required)
   - :language - Programming language for syntax highlighting (default: 'clojure')
   - :show-line-numbers? - Whether to show line numbers (default: true)
   - :theme - Theme variant :dark or :light (default: :dark)
   - :wrap-lines - Whether to wrap long lines (default: true)"
  [{:keys [text language show-line-numbers? theme wrap-lines]
    :or {language "clojure"
         show-line-numbers? true
         wrap-lines true
         theme :dark}}]
  (let [theme-obj (case theme
                    :light paraisoLight
                    :dark paraisoDark
                    paraisoDark)]
    [:>
     Light
     {:language language
      :style theme-obj
      :showLineNumbers show-line-numbers?
      :wrapLines wrap-lines
      :customStyle {:margin 0
                    :border-radius "0.375rem"
                    :font-size "0.875rem"}}
     text]))

(defc copy-block
 "Display a code block with syntax highlighting and copy button.
   
   Props:
   - :text - The code text to display (required)
   - :language - Programming language for syntax highlighting (default: 'clojure')
   - :show-line-numbers? - Whether to show line numbers (default: true)
   - :theme - Theme variant :dark or :light (default: :dark)
   - :wrap-lines - Whether to wrap long lines (default: true)"
 [{:keys [text language show-line-numbers? theme wrap-lines]
   :or {language "clojure"
        show-line-numbers? true
        wrap-lines true
        theme :dark}}]
 (let [[copied? set-copied!] (hooks/use-state false)
       theme-obj (case theme
                   :light paraisoLight
                   :dark paraisoDark
                   paraisoDark)
       handle-copy (fn []
                     (copy-to-clipboard! text)
                     (set-copied! true)
                     (js/setTimeout #(set-copied! false) 2000))]
   [:div.relative.group
    [:button.absolute.top-2.right-2.px-3.py-1.text-xs.rounded.transition-opacity.opacity-0.group-hover:opacity-100.z-10
     {:class (if (= theme :dark)
               "bg-gray-700 text-gray-200 hover:bg-gray-600"
               "bg-gray-200 text-gray-800 hover:bg-gray-300")
      :on-click handle-copy}
     (if copied? "Copied!" "Copy")]
    [:>
     Light
     {:language language
      :style theme-obj
      :showLineNumbers show-line-numbers?
      :wrapLines wrap-lines
      :customStyle {:margin 0
                    :border-radius "0.375rem"
                    :font-size "0.875rem"}}
     text]]))
