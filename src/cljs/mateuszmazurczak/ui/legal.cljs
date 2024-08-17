(ns mateuszmazurczak.ui.legal
  "Namespace gathering all legal documents related components."
  (:require
   [automaton-web.components.section :as web-section]
   [mateuszmazurczak.i18n.fe.translate        :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes                   :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.fe-navigation         :as mateuszmazurczak-fe-nav]
   [mateuszmazurczak.ui.navigation            :as mateuszmazurczak-navigation]))

(defn disclaimer
  []
  (web-section/text-section
   {:title (mateuszmazurczak-fe-translate/tr :disclaimer)
    :description (mateuszmazurczak-fe-translate/tr :disclaimer-content)
    :pre-title [mateuszmazurczak-navigation/back-navigation
                {:href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/home)
                 :text (mateuszmazurczak-fe-translate/tr :back-home)}]}))

(defn privacy
  []
  (let [number-dot-text
        (fn [number text]
          (mateuszmazurczak-fe-translate/tr
           :number-dot-text
           number
           (if (keyword? text) (mateuszmazurczak-fe-translate/tr text) text)))
        text-number (fn [number text]
                      (mateuszmazurczak-fe-translate/tr :text-number
                                               (mateuszmazurczak-fe-translate/tr text)
                                               number))]
    (web-section/text-section
     {:title (mateuszmazurczak-fe-translate/tr :privacy-policy)
      :description (mateuszmazurczak-fe-translate/tr :privacy-policy-description)
      :pre-title [mateuszmazurczak-navigation/back-navigation
                  {:href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/home)
                   :text (mateuszmazurczak-fe-translate/tr :back-home)}]}
     {:title (number-dot-text "1" :gdpr)
      :description (mateuszmazurczak-fe-translate/tr :hephaistox-gdpr-compliant)}
     {:title (number-dot-text "2" :personal-data)}
     {:title (number-dot-text "2.1" :personal-data-you-communicate)
      :description (mateuszmazurczak-fe-translate/tr :what-data-we-collect)}
     {:title (number-dot-text "2.2" :ways-we-collect-data)
      :description (mateuszmazurczak-fe-translate/tr :when-you-provide-personal-data)}
     {:title (number-dot-text "3" :cookies)
      :description (mateuszmazurczak-fe-translate/tr :hephaistox-cookie-policy)}
     {:title (number-dot-text "4" :processing-purposes)
      :description (mateuszmazurczak-fe-translate/tr
                    :hephaistox-personal-data-collection)}
     {:title (number-dot-text "4.1" (text-number "1" :category))
      :description (mateuszmazurczak-fe-translate/tr
                    :hephaistox-personal-data-anonymous)}
     {:title (number-dot-text "4.2" (text-number "2" :category))
      :description (mateuszmazurczak-fe-translate/tr :hephaistox-managing-contact)}
     {:title (number-dot-text "5" :information-protection)
      :description (mateuszmazurczak-fe-translate/tr :hephaistox-information-protection)}
     {:title ""
      :description (mateuszmazurczak-fe-translate/tr :you-not-need-to-give-us-data)}
     {:title (mateuszmazurczak-fe-translate/tr :external-services)
      :description (mateuszmazurczak-fe-translate/tr :hephaistox-external-services-used)}
     {:title (mateuszmazurczak-fe-translate/tr :further-questions)
      :description (mateuszmazurczak-fe-translate/tr
                    :hephaistox-further-questions-contact-us)})))
