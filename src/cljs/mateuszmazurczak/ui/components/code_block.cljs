(ns mateuszmazurczak.ui.components.code-block
  "Code block component with syntax highlighting."
  (:require
   ["react-code-blocks" :refer [CodeBlock CopyBlock dracula paraisoDark paraisoLight]]))

(defn code-block
  "Display a code block with syntax highlighting.
   
   Props:
   - :text - The code text to display (required)
   - :language - Programming language for syntax highlighting (default: 'clojure')
   - :show-line-numbers? - Whether to show line numbers (default: true)
   - :theme - Theme variant :dark or :light (default: :dark)"
  [{:keys [text language show-line-numbers? theme wrap-lines]
    :or {language "clojure"
         show-line-numbers? true
         wrap-lines true
         theme :dark}}]
  (let [theme-obj (case theme
                    :light paraisoLight
                    :dark paraisoDark
                    ;; fallback to dark theme
                    dracula)]
    [:>
     CodeBlock
     {:text text
      :language language
      :showLineNumbers show-line-numbers?
      :theme theme-obj
      :wrapLines wrap-lines}]))

(defn copy-block
  "Display a code block with syntax highlighting.
   
   Props:
   - :text - The code text to display (required)
   - :language - Programming language for syntax highlighting (default: 'clojure')
   - :show-line-numbers? - Whether to show line numbers (default: true)
   - :theme - Theme variant :dark or :light (default: :dark)"
  [{:keys [text language show-line-numbers? theme wrap-lines]
    :or {language "clojure"
         show-line-numbers? true
         wrap-lines true
         theme :dark}}]
  (let [theme-obj (case theme
                    :light paraisoLight
                    :dark paraisoDark
                    ;; fallback to dark theme
                    dracula)]
    [:>
     CopyBlock
     {:text text
      :language language
      :showLineNumbers show-line-numbers?
      :theme theme-obj
      :wrapLines wrap-lines}]))
