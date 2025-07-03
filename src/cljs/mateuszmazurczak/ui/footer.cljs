(ns mateuszmazurczak.ui.footer
  "Frontend based implementation of footer"
  (:require
   [cljs-time.core            :as cljs-time]
   [mateuszmazurczak.ui.icons :as icons]))

(defn footer
  []
  (let [dark? false]
    [:footer
     {:class
      ["footer sm:footer-horizontal bg-white text-neutral-content items-center justify-end p-4"]}
     [:aside
      [:div {:class ["flex justify-center space-x-10 relative"]}
       [icons/icon {:path-kw :svg/youtube
                    :size 1.5
                    :href "https://www.youtube.com/@kaspazza7501"
                    :dark? dark?}]
       [icons/icon {:path-kw :svg/linkedin
                    :size 1.5
                    :href "https://www.linkedin.com/in/mateuszmazurczak/"
                    :dark? dark?}]
       [icons/icon {:path-kw :svg/github
                    :size 1.5
                    :href "https://github.com/kaspazza"
                    :dark? dark?}]]]]))
