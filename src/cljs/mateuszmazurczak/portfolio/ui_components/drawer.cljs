(ns mateuszmazurczak.portfolio.ui-components.drawer
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.drawer :as sut]
   [mateuszmazurczak.ui.components.input  :as input]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r]))

(configure-scenes {:collection :ui-components
                   :title "Drawer"})

(defscene
 bottom-drawer
 "Bottom drawer with form example.
   
   Common use case for mobile: a drawer that slides up from the bottom
   with form fields or actions."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-4"}
       [sut/drawer {:open @open?
                    :on-open-change #(reset! open? %)
                    :direction :bottom}
        [sut/drawer-trigger {}
         (button/button {:variant :outline} "Open Bottom Drawer")]
        [sut/drawer-content {}
         [sut/drawer-header {}
          [sut/drawer-title {}
           "Edit Profile"]
          [sut/drawer-description {}
           "Make changes to your profile here. Click save when you're done."]]
         [:div {:class "p-4 space-y-4"}
          [:div {:class "space-y-2"}
           [:label {:class "text-sm font-medium"}
            "Name"]
           [input/input {:placeholder "Enter your name"}]]
          [:div {:class "space-y-2"}
           [:label {:class "text-sm font-medium"}
            "Email"]
           [input/input {:type "email"
                         :placeholder "Enter your email"}]]]
         [sut/drawer-footer {}
          (button/button {:on-click #(reset! open? false)} "Save Changes")
          (button/button {:variant :outline
                          :on-click #(reset! open? false)}
                         "Cancel")]]]]))))

(defscene
 right-drawer
 "Right drawer with navigation example.
   
   Slides in from the right side - useful for navigation menus
   or detail panels."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-4"}
       [sut/drawer {:open @open?
                    :on-open-change #(reset! open? %)
                    :direction :right}
        [sut/drawer-trigger {}
         (button/button {:variant :outline} "Open Right Drawer")]
        [sut/drawer-content {:class "w-80"}
         [sut/drawer-header {}
          [sut/drawer-title {}
           "Navigation"]
          [sut/drawer-description {}
           "Browse through different sections"]]
         [:div {:class "p-4 space-y-2"}
          (for [item ["Dashboard" "Projects" "Tasks" "Settings"]]
            ^{:key item}
            [:button {:class "w-full text-left px-3 py-2 rounded-md hover:bg-accent"
                      :on-click #(reset! open? false)}
             item])]
         [sut/drawer-footer {}
          (button/button {:variant :ghost
                          :on-click #(reset! open? false)
                          :class "w-full"}
                         "Close")]]]]))))

(defscene
 drawer-with-scroll
 "Bottom drawer with scrollable content.
   
   Shows how the drawer handles overflow content with max-height."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-4"}
       [sut/drawer {:open @open?
                    :on-open-change #(reset! open? %)
                    :direction :bottom}
        [sut/drawer-trigger {}
         (button/button {:variant :outline} "Open Scrollable Drawer")]
        [sut/drawer-content {}
         [sut/drawer-header {}
          [sut/drawer-title {}
           "Terms and Conditions"]
          [sut/drawer-description {}
           "Please read through the terms before continuing"]]
         [:div {:class "p-4 overflow-y-auto"}
          (for [i (range 20)]
            ^{:key i}
            [:p {:class "mb-4 text-sm text-muted-foreground"}
             (str "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                  "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                  "Section "
                  (inc i)
                  " of the terms.")])]
         [sut/drawer-footer {}
          (button/button {:on-click #(reset! open? false)} "I Accept")
          (button/button {:variant :outline
                          :on-click #(reset! open? false)}
                         "Decline")]]]]))))

(defscene
 drawer-no-scale
 "Drawer without background scaling.
   
   By default, the background scales down slightly when drawer opens.
   This example shows how to disable that effect."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-4"}
       [sut/drawer {:open @open?
                    :on-open-change #(reset! open? %)
                    :direction :bottom
                    :should-scale-background false}
        [sut/drawer-trigger {}
         (button/button {:variant :outline} "Open (No Scale)")]
        [sut/drawer-content {}
         [sut/drawer-header {}
          [sut/drawer-title {}
           "No Background Scaling"]
          [sut/drawer-description {}
           "Notice the background doesn't scale when this opens"]]
         [:div {:class "p-4"}
          [:p {:class "text-sm text-muted-foreground"}
           "This drawer is configured with :should-scale-background false"]]
         [sut/drawer-footer {}
          (button/button {:on-click #(reset! open? false)} "Close")]]]]))))
