(ns mateuszmazurczak.tasks.article
  (:require
   [babashka.fs           :as fs]
   [clojure.java.io       :as io]
   [clojure.string        :as str]
   [clojure.walk          :as walk]
   [mateuszmazurczak.file :as file]
   [nextjournal.markdown  :as md]))

(def output-dir "resources/public/article/html")

(def md-dir "public/article/content")

(defn ensure-dir! [dir] (let [f (io/file dir)] (when-not (.exists f) (.mkdirs f))))

(defn read-md-content
  [article]
  (try (let [md (:content-md article)
             md-path (file/create-file-path md-dir md)
             md-res (io/resource md-path)]
         (if (fs/exists? md-res)
           (slurp md-res)
           {:status :failed
            :msg "File "
            md-path " does not exist"}))
       (catch Exception e
         {:status :failed
          :msg e})))

(defn header-ref
  [id]
  [:a {:href (str "#" id)
       :class "no-underline"}
   [:span
    {:class
     "heading-anchorlink-icon bg-base-content/5 hover:bg-primary/10 size-[1em] text-base-content/30 hover:text-primary/50 rounded-field border border-base-content/5 hover:border-primary/20 inline-grid place-content-center hover:shadow-sm hover:shadow-base-200 align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
    [:svg {:class "group-hover:scale-100 scale-90 transition-transform"
           :fill "currentColor"
           :width ".5em"
           :height ".5em"
           :viewBox "0 0 256 256"
           :id "Flat"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path
      {:d
       "M216,148H172V108h44a12,12,0,0,0,0-24H172V40a12,12,0,0,0-24,0V84H108V40a12,12,0,0,0-24,0V84H40a12,12,0,0,0,0,24H84v40H40a12,12,0,0,0,0,24H84v44a12,12,0,0,0,24,0V172h40v44a12,12,0,0,0,24,0V172h44a12,12,0,0,0,0-24Zm-108,0V108h40v40Z"}]]]])

(defn append-header-ref [[htag opts text]] [htag opts (header-ref (:id opts)) text])


(defn update-resource-path [s] (str "img/" s))

(defn update-hiccup
  [hiccup]
  (walk/postwalk (fn [x]
                   (cond
                     (and (vector? x) (= (first x) :pre)) (into [:pre.code] (rest x))
                     (and (map? x) (:src x)) (assoc x :src (update-resource-path (:src x)))
                     (and (vector? x) (= (first x) :h2)) (append-header-ref x)
                     :else x))
                 hiccup))


(defn render-article!
  "Renders all articles hiccup, for now it's used only for live reload."
  [article]
  (let [id (name (:id article))
        md-content (read-md-content article)
        ast (md/parse md-content)
        hiccup (md/->hiccup ast)
        updated-hiccup (update-hiccup hiccup)
        ns-name (str "mateuszmazurczak.domain.articles." (name id))
        clj-file-path
        (str "src/cljc/mateuszmazurczak/articles/" (str/replace (name id) #"-" "_") ".cljc")
        clj-content
        (str "(ns " ns-name ")\n\n" "(def article-content\n" (pr-str updated-hiccup) ")\n")
        ;;If html is needed
        #_#_#_#_html (str (h/html hiccup)) out-file (str output-dir "/" id ".html")]
    (spit clj-file-path clj-content)))


(defn run
  []
  (ensure-dir! output-dir)
  (doseq [article (file/read-file "articles.edn")] (render-article! article)))


(comment
 (def articles (file/read-file "articles.edn"))
 (read-md-content (first articles))
 (io/resource "")
 (render-article! (first articles))
 ;;
)
