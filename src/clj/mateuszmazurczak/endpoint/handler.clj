(ns mateuszmazurczak.endpoint.handler
  "Server-side web handlers

  Gather all handlers for all spa pages"
  (:require
   [clojure.string                          :as str]
   [hiccup.page                             :as hiccup-page]
   [hiccup2.core                            :as hiccup2]
   [mateuszmazurczak.configuration          :as mm-conf]
   [mateuszmazurczak.endpoint.http-response :as http-response]
   [mateuszmazurczak.ui.spinner             :as mm-spinner]
   [mateuszmazurczak.utils.fallback         :as fallback]
   [ring.middleware.anti-forgery            :as ring-anti-forgery]))

(defn anti-forgery-html-token
  []
  [:div {:name "__anti-forgery-token"
         :id "__anti-forgery-token"
         :anti-forgery-token (force ring-anti-forgery/*anti-forgery-token*)
         :class ["hidden"]}])

(defn html-core
  [head-elements body]
  (hiccup2/html (hiccup-page/doctype :html5)
                [:html
                 [:head
                  [:meta {:charset "utf-8"}]
                  [:meta {:content "width=device-width,initial-scale=1"
                          :name "viewport"}]
                  (when head-elements
                    (for [header-el head-elements] header-el))]
                 [:body (for [el body] el)]]))

(defn build
  "Build a webpage header"
  [{:keys [header-elements meta-tags]} & body]
  (let [{:keys [image
                description
                title
                type
                url
                twitter-content
                twitter-site
                author
                icon]
         :or {icon "/favicon.ico"}}
        meta-tags
        meta-title [:meta {:name "title"
                           :property "og:title"
                           :content title}]
        meta-type [:meta {:name "og:type"
                          :property "og:type"
                          :content type}]
        meta-description [:meta {:name "description"
                                 :property "og:description"
                                 :content description}]
        meta-image [:meta {:name "image"
                           :property "og:image"
                           :content image}]
        meta-url [:meta {:name "og:url"
                         :property "og:url"
                         :content url}]
        meta-author [:meta {:name "author"
                            :content author}]
        twitter-meta-card [:meta {:name "twitter:card"
                                  :content twitter-content}]
        twitter-meta-description [:meta {:name "twitter:description"
                                         :content description}]
        twitter-meta-image [:meta {:name "twitter:image"
                                   :content image}]
        twitter-meta-title [:meta {:name "twitter:title"
                                   :content title}]
        twitter-meta-site [:meta {:name "twitter:site"
                                  :content twitter-site}]
        icon [:link {:rel "icon"
                     :href icon}]
        css [:link {:type "text/css"
                    :rel "stylesheet"
                    :href "/css/compiled/styles.css"}]
        html-title [:title title]
        head-elements [meta-title
                       meta-type
                       meta-url
                       meta-image
                       meta-author
                       meta-description
                       twitter-meta-card
                       twitter-meta-title
                       twitter-meta-site
                       twitter-meta-image
                       twitter-meta-description
                       icon
                       css
                       (for [el header-elements] el)
                       html-title]
        body-elements (merge [(anti-forgery-html-token)] body)]
    (str (html-core head-elements body-elements))))

(defn article-page
  [{:keys [title author twitter-content url image description]
    :as _seo-metadata}
   {:keys [tr]
    :as http-request}]
  (http-response/ok
   {"content-type" "text/html;charset=utf8"}
   (build (merge (update-in http-request
                            [:header-elements]
                            conj
                            [:script {:type "text/javascript"}
                             (hiccup2/raw (mm-conf/config-web-reference))])
                 {:meta-tags
                  {:description (fallback/always-return #(tr description) "")
                   :image (str "https://mateuszmazurczak.com/"
                               (if image
                                 (fallback/always-return #(tr image)
                                                         "img/preview/en.png")
                                 "img/preview/en.png"))
                   :title (fallback/always-return #(tr title) title)
                   :author (or author "Mateusz Mazurczak")
                   :url (str/join "/" ["https://mateuszmazurczak.com" url])
                   :twitter-content (or twitter-content "sumary_large_image")
                   :type "website"}})
          [:div {:id "app"
                 :class ["h-full"]}
           (mm-spinner/spinner)]
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
  (http-response/ok
   {"content-type" "text/html;charset=utf8"}
   (build
    (merge
     (update-in http-request
                [:header-elements]
                conj
                [:script {:type "text/javascript"}
                 (hiccup2/raw (mm-conf/config-web-reference))])
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
     (mm-spinner/spinner)]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-share.js"}]
    [:script {:type "text/javascript"
              :src "/js/compiled/mateuszmazurczak-frontend-core.js"}])))

(def registry
  "Registry matching keywords to handler"
  {:html-page/index mateuszmazurczak-page})
