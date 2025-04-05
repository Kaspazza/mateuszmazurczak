(ns mateuszmazurczak.ui.footer
  "Frontend based implementation of footer"
  (:require
   [cljs-time.core            :as cljs-time]
   [mateuszmazurczak.ui.icons :as icons]))

(defn footer
  []
  (let [title (str "Mateusz Mazurczak © " (cljs-time/year (cljs-time/now)))
        dark? true]
    [:footer {:class ["footer footer-center text-base-content p-4"
                      "bg-neutral"]}
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
                    :dark? dark?}]]
      [:p {:class ["text-base-200"]}
       title]]]))
