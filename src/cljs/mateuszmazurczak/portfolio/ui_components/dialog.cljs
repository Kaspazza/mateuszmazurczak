(ns mateuszmazurczak.portfolio.ui-components.dialog
  (:require
   ["lucide-react"                         :refer [Calendar CreditCard Settings Smile User]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.command :as command]
   [mateuszmazurczak.ui.components.dialog  :as sut]
   [mateuszmazurczak.ui.components.drawer  :as drawer]
   [mateuszmazurczak.ui.components.input   :as input]
   [mateuszmazurczak.ui.components.label   :as label]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Dialog"})

(defn- profile-form
  [{:keys [class]}]
  [:div {:class (str "grid gap-4 " class)}
   [:div {:class "grid gap-2"}
    [label/label {:html-for "dialog-name"}
     "Name"]
    [input/input {:id "dialog-name"
                  :default-value "Pedro Duarte"}]]
   [:div {:class "grid gap-2"}
    [label/label {:html-for "dialog-username"}
     "Username"]
    [input/input {:id "dialog-username"
                  :default-value "@peduarte"}]]])

(defscene
 dialog-demo
 "Dialog with form fields and footer actions.

  Based on shadcn/ui Dialog — https://ui.shadcn.com/docs/components/dialog
  Radix primitive: @radix-ui/react-dialog

  Use dialogs for focused, interruptive workflows such as profile edits."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/dialog {}
    [sut/dialog-trigger {:as-child true}
     (button/button {:variant :outline} "Open Dialog")]
    [sut/dialog-content {:class "sm:max-w-[425px]"}
     [sut/dialog-header {}
      [sut/dialog-title {}
       "Edit profile"]
      [sut/dialog-description {}
       "Make changes to your profile here. Click save when you're done."]]
     [profile-form {}]
     [sut/dialog-footer {}
      [sut/dialog-close {:as-child true}
       (button/button {:variant :outline} "Cancel")]
      (button/button {:type "submit"} "Save changes")]]]]))

(defscene
 dialog-close-button
 "Dialog with explicit close button in footer.

  Based on shadcn/ui Dialog — https://ui.shadcn.com/docs/components/dialog
  Radix primitive: @radix-ui/react-dialog

  Useful for share sheets or info dialogs."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/dialog {}
    [sut/dialog-trigger {:as-child true}
     (button/button {:variant :outline} "Share")]
    [sut/dialog-content {:class "sm:max-w-md"}
     [sut/dialog-header {}
      [sut/dialog-title {}
       "Share link"]
      [sut/dialog-description {}
       "Anyone with this link will be able to view this."]]
     [:div {:class "flex items-center gap-2"}
      [:div {:class "grid flex-1 gap-2"}
       [label/label {:html-for "share-link"
                     :class "sr-only"}
        "Link"]
       [input/input {:id "share-link"
                     :read-only true
                     :default-value "https://ui.shadcn.com/docs/installation"}]]]
     [sut/dialog-footer {:class "sm:justify-start"}
      [sut/dialog-close {:as-child true}
       (button/button {:type "button"
                       :variant :secondary}
                      "Close")]]]]]))

(defscene
 command-dialog
 "Command palette rendered inside a dialog.

  Based on shadcn/ui Command Dialog — https://ui.shadcn.com/docs/components/command
  Radix primitive: @radix-ui/react-dialog

  Props are idiomatic kebab-case; Reagent converts to camelCase for Radix.
  Use this for global search or quick actions."
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 space-y-3"}
       [:p {:class "text-muted-foreground text-sm"}
        "Press the button to open the command palette."]
       [sut/dialog {:open @open?
                    :on-open-change #(reset! open? %)}
        [sut/dialog-trigger {:as-child true}
         (button/button {:variant :outline} "Open Command Palette")]
        [sut/dialog-content {:class "p-0 overflow-hidden"}
         [command/command {:class "[&_[data-slot=command-input-wrapper]]:h-12"}
          [command/command-input {:placeholder "Type a command or search..."}]
          [command/command-list {}
           [command/command-empty {}
            "No results found."]
           [command/command-group {:heading "Suggestions"}
            [command/command-item {}
             [:> Calendar]
             [:span "Calendar"]]
            [command/command-item {}
             [:> Smile]
             [:span "Search Emoji"]]
            [command/command-item {:disabled true}
             [:> CreditCard]
             [:span "Calculator"]]]
           [command/command-separator {}]
           [command/command-group {:heading "Settings"}
            [command/command-item {}
             [:> User]
             [:span "Profile"]]
            [command/command-item {}
             [:> Settings]
             [:span "Settings"]]]]]]]]))))

(defscene
 drawer-dialog
 "Dialog and drawer pair for responsive workflows.

  Based on shadcn/ui Drawer + Dialog — https://ui.shadcn.com/docs/components/drawer
  Radix primitive: @radix-ui/react-dialog

  Here we show both variants side by side (instead of media query)."
 []
 (let [dialog-open? (r/atom false)
       drawer-open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 flex flex-wrap gap-4"}
       [sut/dialog {:open @dialog-open?
                    :on-open-change #(reset! dialog-open? %)}
        [sut/dialog-trigger {:as-child true}
         (button/button {:variant :outline} "Edit Profile (Dialog)")]
        [sut/dialog-content {:class "sm:max-w-[425px]"}
         [sut/dialog-header {}
          [sut/dialog-title {}
           "Edit profile"]
          [sut/dialog-description {}
           "Make changes to your profile here. Click save when you're done."]]
         [profile-form {}]]]
       [drawer/drawer {:open @drawer-open?
                       :on-open-change #(reset! drawer-open? %)
                       :direction :bottom}
        [drawer/drawer-trigger {}
         (button/button {:variant :outline} "Edit Profile (Drawer)")]
        [drawer/drawer-content {}
         [drawer/drawer-header {:class "text-left"}
          [drawer/drawer-title {}
           "Edit profile"]
          [drawer/drawer-description {}
           "Make changes to your profile here. Click save when you're done."]]
         [profile-form {:class "px-4"}]
         [drawer/drawer-footer {}
          (button/button {:variant :outline
                          :on-click #(reset! drawer-open? false)}
                         "Cancel")]]]]))))