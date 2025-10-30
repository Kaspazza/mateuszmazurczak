(ns mateuszmazurczak.adapters.http.handler
  "Server-side web handlers utilities"
  (:require
   [hiccup.page                  :as hiccup-page]
   [hiccup2.core                 :as hiccup2]
   [ring.middleware.anti-forgery :as ring-anti-forgery]))

(defn web-page
  [request]
  (-> request
      (assoc-in [:headers "content-type"] "text/html;charset=utf8")))

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
