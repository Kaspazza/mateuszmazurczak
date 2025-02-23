(ns mateuszmazurczak.i18n.translate
  "Frontend translation for mateuszmazurczak"
  (:require
   [day8.re-frame.tracing               :refer-macros [fn-traced]]
   [mateuszmazurczak.i18n               :as i18n]
   [mateuszmazurczak.i18n.language      :as i18n-lang]
   [mateuszmazurczak.navigation.history :as mm-fe-history]
   [mateuszmazurczak.navigation.utils   :as mm-nav-utils]
   [mateuszmazurczak.utils.cookies      :as mm-cookies]
   [re-frame.core                       :as rf]))

(rf/reg-sub ::lang (fn [db _] (:lang db)))

(rf/reg-fx ::set-cookie
           (fn [[key lang-id]]
             (let [saved-value (-> (get i18n-lang/web-languages lang-id)
                                   :ui-text)]
               (mm-cookies/set-cookie key saved-value))))

(rf/reg-event-fx ::change-lang
                 (fn-traced [{:keys [db]} [_ lang-evt]]
                            (let [lang (-> lang-evt
                                           .-target
                                           .-value
                                           i18n-lang/ui-str-to-id)]
                              {:db (assoc db :lang lang)
                               ::set-cookie ["lang" lang]
                               ::mm-fe-history/history-change
                               [(mm-fe-history/href-delta (:route-match db)
                                                          nil
                                                          {:lang lang})]})))

(defn- cookies-language
  []
  (-> "lang"
      mm-cookies/get-cookie-val
      i18n-lang/ui-str-to-id))

(defn language-strategy
  "Init the language to start a frontend with."
  []
  (let [par-lang (-> (mm-nav-utils/current-url)
                     mm-nav-utils/lang-in-url-par
                     i18n-lang/ui-str-to-id)]
    (or par-lang (cookies-language) (first i18n-lang/main-langs))))

(defn tr
  "UI component dealing with translation"
  [tr-id]
  (let [lang @(rf/subscribe [::lang])] (i18n/tr lang tr-id)))

(defn tr-for-key
  "Apply tr to each map in the sequence
  Params:
  * `maps` is a sequence of map"
  [maps k]
  (mapv (fn [m] (assoc m k (tr (get m k)))) maps))
