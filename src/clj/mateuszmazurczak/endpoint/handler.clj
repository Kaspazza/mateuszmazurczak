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
  {:html-page/index mateuszmazurczak-page
   :html-page/simulation (partial article-page
                                  {:title :simulation
                                   :description :simulation-meta-description
                                   :image :simulation-page-preview
                                   :url "articles/simulation"})
   :html-page/simulation-pitch (partial article-page
                                        {:title :simulation-pitch
                                         :description
                                         :simulation-pitch-meta-description
                                         :image :simulation-pitch-page-preview
                                         :url "articles/simulation-pitch"})
   :html-page/about-us (partial article-page
                                {:title :about-us
                                 :description :about-us-meta-description
                                 :image :about-us-page-preview
                                 :url "articles/about-us"})
   :html-page/our-brand (partial article-page
                                 {:title :our-brand
                                  :description :our-brand-meta-description
                                  :image :our-brand-page-preview
                                  :url "articles/our-brand"})
   :html-page/commitment (partial article-page
                                  {:title :commitment-to-collaboration
                                   :description :commitment-meta-description
                                   :image :commitment-page-preview
                                   :url "articles/commitment"})
   :html-page/additional-outcomes
   (partial article-page
            {:title :additional-outcomes
             :description :additional-outcomes-meta-description
             :image :additional-outcomes-page-preview
             :url "articles/additional-outcomes"})
   :html-page/network (partial article-page
                               {:title :network
                                :description :network-meta-description
                                :image :network-page-preview
                                :url "articles/network"})
   :html-page/our-values (partial article-page
                                  {:title :our-values
                                   :description :our-values-meta-description
                                   :image :our-values-page-preview
                                   :url "articles/our-values"})})
