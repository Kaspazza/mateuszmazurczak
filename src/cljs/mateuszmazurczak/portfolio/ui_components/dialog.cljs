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
   [reagent.core                           :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Dialog"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Dialog (modal) component with overlay and content area."
            :npm-install "npm install @radix-ui/react-dialog lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.dialog")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/dialog.cljs"
            :filename "dialog.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "dialog"
               :link {:href "https://www.radix-ui.com/primitives/docs/components/dialog" :label "Radix Dialog Docs"}
                :description "Radix Dialog root that controls modal open/close state and accessibility semantics."
                :props [{:name ":open"            :type "boolean"      :default nil    :description "Controlled open state."}
                        {:name ":defaultOpen"      :type "boolean"      :default nil    :description "Uncontrolled initial open state."}
                        {:name ":onOpenChange"     :type "function"     :default nil    :description "Callback when state changes: (fn [open?] ...)."}
                        {:name ":modal"            :type "boolean"      :default "true" :description "Whether dialog behaves as modal."}
                        {:name "additional props"  :type "map entries"  :default nil    :description "Forwarded to Radix Dialog.Root."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-trigger"
                :description "Interactive trigger that opens the dialog."
                :props [{:name ":asChild"         :type "boolean"      :default nil :description "Compose with child component."}
                        {:name "additional props"  :type "map entries"  :default nil :description "Forwarded to Radix Dialog.Trigger."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-portal"
                :description "Portal wrapper to render dialog outside normal DOM hierarchy."
                :props [{:name "additional props" :type "map entries" :default nil :description "Forwarded to Radix Dialog.Portal."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-close"
                :description "Control that closes the dialog when activated."
                :props [{:name ":asChild"         :type "boolean"      :default nil :description "Compose with child component."}
                        {:name "additional props"  :type "map entries"  :default nil :description "Forwarded to Radix Dialog.Close."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-overlay"
                :description "Backdrop layer behind dialog content."
                :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"  :type "map entries" :default nil :description "Forwarded to Radix Dialog.Overlay."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-content"
                :description "Main dialog panel with built-in overlay + optional corner close button."
                :props [{:name ":class"            :type "string"      :default nil    :description "Additional Tailwind classes."}
                        {:name ":showCloseButton"   :type "boolean"     :default "true" :description "Show top-right close icon button."}
                        {:name "additional props"   :type "map entries" :default nil    :description "Forwarded to Radix Dialog.Content."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-header"
                :description "Layout wrapper for dialog-title and dialog-description."
                :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"  :type "map entries" :default nil :description "Forwarded to underlying div."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-footer"
                :description "Responsive action area (stacked on mobile, row-aligned on desktop)."
                :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"  :type "map entries" :default nil :description "Forwarded to underlying div."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-title"
                :description "Accessible title announced by screen readers (aria-labelledby)."
                :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"  :type "map entries" :default nil :description "Forwarded to Radix Dialog.Title."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "dialog-description"
                :description "Accessible supporting text announced by screen readers (aria-describedby)."
                :props [{:name ":class"           :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props"  :type "map entries" :default nil :description "Forwarded to Radix Dialog.Description."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Use :asChild (camelCase) for trigger/close when wrapping existing button components."]
                [:li "For accessible dialogs, always include dialog-title and usually dialog-description."]
                [:li "Choose controlled mode (:open + :onOpenChange) OR uncontrolled mode (:defaultOpen)."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[dialog {:open @open?\n         :onOpenChange #(reset! open? %)}\n [dialog-trigger {:asChild true}\n  [button {:variant :outline} \"Open\"]]\n [dialog-content {:class \"sm:max-w-[425px]\"}\n  [dialog-header {}\n   [dialog-title {} \"Edit profile\"]\n   [dialog-description {} \"Update details and save.\"]]\n  [dialog-footer {}\n   [dialog-close {:asChild true} [button {:variant :outline} \"Cancel\"]]\n   [button {:type \"submit\"} \"Save\"]]]]"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                [:a {:href "https://www.radix-ui.com/primitives/docs/components/dialog"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "Radix Dialog Docs →"]]]]]]))

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