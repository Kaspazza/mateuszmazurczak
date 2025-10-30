(ns mateuszmazurczak.adapters.http.pages
  "Web handlers implementations"
  (:require
   [clojure.string                            :as str]
   [mateuszmazurczak.adapters.http.error-page :as error-page]
   [mateuszmazurczak.adapters.http.handler    :as handler-utils]
   [mateuszmazurczak.domain.articles.core     :as articles]
   [mateuszmazurczak.ui.spinner               :as mm-spinner]
   [mateuszmazurczak.utils.fallback           :as fallback]
   [ring.util.http-response                   :as http-response]))

(defn article-page
  [{:keys [tr logger]
    :as http-request}]
  (let [{:keys [path-params path]} (:reitit.core/match http-request)
        article-id (keyword (:article-name path-params))
        {:keys [title author twitter-content img description]
         :as article}
        (articles/article article-id)]
    (if (nil? article)
      (->> http-request
           error-page/not-found-page
           http-response/not-found
           handler-utils/web-page)
      (-> (handler-utils/build
           (merge http-request
                  {:meta-tags
                   {:description (cond
                                   (keyword? description) (fallback/always-return #(tr description)
                                                                                  (str description)
                                                                                  logger)
                                   (string? description) description
                                   :else "Just my website hanging in the web")
                    :image (str "https://mateuszmazurczak.com/"
                                (cond
                                  (keyword? img) (fallback/always-return #(tr img) (str img) logger)
                                  (string? img) img
                                  :else "img/preview/en.png"))
                    :title (cond
                             (keyword? img) (fallback/always-return #(tr title) (str title) logger)
                             (string? img) img
                             :else "Mateusz Mazurczak website")
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
  [{:keys [tr logger]
    :as http-request}]
  (-> (handler-utils/build
       (merge http-request
              {:meta-tags {:description (fallback/always-return #(tr :consulting)
                                                                "Software development consulting"
                                                                logger)
                           ;;TODO add preview
                           #_#_:image
                             (str "https://mateuszmazurczak.com/"
                                  (fallback/always-return #(tr :page-preview) "img/preview/en.png"))
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
