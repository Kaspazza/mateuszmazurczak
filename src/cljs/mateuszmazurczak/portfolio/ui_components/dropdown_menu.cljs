(ns mateuszmazurczak.portfolio.ui-components.dropdown-menu
  (:require
   ["lucide-react"                               :refer [MoreHorizontal]]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.dialog        :as dialog]
   [mateuszmazurczak.ui.components.dropdown-menu :as sut]
   [mateuszmazurczak.ui.components.field         :as field]
   [mateuszmazurczak.ui.components.input         :as input]
   [mateuszmazurczak.ui.components.label         :as label]
   [mateuszmazurczak.ui.components.textarea      :as textarea]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]
   [reagent.core                                 :as r]))

(configure-scenes {:collection :ui-components
                   :title "Dropdown Menu"})

(defscene
 dropdown-menu-demo
 "Dropdown menu with grouped items and submenu.

  Based on shadcn/ui Dropdown Menu — https://ui.shadcn.com/docs/components/dropdown-menu
  Radix primitive: @radix-ui/react-dropdown-menu

  Use groups, separators, and submenus for structured menus."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/dropdown-menu {}
    [sut/dropdown-menu-trigger {:as-child true}
     (button/button {:variant :outline} "Open")]
    [sut/dropdown-menu-content {:class "w-56"
                                :align "start"}
     [sut/dropdown-menu-label {}
      "My Account"]
     [sut/dropdown-menu-group {}
      [sut/dropdown-menu-item {}
       "Profile"
       [sut/dropdown-menu-shortcut {}
        "⇧⌘P"]]
      [sut/dropdown-menu-item {}
       "Billing"
       [sut/dropdown-menu-shortcut {}
        "⌘B"]]
      [sut/dropdown-menu-item {}
       "Settings"
       [sut/dropdown-menu-shortcut {}
        "⌘S"]]
      [sut/dropdown-menu-item {}
       "Keyboard shortcuts"
       [sut/dropdown-menu-shortcut {}
        "⌘K"]]]
     [sut/dropdown-menu-separator {}]
     [sut/dropdown-menu-group {}
      [sut/dropdown-menu-item {}
       "Team"]
      [sut/dropdown-menu-sub {}
       [sut/dropdown-menu-sub-trigger {}
        "Invite users"]
       [sut/dropdown-menu-sub-content {}
        [sut/dropdown-menu-item {}
         "Email"]
        [sut/dropdown-menu-item {}
         "Message"]
        [sut/dropdown-menu-separator {}]
        [sut/dropdown-menu-item {}
         "More..."]]]
      [sut/dropdown-menu-item {}
       "New Team"
       [sut/dropdown-menu-shortcut {}
        "⌘+T"]]]
     [sut/dropdown-menu-separator {}]
     [sut/dropdown-menu-item {}
      "GitHub"]
     [sut/dropdown-menu-item {}
      "Support"]
     [sut/dropdown-menu-item {:disabled true}
      "API"]
     [sut/dropdown-menu-separator {}]
     [sut/dropdown-menu-item {}
      "Log out"
      [sut/dropdown-menu-shortcut {}
       "⇧⌘Q"]]]]]))

(defscene
 dropdown-menu-checkboxes
 "Dropdown menu with checkbox items.

  Based on shadcn/ui Dropdown Menu — https://ui.shadcn.com/docs/components/dropdown-menu
  Radix primitive: @radix-ui/react-dropdown-menu

  Checkbox items allow toggling view preferences."
 []
 (let [show-status? (r/atom true)
       show-activity? (r/atom false)
       show-panel? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/dropdown-menu {}
        [sut/dropdown-menu-trigger {:as-child true}
         (button/button {:variant :outline} "Appearance")]
        [sut/dropdown-menu-content {:class "w-56"}
         [sut/dropdown-menu-label {}
          "Appearance"]
         [sut/dropdown-menu-separator {}]
         [sut/dropdown-menu-checkbox-item {:checked @show-status?
                                           :on-checked-change #(reset! show-status? %)}
          "Status Bar"]
         [sut/dropdown-menu-checkbox-item {:checked @show-activity?
                                           :disabled true
                                           :on-checked-change #(reset! show-activity? %)}
          "Activity Bar"]
         [sut/dropdown-menu-checkbox-item {:checked @show-panel?
                                           :on-checked-change #(reset! show-panel? %)}
          "Panel"]]]]))))

