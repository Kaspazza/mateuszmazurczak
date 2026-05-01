(ns mateuszmazurczak.portfolio.ui-components.popover
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.command :as command]
   [mateuszmazurczak.ui.components.input   :as input]
   [mateuszmazurczak.ui.components.label   :as label]
   [mateuszmazurczak.ui.components.popover :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Popover"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Popover component for displaying floating content relative to a trigger."
            :npm-install "npm install @radix-ui/react-popover"
            :source-code (embed-source "mateuszmazurczak.ui.components.popover")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/popover.cljs"
            :filename "popover.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "popover"
               :link {:href "https://www.radix-ui.com/primitives/docs/components/popover" :label "Radix Popover Docs"}
               :description "Radix Popover root that controls open/close state for floating content."
                :props [{:name ":open"           :type "boolean"      :default nil    :description "Controlled open state."}
                        {:name ":defaultOpen"    :type "boolean"      :default nil    :description "Uncontrolled initial open state."}
                        {:name ":onOpenChange"   :type "function"     :default nil    :description "Callback when open state changes: (fn [open?] ...)."}
                        {:name ":modal"          :type "boolean"      :default "false" :description "Whether to trap focus like a modal."}
                        {:name "additional props" :type "map entries" :default nil    :description "Forwarded to Radix Popover.Root."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "popover-trigger"
                :description "Interactive trigger element that opens/closes the popover."
                :props [{:name ":asChild"        :type "boolean"      :default nil :description "Compose with a child element instead of rendering default trigger."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Popover.Trigger."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "popover-anchor"
                :description "Optional positioning anchor. If omitted, trigger is used as anchor."
                :props [{:name ":asChild"        :type "boolean"      :default nil :description "Compose with child element."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Popover.Anchor."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "popover-content"
                :description "Portaled floating content container with animations and collision-aware positioning."
                :props [{:name ":class"           :type "string"         :default nil        :description "Additional Tailwind classes."}
                        {:name ":align"           :type "string"         :default "\"center\"" :description "One of: \"start\" | \"center\" | \"end\"."}
                        {:name ":side"            :type "string"         :default "\"bottom\"" :description "One of: \"top\" | \"right\" | \"bottom\" | \"left\"."}
                        {:name ":sideOffset"      :type "number"         :default "4"         :description "Distance from trigger/anchor."}
                        {:name ":alignOffset"     :type "number"         :default nil         :description "Offset along alignment axis."}
                        {:name ":collisionPadding" :type "number | map"  :default nil         :description "Viewport collision padding."}
                        {:name ":avoidCollisions" :type "boolean"        :default "true"      :description "Enables collision avoidance."}
                        {:name "additional props" :type "map entries"    :default nil         :description "Forwarded to Radix Popover.Content."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "This wrapper uses Radix/cmdk camelCase prop names (e.g. :onOpenChange, :asChild, :sideOffset)."]
                [:li "popover-content is always portaled, so z-index/stacking should be handled at app layout level."]
                [:li "Use popover for non-blocking contextual UI; use dialog/sheet for interruptive workflows."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [open? (r/atom false)]\n  [popover {:open @open?\n            :onOpenChange #(reset! open? %)}\n   [popover-trigger {:asChild true}\n    [button {:variant :outline} \"Open\"]]\n   [popover-content {:align \"start\" :sideOffset 8}\n    [:p {:class \"text-sm\"} \"Popover content\"]]])"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                ]]]]]))

(defscene
 popover-demo
 "Popover with form fields.

  Radix primitive: @radix-ui/react-popover

  Useful for lightweight edits or inline settings."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/popover {}
    [sut/popover-trigger {:as-child true}
     (button/button {:variant :outline} "Open popover")]
    [sut/popover-content {:class "w-80"}
     [:div {:class "grid gap-4"}
      [:div {:class "space-y-2"}
       [:h4 {:class "leading-none font-medium"}
        "Dimensions"]
       [:p {:class "text-muted-foreground text-sm"}
        "Set the dimensions for the layer."]]
      [:div {:class "grid gap-2"}
       [:div {:class "grid grid-cols-3 items-center gap-4"}
        [label/label {:html-for "width"}
         "Width"]
        [input/input {:id "width"
                      :default-value "100%"
                      :class "col-span-2 h-8"}]]
       [:div {:class "grid grid-cols-3 items-center gap-4"}
        [label/label {:html-for "max-width"}
         "Max. width"]
        [input/input {:id "max-width"
                      :default-value "300px"
                      :class "col-span-2 h-8"}]]
       [:div {:class "grid grid-cols-3 items-center gap-4"}
        [label/label {:html-for "height"}
         "Height"]
        [input/input {:id "height"
                      :default-value "25px"
                      :class "col-span-2 h-8"}]]
       [:div {:class "grid grid-cols-3 items-center gap-4"}
        [label/label {:html-for "max-height"}
         "Max. height"]
        [input/input {:id "max-height"
                      :default-value "none"
                      :class "col-span-2 h-8"}]]]]]]]))

(defscene
 combobox-popover
 "Combobox built with popover + command list.

  Radix primitives: @radix-ui/react-popover, @radix-ui/react-dialog

  Use popover + command for searchable selections."
 []
 (let [open? (r/atom false)
       selected (r/atom nil)
       statuses [{:value "backlog"
                  :label "Backlog"}
                 {:value "todo"
                  :label "Todo"}
                 {:value "in-progress"
                  :label "In Progress"}
                 {:value "done"
                  :label "Done"}
                 {:value "canceled"
                  :label "Canceled"}]]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 flex items-center gap-4"}
       [:p {:class "text-muted-foreground text-sm"}
        "Status"]
       [sut/popover {:open @open?
                     :on-open-change #(reset! open? %)}
        [sut/popover-trigger {:as-child true}
         (button/button {:variant :outline
                         :class "w-[160px] justify-start"}
                        (or (:label @selected) "+ Set status"))]
        [sut/popover-content {:class "p-0"
                              :side "right"
                              :align "start"}
         [command/command {}
          [command/command-input {:placeholder "Change status..."}]
          [command/command-list {}
           [command/command-empty {}
            "No results found."]
           [command/command-group {}
            (for [{:keys [value label]} statuses]
              ^{:key value}
              [command/command-item {:value value
                                     :on-select (fn [_]
                                                  (reset! selected {:value value
                                                                    :label label})
                                                  (reset! open? false))}
               label])]]]]]]))))

(defscene
 popover-text-only
 "Simple popover with text content.

  Radix primitive: @radix-ui/react-popover

  Good for quick hints or short explanations."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/popover {}
    [sut/popover-trigger {:as-child true}
     (button/button {:variant :outline} "Why this matters")]
    [sut/popover-content {:class "w-72"}
     [:p {:class "text-sm text-muted-foreground"}
      "Popover content can be lightweight explanatory text or callouts."]]]]))