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
            :source-code (embed-source mateuszmazurczak.ui.components.button)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/button.cljs"
            :filename "button.cljs"}])

(defscene
 api-reference
 "Complete reference for all Button component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Button components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "button"
       :description "Button component"
       :props
       [[":variant"
         "keyword, optional (default :default). One of: :default | :destructive | :outline | :secondary | :ghost | :link"]
        [":size" "keyword, optional (default :default). One of: :default | :sm | :lg | :icon"]
        [":class" "string, optional - Additional Tailwind classes"]
        [":as-child" "boolean, optional (default false) - Render via Radix Slot"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[button {}]"]]]]]]))

(defscene
 button-default
 "Default button style for primary actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot (for :as-child polymorphism)

  Our CLJS wrapper uses keyword props (:variant, :size) instead of
  string variants. The default variant is :default."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {} "Continue")]))

(defscene
 button-destructive
 "Destructive button for dangerous actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Use :destructive for irreversible actions (delete, remove, etc.)."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:variant :destructive} "Delete project")]))

(defscene
 button-outline
 "Outlined button for secondary actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Outline buttons are visually lighter but still prominent."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:variant :outline} "View details")]))

(defscene
 button-secondary
 "Secondary button for neutral actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Use :secondary to de-emphasize a primary action."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:variant :secondary} "Secondary action")]))

(defscene
 button-ghost
 "Ghost button for low-emphasis actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Ghost buttons are useful in dense toolbars."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:variant :ghost} "Dismiss")]))

(defscene
 button-link
 "Link-styled button for inline actions.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Use :link when you want a textual action that still behaves like a button."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:variant :link} "Learn more")]))

(defscene
 button-icon
 "Icon-only button for compact controls.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
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

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
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

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
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

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
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

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot

  Our wrapper supports :sm, :default, :lg, and :icon sizes."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex flex-wrap items-center gap-3"}
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

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
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

  Custom extension — not part of shadcn/ui.
  Our wrapper adds :xs for compact toolbars or inline actions."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     (button/button {:size :xs
                                                     :variant :secondary}
                                                    "Compact action")]))