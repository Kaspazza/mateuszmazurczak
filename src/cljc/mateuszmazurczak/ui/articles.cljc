(ns mateuszmazurczak.ui.articles
  (:require
   #?(:cljs [reagent.core :as r])
   [clojure.walk :as walk]))

(defn header-ref
  [id]
  [:a {:href (str "#" id)
       :class "no-underline"}
   [:span
    {:class
     "heading-anchorlink-icon bg-muted/50 hover:bg-primary/10 size-[1em] text-muted-foreground hover:text-primary/70 rounded-md border border-border hover:border-primary/30 inline-grid place-content-center hover:shadow-sm align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
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


(defn toc-view
  "Floating table of contents component.
   Accepts a vector of maps with :level, :text, and :id keys."
  ([headings] (toc-view headings nil))
  ([headings active-id]
   [:div {:class ["hidden lg:block"
                  "fixed"
                  "right-8"
                  "top-24"
                  "max-w-64"
                  "max-h-[80vh]"
                  "overflow-y-auto"
                  "p-4"
                  "bg-card"
                  "border"
                  "border-border"
                  "rounded-lg"
                  "shadow-md"
                  "z-10"
                  "w-64"]}
    [:h3 {:class "text-sm font-semibold text-foreground mb-3 px-2"}
     "On this page"]
    [:ul {:class ["list-none" "p-0" "m-0" "space-y-1"]}
     (for [{:keys [level text id]} headings]
       ^{:key (str "toc-li-" id)}
       [:li
        [:a
         {:class
          [(case level
             :h1 "ml-0"
             :h2 "ml-3"
             :h3 "ml-6"
             "ml-0")
           (case level
             :h1 "font-medium text-sm"
             :h2 "text-xs"
             :h3 "text-xs"
             "text-sm")
           (if (= id active-id)
             "text-primary font-semibold border-l-2 border-primary bg-primary/5"
             "text-muted-foreground hover:text-foreground border-l-2 border-transparent hover:border-border")
           "block"
           "no-underline"
           "transition-all"
           "duration-200"
           "py-1.5"
           "px-2"
           "rounded-r"
           "overflow-hidden"
           "text-ellipsis"
           "whitespace-nowrap"]
          :href (str "#" id)}
         text]])]]))

#?(:cljs (defn- throttle-header-change
           "Throttles active id change so it looks more smooth"
           [f interval]
           (let [last-call (atom 0)]
             (fn [& args]
               (let [now (js/Date.now)]
                 (when (>= (- now @last-call) interval) (reset! last-call now) (apply f args)))))))

#?(:cljs (defn- determine-active-heading
           "Determines and sets the active heading based on scroll position."
           [active-id headings indexed-headings]
           (let [scroll-y (.-scrollY js/window)
                 viewport-height (.-innerHeight js/window)
                 first-heading-id (-> headings
                                      first
                                      :id)]
             (if (< scroll-y 50)
               (reset! active-id first-heading-id)
               (let [positions (for [{:keys [id]} headings
                                     :let [el (js/document.getElementById id)]
                                     :when el
                                     :let [rect (.getBoundingClientRect el)
                                           absolute-y (+ scroll-y (.-top rect))
                                           distance (- absolute-y scroll-y 100)]]
                                 {:id id
                                  :distance distance
                                  :top (.-top rect)
                                  :index (get indexed-headings id)})
                     visible-in-viewport
                     (filter #(and (>= (:top %) 0) (<= (:top %) (/ viewport-height 2))) positions)
                     just-above (filter #(and (< (:distance %) 0) (> (:distance %) -300)) positions)
                     best-match
                     (cond
                       (seq just-above) (first (sort-by (juxt #(Math/abs (:distance %)) :index)
                                                        just-above))
                       (seq visible-in-viewport) (first (sort-by :top visible-in-viewport))
                       :else (first (sort-by (juxt #(Math/abs (:distance %)) :index) positions)))]
                 (when (and best-match (not= @active-id (:id best-match)))
                   (reset! active-id (:id best-match))))))))

#?(:cljs (defn heading-change
           "Apply heading change for TOC"
           [active-id headings]
           (let [id-to-index (into {} (map-indexed (fn [idx {:keys [id]}] [id idx]) headings))]
             (throttle-header-change #(determine-active-heading active-id headings id-to-index)
                                     50))))

#?(:cljs
     (defn table-of-contents
       "Floating table of contents component that tracks active heading based on scroll position.
        Accepts a vector of heading maps with :level, :text, and :id keys."
       [headings]
       (let [active-id (r/atom (-> headings
                                   first
                                   :id))
             handle-scroll-fn (heading-change active-id headings)]
         (r/create-class {:component-did-mount
                          (fn [_] (.addEventListener js/window "scroll" handle-scroll-fn))
                          :component-will-unmount
                          (fn [_] (.removeEventListener js/window "scroll" handle-scroll-fn))
                          :reagent-render (fn [headings] [toc-view headings @active-id])}))))

(defn extract-headings
  [content]
  (let [headings (atom [])]
    (walk/postwalk (fn [node]
                     (when (and (vector? node) (some #(= % (first node)) #{:h1 :h2 :h3}))
                       (let [[tag attrs & rest] node
                             text (last rest)
                             id (:id attrs)
                             heading {:level tag
                                      :text text
                                      :id id}]
                         (swap! headings conj heading)))
                     node)
                   content)
    @headings))


(defn article-header
  [title date]
  [:header {:class "mb-8 border-b border-border pb-6"}
   [:h1 {:class "text-4xl font-bold text-foreground mb-3"}
    title]
   (when date
     [:time {:class "text-sm text-muted-foreground"}
      date])])

(defn article-wrap
  [{:keys [title date content]}]
  (let [headings (into [{:level :h1
                         :text title}]
                       (extract-headings content))]
    [:div
     #?(:cljs [table-of-contents headings]
        :clj [toc-view headings])
     [:div {:class ["notion-page" "mx-auto max-w-screen-xl px-4 py-8 lg:py-12"]}
      [:article {:class "max-w-2xl mx-auto"}
       [article-header title date]
       content]]]))

(defn article-card
  [{:keys [title description img on-click]}]
  [:div
   {:class
    "group rounded-lg overflow-hidden hover:shadow-lg transition-all duration-300 cursor-pointer bg-card hover:bg-accent/5"
    :on-click on-click}
   [:div {:class "sm:flex"}
    [:div {:class "sm:w-48 sm:flex-shrink-0"}
     [:img {:class "w-full h-48 sm:h-full object-contain p-2"
            :alt (str "Image representing " title)
            :src img
            :width 192
            :height 192
            :loading "lazy"
            :decoding "async"}]]
    [:div {:class "p-6 flex-1"}
     [:h2
      {:class
       "text-xl font-semibold mb-2 text-card-foreground group-hover:text-primary transition-colors"}
      title]
     [:p {:class "text-sm text-muted-foreground line-clamp-2"}
      description]]]])
