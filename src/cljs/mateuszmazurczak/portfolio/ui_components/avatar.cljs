(ns mateuszmazurczak.portfolio.ui-components.avatar
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.avatar :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Avatar"})

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
                                     [sut/avatar {:class "size-12"}
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
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex -space-x-2"}
                                     [sut/avatar {:class "size-12 ring-2 ring-background grayscale"}
                                      [sut/avatar-image {:src "https://github.com/shadcn.png"
                                                         :alt "@shadcn"}]
                                      [sut/avatar-fallback {}
                                       "CN"]]
                                     [sut/avatar {:class "size-12 ring-2 ring-background grayscale"}
                                      [sut/avatar-image {:src "https://github.com/maxleiter.png"
                                                         :alt "@maxleiter"}]
                                      [sut/avatar-fallback {}
                                       "LR"]]
                                     [sut/avatar {:class "size-12 ring-2 ring-background grayscale"}
                                      [sut/avatar-image {:src "https://github.com/evilrabbit.png"
                                                         :alt "@evilrabbit"}]
                                      [sut/avatar-fallback {}
                                       "ER"]]]))

(defscene
 avatar-custom-sizes
 "Custom avatar sizes.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-avatar

  Adjust size via class overrides."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-4"}
                                     [sut/avatar {:class "h-8 w-8"}
                                      [sut/avatar-fallback {}
                                       "SM"]]
                                     [sut/avatar {:class "h-12 w-12"}
                                      [sut/avatar-fallback {}
                                       "MD"]]
                                     [sut/avatar {:class "h-16 w-16"}
                                      [sut/avatar-fallback {}
                                       "LG"]]]))