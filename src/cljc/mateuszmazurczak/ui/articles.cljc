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
                  "bg-base-100"
                  "rounded-lg"
                  "shadow-md"
                  "z-10"
                  "w-64"]} ; Add fixed width to prevent container resizing
    [:ul {:class ["list-none" "p-0" "m-0"]}
     (for [{:keys [level text id]} headings]
       ^{:key (str "toc-li-" id)}
       [:li {:class ["mb-2"]}
        [:a {:class
             [(case level
                :h1 "ml-0"
                :h2 "ml-4"
                :h3 "ml-8"
                "ml-0")
              (case level
                :h1 "font-medium"
                :h2 "text-sm"
                :h3 "text-xs"
                "")
              (if (= id active-id) "text-primary font-bold" "text-base-content hover:text-primary")
              "block"
              "no-underline"
              "transition-colors"
              "duration-200"
              "overflow-hidden"
              "text-ellipsis"
              "whitespace-nowrap"
              "w-full"]
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
  [:header
   [:h1 {:class "page-title"}
    title]
   (when date
     [:time {:class "text-gray-700"}
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
  [:a {:class "card sm:card-side hover:bg-base-200 transition-colors sm:max-w-none cursor-pointer"
       :on-click on-click}
   [:figure {:class "mx-auto w-full object-cover p-6 max-sm:pb-0 sm:max-w-[12rem] sm:pe-0"}
    [:img {:class "border-base-content/5 bg-base-300 rounded-field border"
           :alt "Image representing article"
           :src img}]]
   [:div {:class "card-body"}
    [:h2 {:class "card-title"}
     title]
    [:p {:class "text-xs opacity-60"}
     description]]])
