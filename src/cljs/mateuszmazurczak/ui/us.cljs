(ns mateuszmazurczak.ui.us
  "Section for displaying information about our core team (founders)."
  (:require
   [automaton-web.components.section :as web-section]
   [automaton-web.react-proxy        :as web-react]
   [mateuszmazurczak.i18n.fe.translate        :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.team                     :as lt]
   [mateuszmazurczak.ui.article               :as mateuszmazurczak-ui-article]))

(defn about-us-section
  [{:keys [dark?]}]
  (let [current-founder (web-react/ratom :none)]
    (fn [] [web-section/section-clickable-cards-modal
            {:size :sm
             :section {:title (mateuszmazurczak-fe-translate/tr :about-us)
                       :description (mateuszmazurczak-fe-translate/tr
                                     :about-us-description)}
             :cards (reduce (fn [acc founder]
                              (let [updated-founder
                                    (-> founder
                                        (update :title mateuszmazurczak-fe-translate/tr)
                                        (update :description
                                                mateuszmazurczak-fe-translate/tr))]
                                (conj acc updated-founder)))
                            []
                            lt/founders)
             :dark? dark?
             :modal-id "founder-details-modal"
             :change-card-fn #(reset! current-founder %)
             :current-card @current-founder}])))

(defn about-us-detailed [] [mateuszmazurczak-ui-article/article-page :about-us-page])
