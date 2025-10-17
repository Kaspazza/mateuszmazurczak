(ns mateuszmazurczak.utils.dom)

(defn delay-page-load
  "Delays so that whole page can load and change height."
  [f]
  (-> (js/Promise. (fn [resolve]
                     (if (= (.-readyState js/document) "complete")
                       (resolve)
                       (.addEventListener js/window "load" resolve))))
      ;; Then wait a frame for all layout calculations
      (.then #(js/requestAnimationFrame f))))

(defn on-element-exist
  "Waits for `selector` element to appear in document and executes `on-exist-fn`."
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
