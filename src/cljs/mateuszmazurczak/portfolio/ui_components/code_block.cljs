(ns mateuszmazurczak.portfolio.ui-components.code-block
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button     :as button]
   [mateuszmazurczak.ui.components.code-block :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Code Block"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Code block component with syntax highlighting using Shiki."
            :npm-install "npm install shiki"
            :source-code (embed-source mateuszmazurczak.ui.components.code_block)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/code_block.cljs"
            :filename "code_block.cljs"}])

(defscene
 api-reference
 "Complete reference for all Code Block component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Code Block components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "code-block"
                                             :description "Code block component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "highlight"
                                             :description "Highlight component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "hello"
                                             :description "Hello component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "code-block-group"
                                             :description "Code block group component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[code-block {}]"]]]]]]))

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