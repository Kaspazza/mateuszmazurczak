(ns mateuszmazurczak.i18n.events
  (:require
   [day8.re-frame.tracing                  :refer-macros [fn-traced]]
   [mateuszmazurczak.i18n.language         :as mm-language]
   [mateuszmazurczak.i18n.language.web     :as mm-i18n-lang-web]
   [mateuszmazurczak.navigation.history    :as mm-fe-history]
   [mateuszmazurczak.navigation.history-fx :as mm-fe-fx]
   [mateuszmazurczak.utils.cookies         :as mm-cookies]
   [re-frame.core                          :as rf]))

(rf/reg-sub ::lang (fn [db _] (:lang db)))

(rf/reg-fx ::set-cookie
           (fn [[key lang-id]]
             (let [saved-value (-> (mm-i18n-lang-web/get-web-lang lang-id)
                                   :ui-text)]
               (mm-cookies/set-cookie key saved-value))))

(rf/reg-event-fx ::change-lang
                 (fn-traced [{:keys [db]} [_ lang-evt]]
                            (let [lang (-> lang-evt
                                           .-target
                                           .-value
                                           mm-language/ui-str-to-id)]
                              {:db (assoc db :lang lang)
                               ::set-cookie ["lang" lang]
                               ::mm-fe-fx/history-change
                               [(mm-fe-history/href-delta (:route-match db)
                                                          nil
                                                          {:lang lang})]})))
