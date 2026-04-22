(ns mateuszmazurczak.portfolio.ui-components.avatar
  (:require
   ["lucide-react"                        :refer [Plus]]
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.avatar :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Avatar"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Avatar component based on Radix UI primitives."
            :npm-install "npm install @radix-ui/react-avatar"
            :source-code (embed-source "mateuszmazurczak.ui.components.avatar")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/avatar.cljs"
            :filename "avatar.cljs"}])

(defscene
 api-reference
 "Complete reference for all Avatar component props, default classes, and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Avatar components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar"
       :description "Root avatar container with size variants and optional status badge support."
       :props [{:name ":size"            :type "keyword"      :default nil :description "Size variant (:default, :sm, :lg)"}
               {:name ":class"          :type "string"       :default nil :description "Additional Tailwind classes"}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to root container."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-image"
       :description "Displays the avatar image."
       :props [{:name ":src"   :type "string" :default nil :description "Image source URL"}
               {:name ":alt"   :type "string" :default nil :description "Alt text for accessibility"}
               {:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-fallback"
       :description "Fallback content when image unavailable."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-badge"
       :description
       "Badge indicator positioned at bottom right of avatar. Automatically sizes based on parent avatar size."
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Always provide meaningful :alt text for avatar-image when identity matters."]
       [:li "Use avatar-fallback for resilient UX in slow or failed image loading states."]
       [:li "avatar-badge is size-aware relative to parent avatar; avoid manual absolute positioning overrides."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code
        "[avatar {:size :lg}\n"
        "  [avatar-image {:src \"https://github.com/user.png\"\n"
        "                 :alt \"@user\"}]\n"
        "  [avatar-fallback {}\n"
        "   \"UN\"]\n"
        "  [avatar-badge {:class \"bg-green-600\"}]]"]]]]]]))

(defscene
 avatar-demo
 "Avatar with image, fallback, and grouped stack.

  Radix primitive: @radix-ui/react-avatar

  Use fallbacks for initials or offline states."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 flex flex-wrap items-center gap-12"}
   [sut/avatar {}
    [sut/avatar-image {:src "https://github.com/shadcn.png"
                       :alt "@shadcn"}]
    [sut/avatar-fallback {}
     "CN"]]
   [sut/avatar {:class "rounded-lg"}
    [sut/avatar-image {:src "https://github.com/evilrabbit.png"
                       :alt "@evilrabbit"}]
    [sut/avatar-fallback {}
     "ER"]]
   [:div {:class "flex -space-x-2"}
    [sut/avatar {:class "ring-2 ring-background grayscale"}
     [sut/avatar-image {:src "https://github.com/shadcn.png"
                        :alt "@shadcn"}]
     [sut/avatar-fallback {}
      "CN"]]
    [sut/avatar {:class "ring-2 ring-background grayscale"}
     [sut/avatar-image {:src "https://github.com/maxleiter.png"
                        :alt "@maxleiter"}]
     [sut/avatar-fallback {}
      "LR"]]
    [sut/avatar {:class "ring-2 ring-background grayscale"}
     [sut/avatar-image {:src "https://github.com/evilrabbit.png"
                        :alt "@evilrabbit"}]
     [sut/avatar-fallback {}
      "ER"]]]]))

(defscene
 empty-avatar
 "Avatar-only empty state.

  Radix primitive: @radix-ui/react-avatar

  Use a grayscale avatar as the empty media."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/avatar {:size :lg}
                                      [sut/avatar-image {:src "https://github.com/shadcn.png"
                                                         :alt "@shadcn"
                                                         :class "grayscale"}]
                                      [sut/avatar-fallback {}
                                       "LR"]]]))

(defscene
 empty-avatar-group
 "Stacked avatar group for empty states.

  Radix primitive: @radix-ui/react-avatar

  Use stacked avatars to represent teams or groups."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 flex -space-x-2"}
   [sut/avatar {:size :lg
                :class "ring-2 ring-background grayscale"}
    [sut/avatar-image {:src "https://github.com/shadcn.png"
                       :alt "@shadcn"}]
    [sut/avatar-fallback {}
     "CN"]]
   [sut/avatar {:size :lg
                :class "ring-2 ring-background grayscale"}
    [sut/avatar-image {:src "https://github.com/maxleiter.png"
                       :alt "@maxleiter"}]
    [sut/avatar-fallback {}
     "LR"]]
   [sut/avatar {:size :lg
                :class "ring-2 ring-background grayscale"}
    [sut/avatar-image {:src "https://github.com/evilrabbit.png"
                       :alt "@evilrabbit"}]
    [sut/avatar-fallback {}
     "ER"]]]))

(defscene
 avatar-custom-sizes
 "Avatar size variants.

  Radix primitive: @radix-ui/react-avatar

  Use the :size prop for consistent sizing."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-4"}
                                     [sut/avatar {:size :sm}
                                      [sut/avatar-fallback {}
                                       "SM"]]
                                     [sut/avatar {:size :default}
                                      [sut/avatar-fallback {}
                                       "MD"]]
                                     [sut/avatar {:size :lg}
                                      [sut/avatar-fallback {}
                                       "LG"]]]))

(defscene
 avatar-with-badge
 "Avatar with status badge.

  Radix primitive: @radix-ui/react-avatar

  Use badge to indicate online/offline status or other states."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/avatar {}
                                      [sut/avatar-image {:src "https://github.com/shadcn.png"
                                                         :alt "@shadcn"}]
                                      [sut/avatar-fallback {}
                                       "CN"]
                                      [sut/avatar-badge {:class
                                                         "bg-green-600 dark:bg-green-800"}]]]))

(defscene
 avatar-badge-with-icon
 "Avatar with badge containing an icon.

  Radix primitive: @radix-ui/react-avatar

  Use icon inside badge for actions or enhanced status indicators."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/avatar {:class "grayscale"}
                                      [sut/avatar-image {:src "https://github.com/pranathip.png"
                                                         :alt "@pranathip"}]
                                      [sut/avatar-fallback {}
                                       "PP"]
                                      [sut/avatar-badge {}
                                       [:> Plus]]]]))
