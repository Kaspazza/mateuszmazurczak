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
            :source-code (embed-source "mateuszmazurczak.ui.components.code_block")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/code_block.cljs"
            :filename "code_block.cljs"}])

(defscene api-reference
          "Complete reference for all Code Block component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground mb-4"}
               "Code block components with syntax highlighting powered by Shiki. Compose code-block (container), code-block-group (header/actions), and code-block-code (highlighted content)."]
              [:div {:class "flex flex-wrap gap-2"}
               [:a {:href "https://shiki.style/themes"
                    :target "_blank"
                    :rel "noopener noreferrer"
                    :class "inline-flex items-center text-sm text-primary hover:underline"}
                "Shiki Themes →"]
               [:a {:href "https://shiki.style/languages"
                    :target "_blank"
                    :rel "noopener noreferrer"
                    :class "inline-flex items-center text-sm text-primary hover:underline"}
                "Shiki Languages →"]]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "code-block"
                :description "Root container for code blocks. Provides border, background, and rounded styling. The props map is optional — children can be passed directly without it. All additional props are forwarded to the underlying div element."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes merged with defaults (border, bg-card, rounded-xl, overflow-clip)."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "code-block-code"
                :description "The primary component — renders syntax-highlighted code using Shiki. Highlighting is async: a plain <pre><code> fallback is shown while Shiki loads, then replaced with highlighted HTML. If Shiki fails, the raw code string is displayed as fallback. All additional props are forwarded to the underlying div element."
                :props [{:name ":code"     :type "string" :default nil                :description "The code string to highlight. If nil or empty, renders an empty code block."}
                        {:name ":language" :type "string" :default "\"tsx\""          :description "Language for syntax highlighting. Must match a Shiki language identifier (e.g. \"clojure\", \"javascript\", \"python\", \"html\", \"css\")."}
                        {:name ":theme"    :type "string" :default "\"github-light\"" :description "Shiki theme name. Common values: \"github-light\", \"github-dark\", \"one-dark-pro\", \"dracula\", \"nord\". See Shiki Themes for the full list."}
                        {:name ":class"    :type "string" :default nil                :description "Additional Tailwind classes merged with defaults (overflow-x-auto, text-[13px], padding via [&>pre] selectors)."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "code-block-group"
                :description "Group container for header elements like filenames, language labels, or action buttons. Renders a flex row with items centered and spaced between. The props map is optional — children can be passed directly without it. All additional props are forwarded to the underlying div element."
                :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes merged with defaults (flex, items-center, justify-between). Typically add px-4 py-2 border-b for a header row."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "code-block and code-block-group accept children with or without a leading props map — both [code-block [child]] and [code-block {} [child]] work."]
                [:li "code-block-code requires the props map (it destructures :code, :language, :theme)."]
                [:li "Highlighting is async — there is a brief flash of unstyled code on first render while Shiki loads."]
                [:li "The component uses dangerouslySetInnerHTML internally for Shiki output. Avoid passing untrusted user input as :code if XSS is a concern."]
                [:li "Shiki language identifiers are case-sensitive and must match exactly (e.g. \"clojure\" not \"Clojure\")."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code
                 ";; Basic code block\n[code-block\n  [code-block-code {:code \"(+ 1 2)\" :language \"clojure\"}]]\n\n;; With filename header and copy button\n[code-block {}\n  [code-block-group {:class \"px-4 py-2 border-b text-xs text-muted-foreground\"}\n    [:span \"core.cljs\"]\n    [button {:size :sm :variant :ghost} \"Copy\"]]\n  [code-block-code {:code \"(defn hello [] ...)\" \n                    :language \"clojure\"\n                    :theme \"github-dark\"}]]"]]]]]]))

(defscene
 code-block-single
 "Single code block with syntax highlighting.
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