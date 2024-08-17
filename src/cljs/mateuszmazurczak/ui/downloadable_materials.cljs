(ns mateuszmazurczak.ui.downloadable-materials
  (:require
   [automaton-web.components.button    :as web-button]
   [automaton-web.components.mailchimp :as mailchimp]
   [automaton-web.components.modal     :as web-modal]
   [automaton-web.components.section   :as web-section]
   [mateuszmazurczak.i18n.fe.translate          :as mateuszmazurczak-fe-translate]))

(defn downloadable-info
  [{:keys [document dark?]}]
  [web-section/section-full-container {:dark? dark?
                                       :class ["p-16"]}
   [web-section/section-text-button
    {:text (mateuszmazurczak-fe-translate/tr :read-more-about-us?)
     :button (web-modal/wrap-modal-call
              {:modal-id (str mailchimp/mailchimp-newsletter-modal-id)}
              [web-button/button {:text (mateuszmazurczak-fe-translate/tr
                                         :download-documents)}])
     :dark? dark?}]
   [mailchimp/mailchimp-newsletter-modal {:document document}]])
