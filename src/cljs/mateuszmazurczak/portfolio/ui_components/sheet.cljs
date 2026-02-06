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
            :source-code (embed-source mateuszmazurczak.ui.components.sheet)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/sheet.cljs"
            :filename "sheet.cljs"}])

(defscene
 api-reference
 "Complete reference for all Sheet component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Sheet components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "sheet-overlay"
                                             :description "Sheet overlay component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "sheet-content"
                                             :description "Sheet content component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]
                                                     [":side" "keyword, optional (default :right). One of: :top | :bottom | :left | :right"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "sheet-header"
                                             :description "Sheet header component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "sheet-footer"
                                             :description "Sheet footer component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "sheet-title"
                                             :description "Sheet title component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "sheet-description"
                                             :description "Sheet description component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[sheet-overlay {}]"]]]]]]))

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