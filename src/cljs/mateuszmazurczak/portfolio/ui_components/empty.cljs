(ns mateuszmazurczak.portfolio.ui-components.empty
  (:require
   ["lucide-react"                        :refer
                                          [ArrowUpRight Bell Bookmark Heart Inbox Plus RefreshCcw]]
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.avatar :as avatar]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.empty  :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Empty State"})

(defscene
 empty-demo
 "Primary empty state with actions.

  Based on shadcn/ui Empty — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Use primary + outline actions for next steps."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Inbox]]
     [sut/empty-title {}
      "No Projects Yet"]
     [sut/empty-description {}
      "You haven't created any projects yet. Get started by creating your first project."]]
    [sut/empty-content {}
     [:div {:class "flex gap-2"}
      (button/button {} "Create Project")
      (button/button {:variant :outline} "Import Project")]]
    (button/button {:variant :link
                    :as-child true
                    :class "text-muted-foreground"
                    :size :sm}
                   [:a {:href "#"}
                    "Learn More "
                    [:> ArrowUpRight]])]]))

(defscene
 empty-icon
 "Grid of empty states with icons.

  Based on shadcn/ui Empty — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Useful for showcasing multiple empty modules."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 grid gap-8 md:grid-cols-2"}
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Inbox]]
     [sut/empty-title {}
      "No messages"]
     [sut/empty-description {}
      "Your inbox is empty. New messages will appear here."]]]
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Heart]]
     [sut/empty-title {}
      "No likes yet"]
     [sut/empty-description {}
      "Content you like will be saved here for easy access."]]]
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Bookmark]]
     [sut/empty-title {}
      "No bookmarks"]
     [sut/empty-description {}
      "Save interesting content by bookmarking it."]]]
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Bell]]
     [sut/empty-title {}
      "No notifications"]
     [sut/empty-description {}
      "You're all caught up. New notifications will appear here."]]]]))

(defscene
 empty-outline
 "Outlined empty state variant.

  Based on shadcn/ui Empty — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Use a dashed border to emphasize the empty container."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {:class "border border-dashed"}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Bell]]
     [sut/empty-title {}
      "Cloud Storage Empty"]
     [sut/empty-description {}
      "Upload files to your cloud storage to access them anywhere."]]
    [sut/empty-content {}
     (button/button {:variant :outline
                     :size :sm}
                    "Upload Files")]]]))

(defscene
 empty-avatar
 "Empty state with an avatar media.

  Based on shadcn/ui Empty Avatar — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Use avatar media for user-centric empty states."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :default}
      [avatar/avatar {:class "size-12"}
       [avatar/avatar-image {:src "https://github.com/shadcn.png"
                             :class "grayscale"}]
       [avatar/avatar-fallback {}
        "LR"]]]
     [sut/empty-title {}
      "User Offline"]
     [sut/empty-description {}
      "This user is currently offline. You can leave a message or try again later."]]
    [sut/empty-content {}
     (button/button {:size :sm} "Leave Message")]]]))

(defscene
 empty-background
 "Empty state with muted background.

  Based on shadcn/ui Empty — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Gradient backgrounds help differentiate the empty section."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {:class "from-muted/50 to-background bg-gradient-to-b from-30%"}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Bell]]
     [sut/empty-title {}
      "No Notifications"]
     [sut/empty-description {}
      "You're all caught up. New notifications will appear here."]]
    [sut/empty-content {}
     (button/button {:variant :outline
                     :size :sm}
                    [:> RefreshCcw]
                    "Refresh")]]]))

(defscene
 empty-avatar-group
 "Empty state with avatar group.

  Based on shadcn/ui Empty Avatar Group — https://ui.shadcn.com/docs/components/empty
  Custom component built for empty or zero states.

  Use grouped avatars for team invites or collaboration prompts."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {}
      [:div {:class "flex -space-x-2"}
       [avatar/avatar {:class "size-12 ring-2 ring-background grayscale"}
        [avatar/avatar-image {:src "https://github.com/shadcn.png"
                              :alt "@shadcn"}]
        [avatar/avatar-fallback {}
         "CN"]]
       [avatar/avatar {:class "size-12 ring-2 ring-background grayscale"}
        [avatar/avatar-image {:src "https://github.com/maxleiter.png"
                              :alt "@maxleiter"}]
        [avatar/avatar-fallback {}
         "LR"]]
       [avatar/avatar {:class "size-12 ring-2 ring-background grayscale"}
        [avatar/avatar-image {:src "https://github.com/evilrabbit.png"
                              :alt "@evilrabbit"}]
        [avatar/avatar-fallback {}
         "ER"]]]]
     [sut/empty-title {}
      "No Team Members"]
     [sut/empty-description {}
      "Invite your team to collaborate on this project."]]
    [sut/empty-content {}
     (button/button {:size :sm} [:> Plus] "Invite Members")]]]))