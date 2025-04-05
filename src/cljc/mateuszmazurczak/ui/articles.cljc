(ns mateuszmazurczak.ui.articles)

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
  [:div {:class ["notion-page" "mx-auto max-w-screen-xl px-4 py-8 lg:py-12"]}
   [:article {:class "max-w-2xl mx-auto"}
    [article-header title date]
    content]])

(defn article-card
  [{:keys [title description img on-click]}]
  [:a
   {:class
    "card sm:card-side hover:bg-base-200 transition-colors sm:max-w-none cursor-pointer"
    :on-click on-click}
   [:figure
    {:class
     "mx-auto w-full object-cover p-6 max-sm:pb-0 sm:max-w-[12rem] sm:pe-0"}
    [:img {:class "border-base-content/5 bg-base-300 rounded-field border"
           :alt "Image representing article"
           :src img}]]
   [:div {:class "card-body"}
    [:h2 {:class "card-title"}
     title]
    [:p {:class "text-xs opacity-60"}
     description]]])
