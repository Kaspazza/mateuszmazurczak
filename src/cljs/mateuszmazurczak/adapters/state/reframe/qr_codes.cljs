(ns mateuszmazurczak.adapters.state.reframe.qr-codes
  "Re-frame subscriptions for QR codes page state."
  (:require
   [mateuszmazurczak.application.qr-codes.page-data :as page-data]
   [mateuszmazurczak.domain.state.registry          :as state-registry]
   [re-frame.core                                   :as rf]))

(rf/reg-sub :qr-codes/raw-data (fn [db _] (get-in db state-registry/*qr-codes-page-path*)))

(rf/reg-sub :pages/qr-codes
            :<-
            [:qr-codes/raw-data]
            (fn [raw-data _] (page-data/prepare-ui-data raw-data)))

(def watch "QR codes page subscriptions for re-frame." #{:qr-codes/raw-data :pages/qr-codes})
