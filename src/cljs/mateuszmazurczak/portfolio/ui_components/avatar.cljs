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
       :props [[":size" "keyword, optional - Size variant (:default, :sm, :lg)"]
               [":class" "string, optional - Additional Tailwind classes"]
               ["additional props" "map entries, optional - Forwarded to root container."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-image"
       :description "Displays the avatar image."
       :props [[":src" "string, required - Image source URL"]
               [":alt" "string, optional - Alt text for accessibility"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-fallback"
       :description "Fallback content when image unavailable."
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "avatar-badge"
       :description
       "Badge indicator positioned at bottom right of avatar. Automatically sizes based on parent avatar size."
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
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

  Based on shadcn/ui Avatar — https://ui.shadcn.com/docs/components/avatar
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

  Based on shadcn/ui Empty Avatar — https://ui.shadcn.com/docs/components/empty
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

  Based on shadcn/ui Empty Avatar Group — https://ui.shadcn.com/docs/components/empty
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

  Custom example — not from shadcn/ui.
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

  Based on shadcn/ui Avatar Badge — https://ui.shadcn.com/docs/components/avatar
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

  Based on shadcn/ui Avatar Badge — https://ui.shadcn.com/docs/components/avatar
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
