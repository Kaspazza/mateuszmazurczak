(ns mateuszmazurczak.portfolio.ui-components.avatar
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.avatar  :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Avatar"})

(defscene
 basic-avatars
 "Avatar with image and fallback initials."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex items-center gap-6 p-6"}
   [sut/avatar {}
    [sut/avatar-image {:src "https://avatars.githubusercontent.com/u/1?v=4"
                       :alt "Octocat"}]
    [sut/avatar-fallback {} "OC"]]
   [sut/avatar {:class "h-12 w-12"}
    [sut/avatar-fallback {:class "bg-primary text-primary-foreground"} "JD"]]]))