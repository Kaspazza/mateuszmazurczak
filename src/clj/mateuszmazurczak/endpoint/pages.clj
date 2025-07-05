(ns mateuszmazurczak.endpoint.pages
  "Web handlers implementations"
  (:require
   [clojure.string                       :as str]
   [hiccup2.core                         :as hiccup2]
   [mateuszmazurczak.articles.core       :as articles]
   [mateuszmazurczak.configuration       :as mm-conf]
   [mateuszmazurczak.endpoint.error-page :as error-page]
   [mateuszmazurczak.endpoint.handler    :as handler-utils]
   [mateuszmazurczak.ui.spinner          :as mm-spinner]
   [mateuszmazurczak.utils.fallback      :as fallback]
   [ring.util.http-response              :as http-response]))

(defn article-page
  [{:keys [tr]
    :as http-request}]
  (let [{:keys [path-params path]} (:reitit.core/match http-request)
        article-id (keyword (:article-name path-params))
        {:keys [title author twitter-content img description]
         :as article}
        (articles/article article-id)]
    (if (nil? article)
      (->> (update-in http-request
                      [:header-elements]
                      conj
                      [:script {:type "text/javascript"}
                       (hiccup2/raw (mm-conf/config-web-reference))])
           error-page/not-found-page
           http-response/not-found
           handler-utils/web-page)
      (-> (handler-utils/build
           (merge (update-in http-request
                             [:header-elements]
                             conj
                             [:script {:type "text/javascript"}
                              (hiccup2/raw (mm-conf/config-web-reference))])
                  {:meta-tags
                   {:description (fallback/always-return #(tr description)
                                                         (str description))
                    :image (str "https://mateuszmazurczak.com/"
                                (if img
                                  (fallback/always-return #(tr img) (str img))
                                  "img/preview/en.png"))
                    :title (fallback/always-return #(tr title) (str title))
                    :author (or author "Mateusz Mazurczak")
                    :url (str/join "/" ["https://mateuszmazurczak.com" path])
                    :twitter-content (or twitter-content "sumary_large_image")
                    :type "website"}})
           [:div {:id "app"
                  :class ["h-full"]}
            (mm-spinner/spinner)]
           [:script {:type "text/javascript"
                     :src "/js/compiled/mateuszmazurczak-share.js"}]
           [:script {:type "text/javascript"
                     :src "/js/compiled/mateuszmazurczak-frontend-core.js"}])
          http-response/ok
          handler-utils/web-page))))

(defn mateuszmazurczak-page
  "Generate the mateuszmazurczak page

  Params:
  * `http-request`"
  [{:keys [tr]
    :as http-request}]
  (-> (handler-utils/build
       (merge (update-in http-request
                         [:header-elements]
                         conj
                         [:script {:type "text/javascript"}
                          (hiccup2/raw (mm-conf/config-web-reference))])
              {:meta-tags {:description (fallback/always-return
                                         #(tr :consulting)
                                         "Software development consulting")
                           :image (str "https://mateuszmazurczak.com/"
                                       (fallback/always-return
                                        #(tr :page-preview)
                                        "img/preview/en.png"))
                           :title "Mateuszmazurczak"
                           :author "Mateuszmazurczak"
                           :url "https://mateuszmazurczak.com/"
                           :twitter-content "sumary_large_image"
                           :type "website"}})
       [:div {:id "app"
              :class ["h-full"]}
        (mm-spinner/spinner)]
       [:script {:type "text/javascript"
                 :src "/js/compiled/mateuszmazurczak-share.js"}]
       [:script {:type "text/javascript"
                 :src "/js/compiled/mateuszmazurczak-frontend-core.js"}])
      http-response/ok
      handler-utils/web-page))
