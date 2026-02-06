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
            :source-code (embed-source mateuszmazurczak.ui.components.markdown)
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
                                                 {:component-name "hello"
                                                  :description "Hello component"
                                                  :props []}]
                                                [:div {:class "border rounded-lg p-4 bg-muted/50"}
                                                 [:h4 {:class "text-sm font-semibold mb-2"}
                                                  "Usage Example"]
                                                 [:pre {:class "text-xs overflow-x-auto"}
                                                  [:code "[hello {}]"]]]]]]))

(defscene
 markdown-headings
 "Markdown headings and emphasis.

  Custom component — not from shadcn/ui.
  Uses react-markdown with remark plugins.

  Useful for rich text content blocks."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown {:children "# Heading 1\n\n## Heading 2\n\n**Bold** and _italic_ text."}]]))

(defscene
 markdown-code-blocks
 "Markdown with fenced code blocks.

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
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

  Custom component — not from shadcn/ui.
  Demonstrates headings, lists, links, and code together."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/markdown
    {:children
     "# Release Notes\n\n**Highlights**\n- Added realtime sync\n- Improved exports\n\nLearn more in the [changelog](https://example.com).\n\n```clojure\n(defn sync! [state]\n  (assoc state :status :ok))\n```"}]]))