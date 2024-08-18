(ns mateuszmazurczak.ui.footer
  "Frontend based implementation of footer"
  (:require
   [automaton-core.utils.map           :as utils-map]
   [automaton-web.components.footer    :as web-footer]
   [automaton-web.components.icons     :as web-icons]
   [automaton-web.events-proxy         :as web-events-proxy]
   [automaton-web.events.subs          :as web-subs]
   [automaton-web.utils.date           :as bud]
   [mateuszmazurczak.i18n.fe.translate :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes            :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.fe-navigation  :as mateuszmazurczak-fe-nav]))

(def social-networks
  {"youtube" [web-icons/icon {:path-kw :svg/youtube
                              :size 1.2
                              :href "https://www.youtube.com/@Hephaistoxsc"}]
   "linkedin" [web-icons/icon {:path-kw :svg/linkedin
                               :size 1.2
                               :href
                               "https://www.linkedin.com/company/hephaistox"}]
   "github" [web-icons/icon {:path-kw :svg/github
                             :size 1.2
                             :href "https://github.com/hephaistox"}]})

(defn footer
  []
  (let [title (str "Hephaistox ©" (bud/this-year))
        dark? true
        current-route (get-in (web-events-proxy/subscribe-value
                               [::web-subs/route-match])
                              [:data :name])
        footer-data
        (-> [{:title :homepage
              :href ::mateuszmazurczak-routes/home
              :disabled? (= current-route ::mateuszmazurczak-routes/home)}
             {:title :privacy-policy
              :href ::mateuszmazurczak-routes/privacy
              :disabled? (= current-route ::mateuszmazurczak-routes/privacy)}
             {:title :disclaimer
              :href ::mateuszmazurczak-routes/disclaimer
              :disabled? (= current-route
                            ::mateuszmazurczak-routes/disclaimer)}]
            (utils-map/apply-to-keys (fn [_m _k v]
                                       (mateuszmazurczak-fe-translate/tr v))
                                     :title)
            (utils-map/apply-to-keys (fn [_m _k v]
                                       (mateuszmazurczak-fe-nav/href-delta v))
                                     :href))]
    [:div {:class ["px-6 py-2 sm:py-4 lg:px-8"
                   (if dark? "bg-theme-dark" "bg-theme-light")]}
     [web-footer/simple-footer {:title title
                                :social-networks social-networks
                                :footer-lists footer-data
                                :dark? dark?}]]))
