(ns mateuszmazurczak.portfolio.ui-components.code-block
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button     :as button]
   [mateuszmazurczak.ui.components.code-block :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Code Block"})

(defscene
 code-block-single
 "Single code block with syntax highlighting.

  Custom component — not from shadcn/ui.
  Uses Shiki for syntax highlighting.

  Use for inline documentation and examples."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/code-block {}
    [sut/code-block-code {:language "clojure"
                          :code "(defn greet [name]\n  (str \"Hello, \" name \"!\"))"}]]]))

(defscene
 code-block-with-header
 "Code block with filename header.

  Custom component — not from shadcn/ui.
  Use code-block-group to build headers or actions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/code-block {}
    [sut/code-block-group {:class "px-4 py-2 border-b text-xs text-muted-foreground"}
     [:span "handlers.cljs"]
     (button/button {:size :sm
                     :variant :ghost}
                    "Copy")]
    [sut/code-block-code {:language "clojure"
                          :code "(defn handle-request [req]\n  {:status 200 :body \"OK\"})"}]]]))

(defscene
 code-block-multiple-languages
 "Code blocks in multiple languages.

  Custom component — not from shadcn/ui.
  Shows how to switch language prop for highlighting."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 grid gap-4"}
   [sut/code-block {}
    [sut/code-block-code {:language "javascript"
                          :code "const greet = (name) => `Hello ${name}`"}]]
   [sut/code-block {}
    [sut/code-block-code {:language "python"
                          :code "def greet(name):\n    return f'Hello {name}'"}]]]))

(defscene
 code-block-copy-action
 "Code block with copy action row.

  Custom component — not from shadcn/ui.
  Demonstrates how to build simple copy UI."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-xl"}
   [sut/code-block {}
    [sut/code-block-group {:class "px-4 py-2 border-b flex items-center justify-between"}
     [:span {:class "text-xs text-muted-foreground"}
      "schema.edn"]
     (button/button {:size :sm
                     :variant :ghost}
                    "Copy")]
    [sut/code-block-code {:language "clojure"
                          :code "{:db/id :user/email\n :db/valueType :db.type/string}"}]]]))