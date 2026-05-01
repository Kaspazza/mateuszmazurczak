(ns mateuszmazurczak.portfolio.ui-components.button
  (:require
   ["lucide-react"                         :refer [ArrowUpRight GitBranch]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as button]
   [mateuszmazurczak.ui.components.spinner :as spinner]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Button"})

(defscene installation
  "Install dependencies and copy the component code into your project."
  []
  [mm-portfolio-utils/installation-scene
   {:description "Button component with support for multiple variants and sizes."
    :npm-install "npm install @radix-ui/react-slot"
    :source-code (embed-source "mateuszmazurczak.ui.components.button")
    :namespace-path "src/cljs/mateuszmazurczak/ui/components/button.cljs"
    :filename "button.cljs"}])

(defscene
  api-reference
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 max-w-4xl"}
    [:div {:class "space-y-6"}
     [:div {:class "space-y-4"}
      [mm-portfolio-utils/api-component-card
       {:component-name "button"
        :link {:href "https://ui.shadcn.com/docs/components/button"
               :label "shadcn Button Docs"}
        :description "Polymorphic button primitive with variant + size systems. Supports regular button rendering or slot-based composition via :as-child. Additional props are forwarded to the underlying element."
        :props
        [{:name ":variant"         :type "keyword"      :default ":default" :description "One of: :default | :destructive | :outline | :secondary | :ghost | :link"}
         {:name ":size"            :type "keyword"      :default ":default" :description "One of: :xs | :sm | :default | :lg | :icon | :icon-xs | :icon-sm | :icon-lg"}
         {:name ":as-child"        :type "boolean"      :default "false"    :description "Render using Radix Slot and pass styles/behavior to the child element."}
         {:name ":disabled"        :type "boolean"      :default nil        :description "Disables interaction and applies disabled styles."}
         {:name ":type"            :type "string"       :default nil        :description "Native button type: \"button\" | \"submit\" | \"reset\"."}
         {:name ":on-click"        :type "function"     :default nil        :description "Click handler: (fn [event] ...)."}
         {:name ":class"           :type "string"       :default nil        :description "Additional Tailwind classes."}
         {:name "additional props" :type "map entries"  :default nil        :description "Forwarded to the rendered element (button or slotted child)."}]}]
      [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
       [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
       [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
        [:li "Use :type \"submit\" inside forms to trigger native form submission semantics."]
        [:li "When :as-child is true, ensure the child is an interactive element (<a>, <button>, etc.) for accessibility."]
        [:li "Prefer semantic variants (:destructive for dangerous actions) over one-off color overrides."]]]
      [:div {:class "border rounded-lg p-4 bg-muted/50"}
       [:h4 {:class "text-sm font-semibold mb-2"}
        "Usage Example"]
       [:pre {:class "text-xs overflow-x-auto"}
        [:code "[:div {:class \"flex items-center gap-2\"}\n [button {:variant :default\n          :on-click #(js/console.log \"continue\")}\n  \"Continue\"]\n [button {:variant :outline\n          :size :icon\n          :aria-label \"Open settings\"}\n  [:> ArrowUpRight]]\n [button {:as-child true}\n  [:a {:href \"/docs\"} \"Documentation\"]]]"]]]]]]))

(defscene
  button-default
  "Default button style for primary actions.

  Radix primitive: @radix-ui/react-slot (for :as-child polymorphism)

  Our CLJS wrapper uses keyword props (:variant, :size) instead of
  string variants. The default variant is :default."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {} "Continue")]))

(defscene
  button-destructive
  "Destructive button for dangerous actions.

  Radix primitive: @radix-ui/react-slot

  Use :destructive for irreversible actions (delete, remove, etc.)."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :destructive} "Delete project")]))

(defscene
  button-outline
  "Outlined button for secondary actions.

  Radix primitive: @radix-ui/react-slot

  Outline buttons are visually lighter but still prominent."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :outline} "View details")]))

(defscene
  button-secondary
  "Secondary button for neutral actions.

  Radix primitive: @radix-ui/react-slot

  Use :secondary to de-emphasize a primary action."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :secondary} "Secondary action")]))

(defscene
  button-ghost
  "Ghost button for low-emphasis actions.

  Radix primitive: @radix-ui/react-slot

  Ghost buttons are useful in dense toolbars."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :ghost} "Dismiss")]))

(defscene
  button-link
  "Link-styled button for inline actions.

  Radix primitive: @radix-ui/react-slot

  Use :link when you want a textual action that still behaves like a button."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :link} "Learn more")]))

(defscene
  button-icon
  "Icon-only button for compact controls.

  Radix primitive: @radix-ui/react-slot

  Use :size :icon for square icon buttons.
  Note: shadcn uses size=icon; our wrapper uses :icon."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :outline
                                                      :size :icon
                                                      :aria-label "Open"}
                                                     [:> ArrowUpRight])]))

(defscene
  button-with-icon
  "Button with leading icon and label.

  Radix primitive: @radix-ui/react-slot

  Icons should be placed before text for consistent alignment."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :outline
                                                      :size :sm}
                                                     [:> GitBranch]
                                                     "New branch")]))

(defscene
  button-loading
  "Loading button with spinner and disabled state.

  Radix primitive: @radix-ui/react-slot

  Use a spinner + disabled to communicate in-progress actions."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :outline
                                                      :size :sm
                                                      :disabled true}
                                                     [spinner/spinner {:class "size-4"}]
                                                     "Submitting")]))

(defscene
  button-as-child
  "Polymorphic rendering via :as-child.

  Radix primitive: @radix-ui/react-slot

  Use :as-child to render as an anchor while preserving button styles."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:as-child true}
                                                     [:a {:href "#"}
                                                      "Go to login"])]))

(defscene
  button-size
  "Size variants for compact or prominent buttons.

  Radix primitive: @radix-ui/react-slot

  Our wrapper supports :xs, :sm, :default, :lg, and icon sizes (:icon, :icon-xs, :icon-sm, :icon-lg)."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex flex-wrap items-center gap-3"}
                                      (button/button {:variant :outline
                                                      :size :xs}
                                                     "XS")
                                      (button/button {:variant :outline
                                                      :size :sm}
                                                     "Small")
                                      (button/button {:variant :outline} "Default")
                                      (button/button {:variant :outline
                                                      :size :lg}
                                                     "Large")
                                      (button/button {:variant :outline
                                                      :size :icon
                                                      :aria-label "Open in new tab"}
                                                     [:> ArrowUpRight])]))

(defscene
  button-rounded
  "Rounded icon button for floating actions.

  Radix primitive: @radix-ui/react-slot

  Add a custom :class to achieve rounded-full styling."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:variant :outline
                                                      :size :icon
                                                      :class "rounded-full"
                                                      :aria-label "Move up"}
                                                     [:> ArrowUpRight])]))

(defscene
  button-xs-size
  "Extra-small button size for dense interfaces.

  Aligned with the current shadcn style profile.
  Use :size :xs for compact toolbars or inline actions."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      (button/button {:size :xs
                                                      :variant :secondary}
                                                     "Compact action")]))
