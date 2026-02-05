(ns mateuszmazurczak.portfolio.ui-components.markdown
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.markdown :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Markdown"})

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