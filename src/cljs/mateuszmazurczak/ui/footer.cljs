(ns mateuszmazurczak.ui.footer
  "Frontend based implementation of footer"
  (:require
   [automaton-core.utils.map          :as utils-map]
   [automaton-web.components.footer   :as web-footer]
   [automaton-web.components.icons    :as web-icons]
   [automaton-web.events-proxy        :as web-events-proxy]
   [automaton-web.events.subs         :as web-subs]
   [automaton-web.utils.date          :as web-date]
   [mateuszmazurczak.i18n.translate   :as mm-i18n-translate]
   [mateuszmazurczak.navigation.utils :as mm-nav]
   [mateuszmazurczak.routes           :as mm-routes]))

(def social-networks
  {"youtube" [web-icons/icon {:path-kw :svg/youtube
                              :size 1.2
                              :href "https://www.youtube.com/kaspazza"}]
   "linkedin" [web-icons/icon {:path-kw :svg/linkedin
                               :size 1.2
                               :href
                               "https://www.linkedin.com/in/mateuszmazurczak/"}]
   "github" [web-icons/icon {:path-kw :svg/github
                             :size 1.2
                             :href "https://github.com/kaspazza"}]})

(defn footer
  []
  (let [title (str "Mateusz Mazurczak © 2023-" (web-date/this-year))
        dark? true
        current-route (get-in (web-events-proxy/subscribe-value
                               [::web-subs/route-match])
                              [:data :name])
        footer-data
        (-> [{:title :homepage
              :href ::mm-routes/home
              :disabled? (= current-route ::mm-routes/home)}
             {:title :privacy-policy
              :href ::mm-routes/privacy
              :disabled? (= current-route ::mm-routes/privacy)}
             {:title :disclaimer
              :href ::mm-routes/disclaimer
              :disabled? (= current-route ::mm-routes/disclaimer)}]
            (utils-map/apply-to-keys (fn [_m _k v] (mm-i18n-translate/tr v))
                                     :title)
            (utils-map/apply-to-keys (fn [_m _k v] (mm-nav/href-delta v))
                                     :href))]
    [:div {:class ["px-6 py-2 sm:py-4 lg:px-8"
                   (if dark? "bg-theme-dark" "bg-theme-light")]}
     [web-footer/simple-footer {:title title
                                :social-networks social-networks
                                :footer-lists footer-data
                                :dark? dark?}]]))
