(ns mateuszmazurczak.portfolio.ui-components.dialog
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.dialog  :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Dialog"})

(defscene
 basic-dialog
 "Dialog with header and actions."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/dialog {:open @open?
                    :onOpenChange #(reset! open? %)}
        [sut/dialog-trigger {:asChild true}
         (button/button {:variant :outline} "Open Dialog")]
        [sut/dialog-content {}
         [sut/dialog-header {}
          [sut/dialog-title {} "Confirm action"]
          [sut/dialog-description {}
           "This action cannot be undone."]]
         [:div {:class "py-2 text-sm"}
          "Proceed with the operation?"]
         [sut/dialog-footer {}
          (button/button {:variant :outline
                          :on-click #(reset! open? false)}
                         "Cancel")
          (button/button {:on-click #(reset! open? false)} "Confirm")]]]]))))