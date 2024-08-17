(ns mateuszmazurczak.notion-utils
  (:require
   [automaton-core.log :as core-log]
   [clojure.string     :as str]
   [clojure.walk       :as walk]))

(defn replace-style-string
  [styles]
  (->> (str/split styles #";")
       (map (fn [cur] (str/split cur #":")))
       (reduce (fn [acc val]
                 (let [[key value] val]
                   (assoc acc
                          (str/replace key
                                       #"-."
                                       (fn [css]
                                         (str (get (str/upper-case css) 1))))
                          value)))
               {})))

(defn- replace-html-special-chars
  [x]
  (let [html-special-char-re #"&([^\s]+);"
        replaced-x (cond-> x
                     (str/includes? x "&#39;") (str/replace "&#39;" "'")
                     (str/includes? x "&amp;") (str/replace "&amp;" "&"))]
    (when-let [special-char-found (re-find html-special-char-re replaced-x)]
      (core-log/warn-data "Found non convertable html special character"
                          {:special-char special-char-found}))
    replaced-x))

(defn update-hiccup
  [hiccup]
  (walk/postwalk (fn [x]
                   (cond
                     (and (map? x) (string? (:style x)))
                     (assoc x :style (replace-style-string (:style x)))
                     (string? x) (replace-html-special-chars x)
                     :else x))
                 hiccup))

(comment
  (update-hiccup
   [:div {:style "white-space:pre-wrap;display:flex"}
    [:div {:class [""]
           :style
           "font-weight:600;font-size:1.875em;line-height:1.3;margin:0"}]])
  ;
)
