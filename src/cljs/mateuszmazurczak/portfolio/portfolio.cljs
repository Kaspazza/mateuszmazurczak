(ns mateuszmazurczak.portfolio.portfolio
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.portfolio.mateuszmazurczak.home]))

(defonce app
  (web-proxy/start!
   {:config {:css-paths ["/css/compiled/styles.css"]
             :viewport/options [{:title "<sm | small phone"
                                 :value {:viewport/width 390
                                         :viewport/height 640}}
                                {:title "Tailwind sm (iPhone)"
                                 :value {:viewport/width 640
                                         :viewport/height 1136}}
                                {:title "Tailwind md (iPad Mini)"
                                 :value {:viewport/width 768
                                         :viewport/height 1024}}
                                {:title "Tailwind lg (iPad Pro)"
                                 :value {:viewport/width 1024
                                         :viewport/height 1366}}
                                {:title "Tailwind xl (laptop)"
                                 :value {:viewport/width 1280
                                         :viewport/height 800}}
                                {:title "Tailwind 2xl (Macbook Pro)"
                                 :value {:viewport/width 1536
                                         :viewport/height 960}}
                                {:title ">2xl | Big monitor"
                                 :value {:viewport/width 2560
                                         :viewport/height 1440}}
                                {:title "Auto"
                                 :value {:viewport/width "100%"
                                         :viewport/height "100%"}}]
             ;; Default - tailwind sm
             :viewport/defaults {:viewport/width 640
                                 :viewport/height 1136}}
    :index (web-proxy/search-index)}))

(defn init [] app)

(web-proxy/register-collection! :mateuszmazurczak {:title "Mateuszmazurczak"})
