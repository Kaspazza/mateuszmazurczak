(ns mateuszmazurczak.endpoint.handler
  "Server-side web handlers

  Gather all handlers for all spa pages"
  (:require
   [automaton-core.log                      :as core-log]
   [automaton-core.utils.fallback           :as fallback]
   [automaton-web.adapters.be.http-response :as http-response]
   [automaton-web.components.spinner        :as web-spinner]
   [automaton-web.configuration             :as web-conf]
   [automaton-web.hiccup                    :as web-hiccup]
   [automaton-web.pages.index               :as web-pages-index]
   [clojure.string                          :as str]))

(defn article-page
  [{:keys [title author twitter-content url image description]
    :as _seo-metadata}
   {:keys [tr]
    :as http-request}]
  (core-log/trace "Display articles page")
  (http-response/ok
   {"content-type" "text/html;charset=utf8"}
   (web-pages-index/build
    (merge
     (update-in http-request
                [:header-elements]
                conj
                (web-hiccup/js-script-raw (web-conf/config-web-reference)))
     {:meta-tags {:description (fallback/always-return #(tr description) "")
                  :image (str "https://mateuszmazurczak.com/"
                              (if image
                                (fallback/always-return #(tr image)
                                                        "img/preview/en.png")
                                "img/preview/en.png"))
                  :title (fallback/always-return #(tr title) title)
                  :author (or author "Mateuszmazurczak")
                  :url (str/join "/" ["https://mateuszmazurczak.com" url])
                  :twitter-content (or twitter-content "sumary_large_image")
                  :type "website"}})
    [:div {:id "app"
           :class ["h-full"]}
     (web-spinner/spinner)]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-share.js"}]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-frontend-core.js"}])))

(defn mateuszmazurczak-page
  "Generate the mateuszmazurczak page

  Params:
  * `http-request`"
  [{:keys [tr]
    :as http-request}]
  (core-log/trace "Display mateuszmazurczak page")
  (http-response/ok
   {"content-type" "text/html;charset=utf8"}
   (web-pages-index/build
    (merge
     (update-in http-request
                [:header-elements]
                conj
                (web-hiccup/js-script-raw (web-conf/config-web-reference)))
     {:meta-tags
      {:description
       (fallback/always-return
        #(tr :we-know-how-and-we-will-help-you-grow)
        "With over two decades of expertise in supply chain and IT, working with many industries, we have the tools and knowledge to help you grow!")
       :image (str "https://mateuszmazurczak.com/"
                   (fallback/always-return #(tr :page-preview)
                                           "img/preview/en.png"))
       :title "Mateuszmazurczak"
       :author "Mateuszmazurczak"
       :url "https://mateuszmazurczak.com/"
       :twitter-content "sumary_large_image"
       :type "website"}})
    [:div {:id "app"
           :class ["h-full"]}
     (web-spinner/spinner)]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-share.js"}]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-frontend-core.js"}])))

(def registry
  "Registry matching keywords to handler"
  {:html-page/index mateuszmazurczak-page})
