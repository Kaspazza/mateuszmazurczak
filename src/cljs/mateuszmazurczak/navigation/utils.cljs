(ns mateuszmazurczak.navigation.utils
  "Navigation based on router and history"
  (:require
   [lambdaisland.uri :as lambda-uri]))

(defn extract-tld-from-host
  "Extract the tld from an host
  Params:
  * `url` - url to parse"
  [url]
  (some->> url
           (re-find #".*(?:\.([a-zA-Z]\w{1,2}))(?::\d{1,4})?$")
           second))

(def url-delims
  "According to [RFC3986 page 12](https://www.ietf.org/rfc/rfc3986.txt):
   gen-delims  = : / ? #  [ ] @
   sub-delims  = ! $ & ' ( ) * + , ; ="
  {:gen-delims [":" "/" "?" "#" "[" "]" "@"]
   :sub-delims ["!" "$" "&" "'" "(" ")" "*" "+" "," ";" "="]})

(defn compare-locations
  "Is the url given as a parameter the current location?
  Params:
  * `urls` - sequence of url you want to compare"
  [& urls]
  (apply = (map (comp (juxt :path :query) lambda-uri/uri) urls)))

(defn parse-queries
  "Parse queries to get the parameters
  Params:
  * `url` - url to parse"
  [url]
  (-> url
      lambda-uri/uri
      :query
      lambda-uri/query-string->map))

(defn current-url "Current location in the browser" [] (str js/window.location))

(defn current-path [] (str (.. js/window -location -pathname)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn current-location?
  "Is the `url` parameter matching the current location?
  Params:
  * `urls` - urls that you want to compare with current url "
  [& urls]
  (apply compare-locations (current-url) urls))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn current-path?
  "Is the `path` parameter matching the current location?
  Params:
  * `path` - path that you want to compare with current path"
  [path]
  (apply compare-locations (current-path) path))

(defn lang-in-url-par
  "Return the language parameter in the url
  Params:
  * `url`"
  [url]
  (-> url
      parse-queries
      :lang))

(defn navigate!
  "Navigate to the `url` and decide if browser should `preserve-history?`
  Params:
  * `url`"
  ([url] (navigate! url true))
  ([url preserve-history?]
   (if preserve-history?
     (.pushState js/window.history nil "" url)
     (.replaceState js/window.history nil "" url))))
