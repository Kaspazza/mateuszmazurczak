(ns mateuszmazurczak.portfolio.ui-components.sheet
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.input  :as input]
   [mateuszmazurczak.ui.components.label  :as label]
   [mateuszmazurczak.ui.components.sheet  :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Sheet"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Sheet (drawer/slide-out) component for modal content that slides in from edges."
            :npm-install "npm install @radix-ui/react-dialog lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.sheet")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/sheet.cljs"
            :filename "sheet.cljs"}])

(defscene api-reference
          "Complete reference for all Sheet component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Sheet components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet"
                :description "Radix Dialog.Root alias used as sheet root controller."
                :props [[":open" "boolean, optional - Controlled open state."]
                        [":default-open" "boolean, optional - Uncontrolled initial open state."]
                        [":on-open-change" "function, optional - Callback when state changes: (fn [open?] ...)."]
                        [":modal" "boolean, optional (default true) - Whether sheet is modal."]
                        ["additional props" "map entries, optional - Forwarded to Radix Dialog.Root."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-trigger"
                :description "Control that opens the sheet."
                :props [[":as-child" "boolean, optional - Compose with a child element."]
                        ["additional props" "map entries, optional - Forwarded to Radix Dialog.Trigger."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-close"
                :description "Control that closes the sheet."
                :props [[":as-child" "boolean, optional - Compose with a child element."]
                        ["additional props" "map entries, optional - Forwarded to Radix Dialog.Close."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-overlay"
                :description "Backdrop overlay rendered behind the sheet."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Dialog overlay."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-content"
                :description "Sliding panel container with built-in overlay and close button."
                :props [[":side" "keyword, optional (default :right). One of: :top | :bottom | :left | :right."]
                        [":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Dialog content."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-header"
                :description "Layout wrapper for sheet-title and sheet-description."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to underlying div."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-footer"
                :description "Responsive action area for sheet controls."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to underlying div."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-title"
                :description "Accessible title announced by screen readers."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Dialog.Title."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "sheet-description"
                :description "Accessible supporting text for sheet content."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Dialog.Description."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "sheet, sheet-trigger, and sheet-close are raw React aliases, so render them with :> in Reagent."]
                [:li "Use :side on sheet-content to control animation direction and panel placement."]
                [:li "For forms, keep explicit close controls (sheet-close) in footer for predictable UX."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:> sheet {}\n [:> sheet-trigger {:as-child true}\n  [button {:variant :outline} \"Open\"]]\n [sheet-content {:side :right}\n  [sheet-header {}\n   [sheet-title {} \"Edit profile\"]\n   [sheet-description {} \"Update details and save.\"]]\n  [sheet-footer {}\n   [:> sheet-close {:as-child true} [button {:variant :outline} \"Close\"]]]]]"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                [:a {:href "https://ui.shadcn.com/docs/components/sheet"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "shadcn Sheet Docs →"]
                [:a {:href "https://www.radix-ui.com/primitives/docs/components/dialog"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "Radix Dialog Docs →"]]]]]]))

(defscene
 sheet-demo
 "Basic sheet with profile form.

  Based on shadcn/ui Sheet — https://ui.shadcn.com/docs/components/sheet
  Radix primitive: @radix-ui/react-dialog

  Note: `sheet`, `sheet-trigger`, and `sheet-close` are raw React defs and use `:>`.
  Wrapper components like `sheet-content` are reagent fns and use `[sut/...`.

  Sheets slide in from an edge to reveal secondary content."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [:>
    sut/sheet
    {}
    [:> sut/sheet-trigger {:as-child true} (button/button {:variant :outline} "Open")]
    [sut/sheet-content {}
     [sut/sheet-header {}
      [sut/sheet-title {}
       "Edit profile"]
      [sut/sheet-description {}
       "Make changes to your profile here. Click save when you're done."]]
     [:div {:class "grid flex-1 auto-rows-min gap-6 px-4"}
      [:div {:class "grid gap-3"}
       [label/label {:html-for "sheet-demo-name"}
        "Name"]
       [input/input {:id "sheet-demo-name"
                     :default-value "Pedro Duarte"}]]
      [:div {:class "grid gap-3"}
       [label/label {:html-for "sheet-demo-username"}
        "Username"]
       [input/input {:id "sheet-demo-username"
                     :default-value "@peduarte"}]]]
     [sut/sheet-footer {}
      (button/button {:type "submit"} "Save changes")
      [:> sut/sheet-close {:as-child true} (button/button {:variant :outline} "Close")]]]]]))

(defscene
 sheet-side
 "Sheets on all four sides.

  Based on shadcn/ui Sheet — https://ui.shadcn.com/docs/components/sheet
  Radix primitive: @radix-ui/react-dialog

  Use :side to control where the sheet appears."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 grid grid-cols-2 gap-2"}
   (for [side [:top :right :bottom :left]]
     ^{:key side}
     [:>
      sut/sheet
      {}
      [:> sut/sheet-trigger {:as-child true} (button/button {:variant :outline} (name side))]
      [sut/sheet-content {:side side}
       [sut/sheet-header {}
        [sut/sheet-title {}
         "Edit profile"]
        [sut/sheet-description {}
         "Make changes to your profile here. Click save when you're done."]]
       [:div {:class "grid gap-4 py-4"}
        [:div {:class "grid grid-cols-4 items-center gap-4"}
         [label/label {:html-for (str "sheet-name-" (name side))
                       :class "text-right"}
          "Name"]
         [input/input {:id (str "sheet-name-" (name side))
                       :default-value "Pedro Duarte"
                       :class "col-span-3"}]]
        [:div {:class "grid grid-cols-4 items-center gap-4"}
         [label/label {:html-for (str "sheet-username-" (name side))
                       :class "text-right"}
          "Username"]
         [input/input {:id (str "sheet-username-" (name side))
                       :default-value "@peduarte"
                       :class "col-span-3"}]]]
       [sut/sheet-footer {}
        [:> sut/sheet-close {:as-child true} (button/button {:type "submit"} "Save changes")]]]])]))

(defscene
 sheet-scrollable
 "Sheet with scrollable content.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-dialog

  Use overflow classes to handle long content in sheets."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [:>
    sut/sheet
    {}
    [:> sut/sheet-trigger {:as-child true} (button/button {:variant :outline} "Open Scrollable")]
    [sut/sheet-content {:class "overflow-y-auto"}
     [sut/sheet-header {}
      [sut/sheet-title {}
       "Release Notes"]
      [sut/sheet-description {}
       "Review recent changes before continuing."]]
     [:div {:class "space-y-3 px-4"}
      (for [idx (range 1 16)]
        ^{:key idx}
        [:p {:class "text-sm text-muted-foreground"}
         (str "Release item " idx ": Updated feature details and fixes.")])]
     [sut/sheet-footer {}
      [:> sut/sheet-close {:as-child true} (button/button {:variant :outline} "Close")]]]]]))