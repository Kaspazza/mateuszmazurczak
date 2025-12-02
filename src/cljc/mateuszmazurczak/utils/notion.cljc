(ns mateuszmazurczak.utils.notion
  (:require
   [clojure.string               :as str]
   [clojure.walk                 :as walk]
   [mateuszmazurczak.ui.articles :as ui-articles]))

(defn replace-style-string
  [styles]
  (->> (str/split styles #";")
       (map (fn [cur] (str/split cur #":")))
       (reduce (fn [acc val]
                 (let [[key value] val]
                   (assoc acc
                          (str/replace key #"-." (fn [css] (str (get (str/upper-case css) 1))))
                          value)))
               {})))

(defn- replace-html-special-chars
  [x]
  (let [html-special-char-re #"&([^\s]+);"
        replaced-x (cond-> x
                     (str/includes? x "&#39;") (str/replace "&#39;" "'")
                     (str/includes? x "&amp;") (str/replace "&amp;" "&")
                     (str/includes? x "&gt;") (str/replace "&gt;" ">")
                     (str/includes? x "&quot;") (str/replace "&quot;" "\"")
                     (str/includes? x "&lt;") (str/replace "&lt;" "<"))]
    (when-let [special-char-found (re-find html-special-char-re replaced-x)]
      (throw (ex-info "Found non convertable html special character"
                      {:special-char special-char-found})))
    replaced-x))

(defn append-header-ref [[htag opts text]] [htag opts (ui-articles/header-ref (:id opts)) text])

(defn update-hiccup
  [hiccup]
  (walk/postwalk (fn [x]
                   (cond
                     (and (map? x) (string? (:style x)))
                     (assoc x :style (replace-style-string (:style x)))
                     (string? x) (replace-html-special-chars x)
                     (and (vector? x) (= (first x) :h2)) (append-header-ref x)
                     :else x))
                 hiccup))



(comment
 (def article-content [])
 (update-hiccup article-content)
 ;;
)
