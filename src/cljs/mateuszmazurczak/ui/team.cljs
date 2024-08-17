(ns mateuszmazurczak.ui.team
  "Team section is here for convincing customer of our seriousness (social proof) and proof of competencies.
  We are prepared and capable of fulfilling our promises.
  So that page list their picture and external link to LinkedIn"
  (:require
   [automaton-web.components.icons   :as web-icons]
   [automaton-web.components.section :as web-section]
   [automaton-web.react-proxy        :as web-react]
   [mateuszmazurczak.i18n.fe.translate        :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.team                     :as lt]
   [mateuszmazurczak.ui.article               :as mateuszmazurczak-ui-article]))

(defn moonshot-topics
  [moonshot-subjects]
  [:ul {:role "list"
        :class ["mt-8 max-w-xl space-y-8 text-gray-600"]}
   (doall (for [{:keys [description title]} moonshot-subjects]
            ^{:key (str "moonshot-" title description)}
            [:li {:class ["flex gap-x-3"]}
             [:div {:class ["mt-0 5"]}
              [web-icons/icon {:path-kw :svg/check-circle}]]
             [:div
              [:div {:class ["font-semibold text-gray-900"]}
               (mateuszmazurczak-fe-translate/tr title)]
              (when description
                [:div (mateuszmazurczak-fe-translate/tr description)])]]))])

(defn hephaistox-role-person-details
  [name person-role-title hephaistox-title hephaistox-role]
  {:id (str "heph-role" name person-role-title)
   :title [:span
           (mateuszmazurczak-fe-translate/tr person-role-title)
           " - "
           (mateuszmazurczak-fe-translate/tr hephaistox-title)]
   :description (mateuszmazurczak-fe-translate/tr hephaistox-role)})

(defn moonshot-subjects-person-details
  [name person-moonshot-title moonshot-subjects]
  {:id (str "moonshot" name person-moonshot-title)
   :title (mateuszmazurczak-fe-translate/tr person-moonshot-title)
   :description [moonshot-topics moonshot-subjects]})

(defn our-team-section
  [{:keys [dark?]}]
  (let [current-expert (web-react/ratom {:expert :none})]
    (fn []
      [web-section/section-clickable-cards-modal
       {:dark? dark?
        :current-card (merge @current-expert
                             {:sections
                              [(when (:hephaistox-role @current-expert)
                                 (hephaistox-role-person-details
                                  (:name @current-expert)
                                  :role-in-hephaistox
                                  (:hephaistox-title @current-expert)
                                  (:hephaistox-role @current-expert)))
                               (when (:moonshot-subjects @current-expert)
                                 (moonshot-subjects-person-details
                                  (:name @current-expert)
                                  :moonshot-topics
                                  (:moonshot-subjects @current-expert)))]})
        :change-card-fn #(reset! current-expert %)
        :size :md
        :section {:title (mateuszmazurczak-fe-translate/tr :our-network)
                  :description (mateuszmazurczak-fe-translate/tr
                                :our-network-description)}
        :modal-id "person-details-modal"
        :cards (reduce (fn [acc expert]
                         (if (not (:hidden? expert))
                           (let [updated-expert
                                 (-> expert
                                     (update :title mateuszmazurczak-fe-translate/tr)
                                     (update :description
                                             mateuszmazurczak-fe-translate/tr))]
                             (conj acc updated-expert))
                           acc))
                       []
                       lt/teammates)}])))

(defn network-detailed [] [mateuszmazurczak-ui-article/article-page :network-page])
