(ns mateuszmazurczak.navigation.history-fx
  (:require
   [mateuszmazurczak.navigation.history          :as mm-fe-history]
   [mateuszmazurczak.navigation.history.protocol :as mm-nav-hist]
   [re-frame.core                                :as rf]))

(rf/reg-fx ::history-change
           (fn [[href]] (mm-nav-hist/navigate! @mm-fe-history/history href)))
