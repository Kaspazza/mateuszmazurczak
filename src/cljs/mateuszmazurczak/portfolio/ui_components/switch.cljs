(ns mateuszmazurczak.portfolio.ui-components.switch
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field  :as field]
   [mateuszmazurczak.ui.components.label  :as label]
   [mateuszmazurczak.ui.components.switch :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r]))

(configure-scenes {:collection :ui-components
                   :title "Switch"})

(defscene
 switch-demo
 "Switch paired with a label.

  Based on shadcn/ui Switch — https://ui.shadcn.com/docs/components/switch
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

  Based on shadcn/ui Field + Switch —
  https://ui.shadcn.com/docs/components/field
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

  Based on shadcn/ui Switch — https://ui.shadcn.com/docs/components/switch
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
 switch-controlled
 "Controlled switch with live state.

  Custom example — not from shadcn/ui.
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