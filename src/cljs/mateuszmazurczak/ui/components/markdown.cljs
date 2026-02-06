(ns mateuszmazurczak.ui.components.markdown
  "Markdown component with syntax highlighting and rich formatting.
  Uses react-markdown with remark plugins for GFM and line breaks.

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["marked"                                  :refer [marked]]
   ["react-markdown"                          :default ReactMarkdown]
   ["remark-breaks"                           :default remarkBreaks]
   ["remark-gfm"                              :default remarkGfm]
   [mateuszmazurczak.ui.components.code-block :as code-block]
   [mateuszmazurczak.utils.styles             :refer [merge-classes]]
   [reagent.core                              :as    r
                                              :refer [defc]]
   [reagent.hooks                             :as rhooks]))

(defn- parse-markdown-into-blocks
  "Parses markdown string into blocks using marked lexer.
  Returns a vector of raw token strings."
  [markdown]
  (let [tokens (.lexer marked markdown)] (vec (map #(.-raw %) tokens))))

(defn- extract-language
  "Extracts language from className string (e.g., 'language-javascript' -> 'javascript').
  Returns 'plaintext' if no language is found."
  [class-name]
  (if-not class-name
    "plaintext"
    (if-let [match (re-find #"language-(\w+)" class-name)]
      (second match)
      "plaintext")))

(def ^:private initial-components
  "Default component overrides for react-markdown."
  {:code (fn [props]
           (let [class-name (.-className props)
                 children (.-children props)
                 node (.-node props)
                 position (when node (.-position node))
                 start-line (when position (.. position -start -line))
                 end-line (when position (.. position -end -line))
                 is-inline? (or (not start-line) (= start-line end-line))]
             (if is-inline?
               (r/as-element [:span {:class
                                     (merge-classes
                                      "bg-primary-foreground rounded-sm px-1 font-mono text-sm"
                                      class-name)}
                              children])
               (let [language (extract-language class-name)
                     code-content (if (string? children) children (str children))]
                 (r/as-element [code-block/code-block {:class class-name}
                                [code-block/code-block-code {:code code-content
                                                             :language language}]])))))
   :pre (fn [props] (let [children (.-children props)] (r/as-element [:<> children])))})

(defc memoized-markdown-block
 "Memoized markdown block component. Renders a single markdown block.
  Only re-renders when content changes."
 [{:keys [content components]
   :or {components initial-components}}]
 [:>
  ReactMarkdown
  {:remarkPlugins #js [remarkGfm remarkBreaks]
   :components (clj->js components)}
  content])

;; Custom display name for better debugging
(set! (.-displayName memoized-markdown-block) "MemoizedMarkdownBlock")

(defc markdown
 "Markdown component with syntax highlighting and rich formatting.
  
  Parses markdown into blocks and renders each block separately for better performance.
  Uses react-markdown with remark-gfm (GitHub Flavored Markdown) and remark-breaks.
  
  Props:
  - `:children` - Markdown content string to render (required)
  - `:id` - Optional ID for the container (auto-generated if not provided)
  - `:class` - Additional Tailwind classes for the container
  - `:components` - Optional custom component overrides for react-markdown
  
  Features:
  - GFM support (tables, strikethrough, task lists, etc.)
  - Automatic line breaks
  - Syntax highlighting for code blocks via Shiki
  - Inline code styling
  - Memoized block rendering for performance
  
  Example:
  [markdown {:children \"# Hello\\n\\nThis is **bold** text.\"}]
  
  Example with custom ID:
  [markdown {:id \"my-markdown\" 
             :children \"```clojure\\n(defn hello [] ...)\\n```\"}]"
 [{:keys [children id class components]
   :or {components initial-components}}]
 (let [generated-id (rhooks/use-id)
       block-id (or id generated-id)
       ;; Memoize blocks parsing
       blocks (rhooks/use-memo (fn [] (parse-markdown-into-blocks children)) [children])]
   [:div {:class class}
    (for [block blocks]
      ^{:key (str block-id "-" (hash block))}
      [memoized-markdown-block {:content block
                                :components components}])]))

;; Custom display name for better debugging
(set! (.-displayName markdown) "Markdown")
