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
   [reagent.core                                 :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Dropdown Menu"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Dropdown menu component for displaying a menu of actions."
            :npm-install "npm install @radix-ui/react-dropdown-menu lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.dropdown_menu")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/dropdown_menu.cljs"
            :filename "dropdown_menu.cljs"}])

(defscene
 api-reference
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-4"}
    [mm-portfolio-utils/api-component-card
     {:component-name "dropdown-menu"
      :link {:href "https://www.radix-ui.com/primitives/docs/components/dropdown-menu" :label "Radix Dropdown Menu Docs"}
       :description "Radix DropdownMenu root controlling menu open state and modality."
       :props [{:name ":open"          :type "boolean"      :default nil    :description "Controlled open state."}
               {:name ":default-open"  :type "boolean"      :default nil    :description "Uncontrolled initial open state."}
               {:name ":on-open-change" :type "function"    :default nil    :description "Callback: (fn [open?] ...)."}
               {:name ":modal"         :type "boolean"      :default "true" :description "Whether menu behaves modally."}
               {:name ":class"         :type "string"       :default nil    :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil   :description "Forwarded to Radix DropdownMenu.Root."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-trigger"
       :description "Interactive trigger for opening/closing the menu."
       :props [{:name ":as-child"      :type "boolean"      :default nil :description "Compose trigger behavior into child element."}
               {:name ":class"         :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Trigger."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-content"
       :description "Portaled menu content with keyboard navigation and collision-aware positioning."
       :props [{:name ":side-offset"   :type "number"       :default "4"  :description "Distance from trigger."}
               {:name ":align"         :type "keyword | string" :default nil :description "Content alignment relative to trigger."}
               {:name ":class"         :type "string"       :default nil  :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Content."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-item"
       :description "Standard actionable menu item."
       :props [{:name ":inset"         :type "boolean"      :default nil :description "Adds inset padding for alignment."}
               {:name ":disabled"      :type "boolean"      :default nil :description "Disables selection."}
               {:name ":on-select"     :type "function"     :default nil :description "Item select callback."}
               {:name ":class"         :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Item."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-checkbox-item"
       :description "Toggleable checkbox item for multi-select preferences."
       :props [{:name ":checked"           :type "boolean | \"indeterminate\"" :default nil :description "Checked state."}
               {:name ":on-checked-change" :type "function"     :default nil :description "Callback when checked state changes."}
               {:name ":disabled"          :type "boolean"      :default nil :description "Disables item."}
               {:name ":class"             :type "string"       :default nil :description "Additional classes."}
               {:name "additional props"   :type "map entries"  :default nil :description "Forwarded to Radix CheckboxItem."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-radio-group"
       :description "Container for radio items with single-selection behavior."
       :props [{:name ":value"          :type "string"       :default nil :description "Controlled selected value."}
               {:name ":on-value-change" :type "function"    :default nil :description "Callback: (fn [value] ...)."}
               {:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix RadioGroup."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-radio-item"
       :description "Single option within dropdown-menu-radio-group."
       :props [{:name ":value"          :type "string"       :default nil :description "Value represented by this item."}
               {:name ":disabled"       :type "boolean"      :default nil :description "Disables option."}
               {:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix RadioItem."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-group"
       :description "Groups related menu items."
       :props [{:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Group."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-label"
       :description "Non-interactive section label inside menu content."
       :props [{:name ":inset"          :type "boolean"      :default nil :description "Adds inset padding."}
               {:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Label."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-separator"
       :description "Visual divider between menu groups."
       :props [{:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Separator."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-shortcut"
       :description "Right-aligned visual shortcut hint (display only)."
       :props [{:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to underlying span."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-sub"
       :description "Root for nested submenu interactions."
       :props [{:name ":open"           :type "boolean"  :default nil :description "Controlled submenu open state."}
               {:name ":default-open"   :type "boolean"  :default nil :description "Uncontrolled initial open state."}
               {:name ":on-open-change" :type "function" :default nil :description "Callback when submenu state changes."}
               {:name ":class"          :type "string"   :default nil :description "Additional classes."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-sub-trigger"
       :description "Menu item that opens a nested submenu."
       :props [{:name ":inset"          :type "boolean"      :default nil :description "Adds inset padding."}
               {:name ":disabled"       :type "boolean"      :default nil :description "Disables submenu trigger."}
               {:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix SubTrigger."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-sub-content"
       :description "Nested submenu content container."
       :props [{:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix SubContent."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "dropdown-menu-portal"
       :description "Portal wrapper for rendering menu outside local stacking context."
       :props [{:name ":container"      :type "DOM node"     :default nil :description "Custom portal target."}
               {:name ":force-mount"   :type "boolean"      :default nil :description "Force mounting for animation control."}
               {:name ":class"          :type "string"       :default nil :description "Additional classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Portal."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Many wrappers forward additional props directly to Radix primitives; avoid undocumented keys that may conflict with Radix internals."]
       [:li "For composition with existing buttons/links, use :as-child on dropdown-menu-trigger."]
       [:li "dropdown-menu-shortcut is visual only; keyboard handling must be implemented separately."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[dropdown-menu {}\n [dropdown-menu-trigger {:as-child true}\n  [button {:variant :outline} \"Open\"]]\n [dropdown-menu-content {:align \"end\"}\n  [dropdown-menu-label {} \"Actions\"]\n  [dropdown-menu-item {:on-select #(js/console.log \"edit\")} \"Edit\"]\n  [dropdown-menu-separator {}]\n  [dropdown-menu-sub {}\n   [dropdown-menu-sub-trigger {} \"More\"]\n   [dropdown-menu-sub-content {}\n    [dropdown-menu-item {} \"Duplicate\"]]]]]"]]
     ]]])))

(defscene
 dropdown-menu-demo
 "Dropdown menu with grouped items and submenu.

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