(defscene
 dropdown-menu-radio-group
 "Dropdown menu with radio group selection.

  Based on shadcn/ui Dropdown Menu — https://ui.shadcn.com/docs/components/dropdown-menu
  Radix primitive: @radix-ui/react-dropdown-menu

  Radio groups enforce a single selection within the menu."
 []
 (let [position (r/atom "bottom")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/dropdown-menu {}
        [sut/dropdown-menu-trigger {:as-child true}
         (button/button {:variant :outline} "Panel Position")]
        [sut/dropdown-menu-content {:class "w-56"}
         [sut/dropdown-menu-label {}
          "Panel Position"]
         [sut/dropdown-menu-separator {}]
         [sut/dropdown-menu-radio-group {:value @position
                                         :on-value-change #(reset! position %)}
          [sut/dropdown-menu-radio-item {:value "top"}
           "Top"]
          [sut/dropdown-menu-radio-item {:value "bottom"}
           "Bottom"]
          [sut/dropdown-menu-radio-item {:value "right"}
           "Right"]]]]]))))

(defscene
 dropdown-menu-dialog
 "Dropdown menu launching dialogs.

  Based on shadcn/ui Dropdown Menu + Dialog —
  https://ui.shadcn.com/docs/components/dropdown-menu
  Radix primitives: @radix-ui/react-dropdown-menu, @radix-ui/react-dialog

  Useful for contextual actions that open richer modals."
 []
 (let [show-new? (r/atom false)
       show-share? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/dropdown-menu {:modal false}
        [sut/dropdown-menu-trigger {:as-child true}
         (button/button {:variant :outline
                         :size :icon
                         :aria-label "Open menu"}
                        [:> MoreHorizontal])]
        [sut/dropdown-menu-content {:class "w-40"
                                    :align "end"}
         [sut/dropdown-menu-label {}
          "File Actions"]
         [sut/dropdown-menu-group {}
          [sut/dropdown-menu-item {:on-select #(reset! show-new? true)}
           "New File..."]
          [sut/dropdown-menu-item {:on-select #(reset! show-share? true)}
           "Share..."]
          [sut/dropdown-menu-item {:disabled true}
           "Download"]]]]
       [dialog/dialog {:open @show-new?
                       :on-open-change #(reset! show-new? %)}
        [dialog/dialog-content {:class "sm:max-w-[425px]"}
         [dialog/dialog-header {}
          [dialog/dialog-title {}
           "Create New File"]
          [dialog/dialog-description {}
           "Provide a name for your new file. Click create when you're done."]]
         [field/field-group {:class "pb-3"}
          [field/field {}
           [field/field-label {:html-for "filename"}
            "File Name"]
           [input/input {:id "filename"
                         :placeholder "document.txt"}]]]
         [dialog/dialog-footer {}
          [dialog/dialog-close {:as-child true}
           (button/button {:variant :outline} "Cancel")]
          (button/button {:type "submit"} "Create")]]]
       [dialog/dialog {:open @show-share?
                       :on-open-change #(reset! show-share? %)}
        [dialog/dialog-content {:class "sm:max-w-[425px]"}
         [dialog/dialog-header {}
          [dialog/dialog-title {}
           "Share File"]
          [dialog/dialog-description {}
           "Anyone with the link will be able to view this file."]]
         [field/field-group {:class "py-3"}
          [field/field {}
           [label/label {:html-for "share-email"}
            "Email Address"]
           [input/input {:id "share-email"
                         :type "email"
                         :placeholder "shadcn@vercel.com"}]]
          [field/field {}
           [field/field-label {:html-for "share-message"}
            "Message (Optional)"]
           [textarea/textarea {:id "share-message"
                               :placeholder "Check out this file"}]]]
         [dialog/dialog-footer {}
          [dialog/dialog-close {:as-child true}
           (button/button {:variant :outline} "Cancel")]
          (button/button {:type "submit"} "Send Invite")]]]]))))