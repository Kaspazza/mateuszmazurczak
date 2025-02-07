(ns mateuszmazurczak.events.routing
  (:require
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [re-frame.core         :as rf]))

(defn on-element-exist
  "Waits for `selector` element to appear in document and executes `on-exist-fn`"
  [selector on-exist-fn]
  (-> (new js/Promise
           (fn [resolve]
             (when (.querySelector js/document selector)
               (resolve (.querySelector js/document selector)))
             #_{:clj-kondo/ignore [:inline-def]}
             (def observer
               (new js/MutationObserver
                    (fn [_mutations]
                      (when (.querySelector js/document selector)
                        (.disconnect observer)
                        (resolve (.querySelector js/document selector))))))
             (.observe observer
                       (.-body js/document)
                       #js {:childList true
                            :subtree true})))
      (.then on-exist-fn)))

(rf/reg-sub ::route-match (fn [db _] (:route-match db)))

(rf/reg-fx :new-route-scroll-position
           (fn [fragment]
             (if fragment
               (on-element-exist (str "#" fragment) #(.scrollIntoView %))
               (.scrollTo js/window 0 0))))

(rf/reg-event-fx ::new-route-match
                 (fn-traced [{:keys [db]} [_ match]]
                            {:db (assoc db :route-match match)
                             :new-route-scroll-position (:fragment match)}))
