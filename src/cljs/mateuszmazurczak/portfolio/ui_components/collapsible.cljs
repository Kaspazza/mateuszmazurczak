(ns mateuszmazurczak.portfolio.ui-components.collapsible
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button      :as button]
   [mateuszmazurczak.ui.components.collapsible :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]
   [reagent.core                               :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Collapsible"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Collapsible component for showing and hiding content with animation."
            :npm-install "npm install @radix-ui/react-collapsible"
            :source-code (embed-source "mateuszmazurczak.ui.components.collapsible")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/collapsible.cljs"
            :filename "collapsible.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "collapsible"
               :link {:href "https://www.radix-ui.com/primitives/docs/components/collapsible" :label "Radix Collapsible Docs"}
               :description "Radix Collapsible.Root wrapper that controls open/closed state for collapsible sections. Additional props are forwarded to the Radix root primitive."
               :props [{:name ":open"          :type "boolean"     :default nil :description "Controlled open state."}
                       {:name ":default-open"  :type "boolean"     :default nil :description "Uncontrolled initial open state."}
                       {:name ":on-open-change" :type "function"   :default nil :description "Callback when open state changes: (fn [open?] ...)."}
                       {:name ":disabled"       :type "boolean"    :default nil :description "Disables toggling."}
                       {:name ":class"          :type "string"     :default nil :description "Additional Tailwind classes."}
                       {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Collapsible.Root."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "collapsible-trigger"
                :description "Toggle control for a collapsible block. Typically wraps a button label or a custom button when using :as-child."
                :props [{:name ":as-child"      :type "boolean"     :default nil :description "Use child element as trigger via Radix Slot."}
                        {:name ":on-click"      :type "function"    :default nil :description "Additional click handler composed with Radix toggle behavior."}
                        {:name ":class"         :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix CollapsibleTrigger."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "collapsible-content"
                :description "Expandable/collapsible content container with Radix state attributes for animation styling."
                :props [{:name ":force-mount"   :type "boolean"     :default nil :description "Forces mounting even when collapsed (useful for animation libraries)."}
                        {:name ":class"         :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix CollapsibleContent."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Use either controlled (:open + :on-open-change) or uncontrolled (:default-open) mode, not both."]
                [:li "When using :as-child on trigger, ensure your child element is interactive and keyboard-accessible."]
                [:li "Style animations using data-state attributes: data-[state=open] and data-[state=closed]."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [open? (r/atom false)]\n  [collapsible {:open @open?\n                :on-open-change #(reset! open? %)}\n   [collapsible-trigger {} [button {:variant :outline} \"Toggle details\"]]\n   [collapsible-content {:class \"mt-3\"}\n    [:div {:class \"rounded-md border p-3\"} \"Collapsible content\"]]])"]]]]]]))

(defscene
 collapsible-basic
 "Collapsible content with toggle.

  Radix primitive: @radix-ui/react-collapsible

  Use to reveal secondary details without leaving the page."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/collapsible {:open @open?
                         :on-open-change #(reset! open? %)}
        [sut/collapsible-trigger {}
         (button/button {:variant :outline} (if @open? "Hide details" "Show details"))]
        [sut/collapsible-content {:class "mt-4"}
         [:div {:class "rounded-md border bg-muted p-4 text-sm"}
          "This content expands and collapses."]]]]))))

(defscene
 collapsible-default-open
 "Collapsible starting in the open state.

  Radix primitive: @radix-ui/react-collapsible

  Use :default-open for uncontrolled open state."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/collapsible {:default-open true}
                                      [sut/collapsible-trigger {}
                                       (button/button {:variant :outline} "Toggle details")]
                                      [sut/collapsible-content {:class "mt-4"}
                                       [:div {:class "rounded-md border bg-muted p-4 text-sm"}
                                        "Starts expanded without external state management."]]]]))

(defscene
 collapsible-disabled
 "Disabled collapsible trigger.

  Radix primitive: @radix-ui/react-collapsible

  Use :disabled to lock the collapsible state."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/collapsible {:open true
                     :disabled true}
    [sut/collapsible-trigger {}
     (button/button {:variant :outline
                     :disabled true}
                    "Locked")]
    [sut/collapsible-content {:class "mt-4"}
     [:div {:class "rounded-md border bg-muted p-4 text-sm"}
      "Disabled collapsible remains open and cannot be toggled."]]]]))

(defscene
 collapsible-multiple
 "Multiple collapsibles in a list.

  Radix primitive: @radix-ui/react-collapsible

  Useful for FAQ sections or grouped settings."
 []
 (let [open-ids (r/atom #{:one})
       set-open! (fn [id next-open?]
                   (swap! open-ids (fn [current]
                                     (if next-open? (conj current id) (disj current id)))))]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 space-y-4"}
       (for [{:keys [id title body]} [{:id :one
                                       :title "Shipping"
                                       :body "Shipping details and estimated delivery windows."}
                                      {:id :two
                                       :title "Returns"
                                       :body "Return policy and refund timeline."}
                                      {:id :three
                                       :title "Support"
                                       :body "Contact information and support hours."}]]
         ^{:key id}
         [sut/collapsible {:open (contains? @open-ids id)
                           :on-open-change (fn [next-open?] (set-open! id next-open?))}
          [sut/collapsible-trigger {}
           (button/button {:variant :outline
                           :class "w-full justify-between"}
                          title)]
          [sut/collapsible-content {:class "mt-2"}
           [:div {:class "rounded-md border bg-muted p-4 text-sm"}
            body]]])]))))
