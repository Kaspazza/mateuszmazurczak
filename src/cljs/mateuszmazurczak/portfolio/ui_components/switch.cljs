(ns mateuszmazurczak.portfolio.ui-components.switch
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field  :as field]
   [mateuszmazurczak.ui.components.label  :as label]
   [mateuszmazurczak.ui.components.switch :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Switch"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Switch component for toggle controls."
            :npm-install "npm install @radix-ui/react-switch"
            :source-code (embed-source "mateuszmazurczak.ui.components.switch")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/switch.cljs"
            :filename "switch.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "switch"
               :description "Radix-based boolean toggle control with accessible switch semantics. Additional props are forwarded to the underlying Radix Switch.Root."
               :link {:href "https://www.radix-ui.com/primitives/docs/components/switch" :label "Radix Switch Docs"}
               :props [{:name ":checked"           :type "boolean"      :default nil :description "Controlled checked state."}
                        {:name ":default-checked"   :type "boolean"      :default nil :description "Uncontrolled initial checked state."}
                        {:name ":on-checked-change" :type "function"     :default nil :description "Callback when state changes: (fn [checked?] ...)."}
                        {:name ":disabled"          :type "boolean"      :default nil :description "Disables interaction."}
                        {:name ":required"          :type "boolean"      :default nil :description "Marks field as required for forms."}
                        {:name ":name"              :type "string"       :default nil :description "Form field name."}
                        {:name ":value"             :type "string"       :default nil :description "Form field value."}
                        {:name ":class"             :type "string"       :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"   :type "map entries"  :default nil :description "Forwarded to Radix Switch.Root."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Use controlled mode (:checked + :on-checked-change) when external state drives related UI."]
                [:li "Switch includes built-in disabled styling; avoid layering custom pointer-events overrides unless needed."]
                [:li "Associate with a label using matching :id and :html-for for better accessibility."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [enabled? (r/atom false)]\n  [:div {:class \"flex items-center gap-2\"}\n   [switch {:id \"notifications\"\n            :checked @enabled?\n            :on-checked-change #(reset! enabled? %)\n            :name \"notifications\"\n            :value \"enabled\"}]\n   [label {:html-for \"notifications\"} \"Enable notifications\"]])"]]
          ]]]]]]])

(defscene
 switch-demo
 "Switch paired with a label.

  Radix primitive: @radix-ui/react-switch

  Useful for single boolean preferences."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex items-center gap-2"}
                                      [sut/switch {:id "airplane-mode"}]
                                      [label/label {:html-for "airplane-mode"}
                                       "Airplane Mode"]]]))

(defscene
 field-switch
 "Switch inside Field layout with description.

  Radix primitive: @radix-ui/react-switch

  Use for richer settings forms with copy."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [field/field {:orientation :horizontal}
                                      [field/field-content {}
                                       [field/field-label {:html-for "mfa"}
                                        "Multi-factor authentication"]
                                       [field/field-description {}
                                        "Enable MFA for additional account security."]]
                                      [sut/switch {:id "mfa"}]]]))

(defscene
 switch-disabled
 "Disabled switch state.

  Radix primitive: @radix-ui/react-switch

  Use disabled state when the preference is locked."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex items-center gap-2"}
                                      [sut/switch {:id "locked"
                                                   :disabled true
                                                   :checked true}]
                                      [:span {:class "text-sm text-muted-foreground"}
                                       "Locked setting"]]]))

(defscene
 switch-invalid
 "Invalid switch state for form validation.

  Radix primitive: @radix-ui/react-switch

  Use :aria-invalid true and helper text for validation feedback."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-2"}
                                     [:div {:class "flex items-center gap-2"}
                                      [sut/switch {:id "terms-invalid"
                                                   :aria-invalid true
                                                   :checked false}]
                                      [label/label {:html-for "terms-invalid"}
                                       "Accept terms"]]
                                     [:p {:class "text-destructive text-sm"}
                                      "You must accept terms to continue."]]))

(defscene
 switch-controlled
 "Controlled switch with live state.

  Radix primitive: @radix-ui/react-switch

  Use controlled switches when state drives other UI."
 []
 (let [enabled? (r/atom true)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-3"}
                                         [sut/switch {:checked @enabled?
                                                      :on-checked-change #(reset! enabled? %)}]
                                         [:span {:class "text-sm"}
                                          (if @enabled? "Enabled" "Disabled")]]))))