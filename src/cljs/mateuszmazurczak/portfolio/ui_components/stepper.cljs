(ns mateuszmazurczak.portfolio.ui-components.stepper
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.stepper :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r]))

(configure-scenes {:collection :ui-components
                   :title "Stepper"})

(defscene
 basic-stepper
 "Horizontal stepper with panels." 
 []
 (let [steps [{:id :details
               :title "Details"
               :description "Account info"}
              {:id :billing
               :title "Billing"
               :description "Payment setup"}
              {:id :confirm
               :title "Confirm"
               :description "Review"}]
       step-ids (mapv :id steps)
       current-step (r/atom (first step-ids))]
   (fn []
     (let [current-index (.indexOf step-ids @current-step)
           prev-id (get step-ids (dec current-index))
           next-id (get step-ids (inc current-index))]
       (mm-portfolio-utils/wrap-component
        [:div {:class "p-6 max-w-xl space-y-6"}
         [sut/stepper {:current-step @current-step
                       :on-step-change #(reset! current-step %)}
          [sut/stepper-navigation {}
           (for [{:keys [id title description]} steps]
             ^{:key id}
             [sut/stepper-step {:id id}
              [sut/stepper-title {} title]
              [sut/stepper-description {} description]])]
          [sut/stepper-panel {:id :details}
           [:div {:class "text-sm text-muted-foreground"} "Fill in your account details."]]
          [sut/stepper-panel {:id :billing}
           [:div {:class "text-sm text-muted-foreground"} "Add your billing information."]]
          [sut/stepper-panel {:id :confirm}
           [:div {:class "text-sm text-muted-foreground"} "Review and confirm."]]
          [sut/stepper-controls {}
           (button/button {:variant :outline
                           :disabled (nil? prev-id)
                           :on-click #(when prev-id (reset! current-step prev-id))}
                          "Back")
           (button/button {:disabled (nil? next-id)
                           :on-click #(when next-id (reset! current-step next-id))}
                          "Next")]]])))))
