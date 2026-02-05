(ns mateuszmazurczak.portfolio.ui-components.stepper
  (:require
   ["lucide-react"                         :refer [Check CircleDollarSign User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.stepper :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Stepper"})

(defn- step-data
  []
  [{:id :details
    :title "Details"
    :description "Account info"}
   {:id :billing
    :title "Billing"
    :description "Payment setup"}
   {:id :confirm
    :title "Confirm"
    :description "Review"}])

(defscene
 stepper-horizontal
 "Horizontal stepper with panels.

  Custom component — not from shadcn/ui.
  Uses React Context to share step state across components.

  Best for linear multi-step flows."
 []
 (let [steps (step-data)
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
              [sut/stepper-title {}
               title]
              [sut/stepper-description {}
               description]])]
          [sut/stepper-panel {:id :details}
           [:div {:class "text-sm text-muted-foreground"}
            "Fill in your account details."]]
          [sut/stepper-panel {:id :billing}
           [:div {:class "text-sm text-muted-foreground"}
            "Add your billing information."]]
          [sut/stepper-panel {:id :confirm}
           [:div {:class "text-sm text-muted-foreground"}
            "Review and confirm."]]
          [sut/stepper-controls {}
           (button/button {:variant :outline
                           :disabled (nil? prev-id)
                           :on-click #(when prev-id (reset! current-step prev-id))}
                          "Back")
           (button/button {:disabled (nil? next-id)
                           :on-click #(when next-id (reset! current-step next-id))}
                          "Next")]]])))))

(defscene
 stepper-vertical
 "Vertical stepper layout.

  Custom component — not from shadcn/ui.
  Use :variant :vertical for stacked steps and panels."
 []
 (let [steps (step-data)
       current-step (r/atom :billing)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl"}
       [sut/stepper {:current-step @current-step
                     :variant :vertical
                     :on-step-change #(reset! current-step %)}
        [sut/stepper-navigation {}
         (for [{:keys [id title description]} steps]
           ^{:key id}
           [sut/stepper-step {:id id}
            [sut/stepper-title {}
             title]
            [sut/stepper-description {}
             description]])]
        [sut/stepper-panel {:id :details}
         [:div {:class "text-sm text-muted-foreground"}
          "Account setup details."]]
        [sut/stepper-panel {:id :billing}
         [:div {:class "text-sm text-muted-foreground"}
          "Billing preferences."]]
        [sut/stepper-panel {:id :confirm}
         [:div {:class "text-sm text-muted-foreground"}
          "Confirmation step."]]]]))))

(defscene
 stepper-circle
 "Circle stepper variant.

  Custom component — not from shadcn/ui.
  Use :variant :circle to show step count and progress ring."
 []
 (let [steps (step-data)
       current-step (r/atom :details)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/stepper {:current-step @current-step
                                                       :variant :circle
                                                       :on-step-change #(reset! current-step %)}
                                          [sut/stepper-navigation {}
                                           (for [{:keys [id title description]} steps]
                                             ^{:key id}
                                             [sut/stepper-step {:id id}
                                              [sut/stepper-title {}
                                               title]
                                              [sut/stepper-description {}
                                               description]])]]]))))

(defscene
 stepper-reverse-progress
 "Reverse progress for newest-first lists.

  Custom component — not from shadcn/ui.
  Use :reverse-progress? to mark later steps as completed."
 []
 (let [steps (step-data)
       current-step (r/atom :billing)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/stepper {:current-step @current-step
                                                       :reverse-progress? true
                                                       :on-step-change #(reset! current-step %)}
                                          [sut/stepper-navigation {}
                                           (for [{:keys [id title description]} steps]
                                             ^{:key id}
                                             [sut/stepper-step {:id id}
                                              [sut/stepper-title {}
                                               title]
                                              [sut/stepper-description {}
                                               description]])]]]))))

(defscene
 stepper-disabled-steps
 "Stepper with disabled steps.

  Custom component — not from shadcn/ui.
  Disabled steps are not interactive and appear muted."
 []
 (let [steps (step-data)
       current-step (r/atom :details)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/stepper {:current-step @current-step
                                                       :on-step-change #(reset! current-step %)}
                                          [sut/stepper-navigation {}
                                           (for [{:keys [id title description]} steps]
                                             ^{:key id}
                                             [sut/stepper-step {:id id
                                                                :disabled? (= id :billing)}
                                              [sut/stepper-title {}
                                               title]
                                              [sut/stepper-description {}
                                               description]])]]]))))

(defscene
 stepper-label-orientation
 "Vertical label orientation.

  Custom component — not from shadcn/ui.
  Use :label-orientation :vertical for compact headers."
 []
 (let [steps (step-data)
       current-step (r/atom :details)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/stepper {:current-step @current-step
                                                       :label-orientation :vertical
                                                       :on-step-change #(reset! current-step %)}
                                          [sut/stepper-navigation {}
                                           (for [{:keys [id title description]} steps]
                                             ^{:key id}
                                             [sut/stepper-step {:id id}
                                              [sut/stepper-title {}
                                               title]
                                              [sut/stepper-description {}
                                               description]])]]]))))

(defscene
 stepper-custom-icons
 "Stepper with custom icons per step.

  Custom component — not from shadcn/ui.
  Provide an :icon to override the step number."
 []
 (let [steps [{:id :account
               :title "Account"
               :icon [:> User]}
              {:id :billing
               :title "Billing"
               :icon [:> CircleDollarSign]}
              {:id :done
               :title "Complete"
               :icon [:> Check]}]
       current-step (r/atom :billing)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/stepper {:current-step @current-step
                                                       :on-step-change #(reset! current-step %)}
                                          [sut/stepper-navigation {}
                                           (for [{:keys [id title icon]} steps]
                                             ^{:key id}
                                             [sut/stepper-step {:id id
                                                                :icon icon}
                                              [sut/stepper-title {}
                                               title]])]]]))))

(defscene
 stepper-form-content
 "Stepper panels with form-like content.

  Custom component — not from shadcn/ui.
  Panels can host any content, including forms or summaries."
 []
 (let [current-step (r/atom :details)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-xl space-y-4"}
       [sut/stepper {:current-step @current-step
                     :on-step-change #(reset! current-step %)}
        [sut/stepper-navigation {}
         [sut/stepper-step {:id :details}
          [sut/stepper-title {}
           "Details"]]
         [sut/stepper-step {:id :billing}
          [sut/stepper-title {}
           "Billing"]]
         [sut/stepper-step {:id :confirm}
          [sut/stepper-title {}
           "Confirm"]]]
        [sut/stepper-panel {:id :details}
         [:div {:class "rounded-md border p-4 text-sm"}
          "Account details form fields go here."]]
        [sut/stepper-panel {:id :billing}
         [:div {:class "rounded-md border p-4 text-sm"}
          "Billing preferences and card form fields."]]
        [sut/stepper-panel {:id :confirm}
         [:div {:class "rounded-md border p-4 text-sm"}
          "Review summary and confirm action."]]]]))))
