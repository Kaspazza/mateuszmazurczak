(ns mateuszmazurczak.portfolio.ui-components.stepper
  (:require
   ["lucide-react"                         :refer [Check CircleDollarSign User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.stepper :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Stepper"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Pure presentational stepper components using React Context."
            :npm-install "npm install react"
            :source-code (embed-source "mateuszmazurczak.ui.components.stepper")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/stepper.cljs"
            :filename "stepper.cljs"}])

(defscene
 api-reference
 "Complete reference for all Stepper component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Stepper components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper"
       :description "Root context provider for multi-step flows. Coordinates active step state shared by navigation and panels."
       :props [{:name ":current-step"      :type "string | keyword | number" :default nil          :description "Active step id."}
               {:name ":on-step-change"    :type "function"  :default nil          :description "Callback when step changes: (fn [step-id])."}
               {:name ":variant"           :type "keyword"   :default ":horizontal" :description ":horizontal | :vertical | :circle."}
               {:name ":label-orientation" :type "keyword"   :default ":horizontal" :description ":horizontal | :vertical."}
               {:name ":reverse-progress?" :type "boolean"   :default "false"      :description "Marks progress in reverse direction."}
               {:name ":class"             :type "string"    :default nil          :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-navigation"
       :description "Navigation container that auto-indexes stepper-step children and injects step metadata."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-step"
       :description "Interactive step node with indicator, title/description slots, and optional icon override."
       :props [{:name ":id"        :type "keyword | string | number" :default nil :description "Step identifier."}
               {:name ":disabled?" :type "boolean"                  :default nil :description "Disables interaction for this step."}
               {:name ":icon"      :type "hiccup | React element"   :default nil :description "Replaces numeric step indicator."}
               {:name ":class"     :type "string"                   :default nil :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-panel"
       :description "Content panel bound to a specific step id; renders only when active."
       :props [{:name ":id"    :type "keyword | string | number" :default nil :description "Step id this panel belongs to."}
               {:name ":class" :type "string"                   :default nil :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-title"
       :description "Title text slot for step labels."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-description"
       :description "Secondary description text slot for step labels."
       :props [[":class" "string, optional - Additional Tailwind classes."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "stepper-controls"
       :description "Layout container for navigation buttons (Back/Next/Finish)."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "stepper-step and stepper-panel require stepper context; use them inside [stepper ...]."]
       [:li "Each stepper-step :id should have a corresponding stepper-panel :id for complete UX."]
       [:li "stepper-navigation auto-injects index metadata; avoid manually setting :index/:total props."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "(let [current (r/atom :details)]\n  [stepper {:current-step @current\n            :on-step-change #(reset! current %)}\n   [stepper-navigation {}\n    [stepper-step {:id :details} [stepper-title {} \"Details\"]]\n    [stepper-step {:id :billing} [stepper-title {} \"Billing\"]]]\n   [stepper-panel {:id :details} [:div \"Details form\"]]\n   [stepper-panel {:id :billing} [:div \"Billing form\"]]])"]]]]]]))

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
