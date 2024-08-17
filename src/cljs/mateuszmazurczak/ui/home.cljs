(ns mateuszmazurczak.ui.home
  "Customer app home page assembly"
  (:require
   [automaton-web.components.init-components :as web-init-components]
   #_[landing.ui.downloadable-materials :as lcdm]))

(defn mateuszmazurczak-page
  [{:keys [_document]}]
  [:div {:class ["flex flex-col"]}
   "hello world"])

(defn home
  "Functional component for displaying landing page sections."
  []
  [:f> #(let [_ (web-init-components/init-rendering)] [mateuszmazurczak-page])])
