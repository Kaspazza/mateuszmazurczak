(ns mateuszmazurczak.portfolio.ui-components.markdown
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.markdown :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Markdown"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Markdown component with syntax highlighting and rich formatting."
            :npm-install "npm install marked react-markdown remark-breaks remark-gfm"
            :source-code (embed-source "mateuszmazurczak.ui.components.markdown")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/markdown.cljs"
            :filename "markdown.cljs"}])

(defscene api-reference
          "Complete reference for all Markdown component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-4xl"}
                                              [:div {:class "space-y-6"}
                                               [:div
                                                [:p {:class "text-sm text-muted-foreground"}
                                                 "All available props for Markdown components."]]
                                               [:div {:class "space-y-4"}
                                                [mm-portfolio-utils/api-component-card
                                                 {:component-name "markdown"
                                                  :description "Markdown renderer with GFM support, automatic line breaks, and syntax-highlighted code blocks. Parses input into memoized blocks for better rendering performance."
                                                  :props [[":children" "string, required - Markdown content to render."]
                                                          [":id" "string, optional - Stable base ID used for generated block keys."]
                                                          [":class" "string, optional - Additional Tailwind classes for the wrapper container."]
                                                          [":components" "map, optional - Custom react-markdown component overrides. Defaults to built-in code/pre renderers."]]}]
                                                [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
                                                 [:h4 {:class "text-sm font-semibold mb-2"}
                                                  "⚠️ Important Notes"]
                                                 [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                                                  [:li "The props map is required; always pass at least {:children \"...\"}."]
                                                  [:li "Code fences use the shared code-block component and infer language from class names like language-clojure."]
                                                  [:li "For upstream markdown behavior and supported syntax, see react-markdown and remark-gfm docs."]]]
                                                [:div {:class "border rounded-lg p-4 bg-muted/50"}
                                                 [:h4 {:class "text-sm font-semibold mb-2"}
                                                  "Usage Example"]
                                                 [:pre {:class "text-xs overflow-x-auto"}
                                                  [:code "[markdown {:children \"# Release Notes\\n\\n- Added sync\\n- Fixed edge cases\\n\\n```clojure\\n(defn ready? [state]\\n  (= :ok (:status state)))\\n```\"\n           :class \"prose prose-sm max-w-none\"}]"]]
                                                [:div {:class "flex flex-wrap gap-2"}
                                                 [:a {:href "https://github.com/remarkjs/react-markdown"
                                                      :target "_blank"
                                                      :rel "noopener noreferrer"
                                                      :class "inline-flex items-center text-sm text-primary hover:underline"}
                                                  "react-markdown Docs →"]
                                                 [:a {:href "https://github.com/remarkjs/remark-gfm"
                                                      :target "_blank"
                                                      :rel "noopener noreferrer"
                                                      :class "inline-flex items-center text-sm text-primary hover:underline"}
                                                  "remark-gfm Docs →"]]]]]]))

(defscene
 markdown-headings
 "Markdown headings and emphasis.
  Uses react-markdown with remark plugins.

  Useful for rich text content blocks."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown {:children "# Heading 1\n\n## Heading 2\n\n**Bold** and _italic_ text."}]]))

(defscene
 markdown-code-blocks
 "Markdown with fenced code blocks.
  Code blocks are highlighted via Shiki."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown
    {:children
     "```clojure\n(defn hello []\n  (println \"Hello\"))\n```\n\n```javascript\nconsole.log('Hello')\n```"}]]))

(defscene
 markdown-links-lists
 "Markdown lists and links.
  Links receive underline styles via markdown component."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown
    {:children
     "- Item one\n- Item two\n- Item three\n\nVisit [our docs](https://ui.shadcn.com)."}]]))

(defscene
 markdown-combined
 "Full markdown example combining features.
  Demonstrates headings, lists, links, and code together."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown
    {:children
     "# Release Notes\n\n**Highlights**\n- Added realtime sync\n- Improved exports\n\nLearn more in the [changelog](https://example.com).\n\n```clojure\n(defn sync! [state]\n  (assoc state :status :ok))\n```"}]]))