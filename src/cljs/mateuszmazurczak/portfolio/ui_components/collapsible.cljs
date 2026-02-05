(ns mateuszmazurczak.portfolio.ui-components.collapsible
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button      :as button]
   [mateuszmazurczak.ui.components.collapsible :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]
   [reagent.core                               :as r]))

(configure-scenes {:collection :ui-components
                   :title "Collapsible"})

(defscene
 collapsible-basic
 "Collapsible content with toggle.

  Based on shadcn/ui Collapsible — https://ui.shadcn.com/docs/components/collapsible
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

  Based on shadcn/ui Collapsible — https://ui.shadcn.com/docs/components/collapsible
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

  Based on shadcn/ui Collapsible — https://ui.shadcn.com/docs/components/collapsible
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

  Based on shadcn/ui Collapsible — https://ui.shadcn.com/docs/components/collapsible
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
