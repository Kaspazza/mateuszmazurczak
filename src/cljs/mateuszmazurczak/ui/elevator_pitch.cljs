(ns mateuszmazurczak.ui.elevator-pitch
  (:require
   [automaton-web.components.section :as web-section]
   [mateuszmazurczak.i18n.fe.translate        :as mateuszmazurczak-fe-translate]))

(defn elevator-pitch-section
  [{:keys [dark?]}]
  [web-section/section-full-container {:dark? dark?}
   [web-section/section-text-video
    {:title (mateuszmazurczak-fe-translate/tr :hephaistox-offer)
     :subtitle "Anthony Caumond"
     :text (mateuszmazurczak-fe-translate/tr :hephaistox-co-founder)
     :dark? dark?
     :btn-props {:text (mateuszmazurczak-fe-translate/tr :book-call-person "Anthony")}
     :btn-link
     "https://outlook.office365.com/owa/calendar/MatiAnthony@hephaistox.com/bookings/s/jCJNJ2lYPUmwF_13sgEmng2"
     :video-src (mateuszmazurczak-fe-translate/tr :elevator-pitch)}]])
