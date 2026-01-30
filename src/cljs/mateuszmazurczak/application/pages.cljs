(ns mateuszmazurczak.application.pages
  "Describes the link between page names and contents"
  (:require
   [cljs.pprint]
   [mateuszmazurczak.domain.articles.core  :as articles]
   [mateuszmazurczak.frontend-i18n         :as fi18n]
   [mateuszmazurczak.system.config         :as config]
   [mateuszmazurczak.ui.components.spinner :as mm-ui-spinner]
   [mateuszmazurczak.ui.errors             :as mm-ui-errors]
   [mateuszmazurczak.ui.pages.admin        :as pages-admin]
   [mateuszmazurczak.ui.pages.aoc          :as pages-aoc]
   [mateuszmazurczak.ui.pages.articles     :as pages-articles]
   [mateuszmazurczak.ui.pages.home         :as mm-home]
   [mateuszmazurczak.ui.pages.qr-codes     :as pages-qr-codes]
   [mateuszmazurczak.ui.structure          :as mm-ui-structure]))

(defmulti pages :page-id)

(defmethod pages :default
  [_]
  [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                           :description (fi18n/tr :not-found-description)
                           :back-home-text (fi18n/tr :back-home)}])

(defmethod pages :pages/pending
  [_]
  [mm-ui-spinner/spinner {:class "size-8 m-auto"}])

(defmethod pages :pages/system-error
  [_]
  [mm-ui-errors/internal-error
   {:title "System Initialization Failed"
    :description
    "We encountered an error while starting the application. Please refresh the page or contact support if the problem persists."
    :back-home-text "Refresh Page"}])

(defmethod pages :pages/home
  [_ page-data]
  (let [{:keys [valid? data]} page-data
        {:keys [loading?]} data]
    (cond
      (not valid?) [mm-ui-errors/internal-error
                    {:title "Page Data Error"
                     :description "There was an error loading the home page data. Please refresh."
                     :back-home-text "Refresh Page"}]
      (false? loading?) [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home data]]
      :else [mm-ui-structure/mateuszmazurczak-page-structure
             [:div {:class "flex items-center justify-center h-full"}
              [mm-ui-spinner/spinner {:class "size-8"}]]])))

(defmethod pages :pages/articles
  [_]
  [mm-ui-structure/mateuszmazurczak-page-structure [pages-articles/articles-page]])

(defmethod pages :pages/article
  [route-data]
  (let [article-id (keyword (get-in route-data [:path-parameters :article-id]))
        article (articles/article article-id)]
    (if article
      [mm-ui-structure/mateuszmazurczak-page-structure [pages-articles/article-page article]]
      [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                               :description (fi18n/tr :not-found-description)
                               :back-home-text (fi18n/tr :back-home)}])))

(defmethod pages :pages/aoc
  [_ page-data]
  (let [{:keys [valid? data error]} page-data
        error-text (:text error)]
    (if valid?
      [mm-ui-structure/mateuszmazurczak-page-structure [pages-aoc/aoc-page data]]
      [mm-ui-structure/mateuszmazurczak-page-structure
       [:div {:class "container mx-auto px-4 py-8 max-w-4xl"}
        [:div {:class "bg-destructive/10 border-2 border-destructive rounded-lg p-8 mb-6"}
         [:div {:class "flex items-start gap-4"}
          [:div {:class "flex-shrink-0"}
           [:svg {:class "size-12 text-destructive"
                  :xmlns "http://www.w3.org/2000/svg"
                  :viewBox "0 0 24 24"
                  :fill "none"
                  :stroke "currentColor"
                  :stroke-width "2"}
            [:circle {:cx "12" :cy "12" :r "10"}]
            [:line {:x1 "12" :y1 "8" :x2 "12" :y2 "12"}]
            [:line {:x1 "12" :y1 "16" :x2 "12.01" :y2 "16"}]]]
          [:div {:class "flex-1"}
           [:h1 {:class "text-2xl font-bold text-destructive mb-2"}
            (:title error-text)]
           [:p {:class "text-muted-foreground mb-4"}
            (:description error-text)]
           [:button {:class "px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
                     :on-click #(js/window.location.reload)}
            (:refresh-page error-text)]]]]
        (when (and (config/development?) error)
          [:div {:class "space-y-4"}
           [:div {:class "bg-card border rounded-lg p-6 shadow-sm"}
            [:h2 {:class "text-lg font-semibold mb-3 flex items-center gap-2"}
             [:svg {:class "size-5 text-amber-500"
                    :xmlns "http://www.w3.org/2000/svg"
                    :viewBox "0 0 24 24"
                    :fill "none"
                    :stroke "currentColor"
                    :stroke-width "2"}
              [:path {:d "M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"}]
              [:line {:x1 "12" :y1 "9" :x2 "12" :y2 "13"}]
              [:line {:x1 "12" :y1 "17" :x2 "12.01" :y2 "17"}]]
             (:validation-errors error-text)]
            [:div {:class "bg-muted rounded p-4 font-mono text-sm"}
             [:pre {:class "whitespace-pre-wrap break-words"}
              (with-out-str
                (cljs.pprint/pprint (get-in error [:explanation :explained])))]]]
           [:div {:class "bg-card border rounded-lg p-6 shadow-sm"}
            [:h2 {:class "text-lg font-semibold mb-3 flex items-center gap-2"}
             [:svg {:class "size-5 text-blue-500"
                    :xmlns "http://www.w3.org/2000/svg"
                    :viewBox "0 0 24 24"
                    :fill "none"
                    :stroke "currentColor"
                    :stroke-width "2"}
              [:circle {:cx "12" :cy "12" :r "10"}]
              [:line {:x1 "12" :y1 "16" :x2 "12" :y2 "12"}]
              [:line {:x1 "12" :y1 "8" :x2 "12.01" :y2 "8"}]]
             (:raw-data-received error-text)]
            [:details {:class "cursor-pointer"}
             [:summary {:class "text-sm text-muted-foreground hover:text-foreground mb-2"}
              (:click-to-expand-raw-data error-text)]
             [:div {:class "bg-muted rounded p-4 font-mono text-xs overflow-auto max-h-96"}
              [:pre {:class "whitespace-pre-wrap break-words"}
               (with-out-str
                 (cljs.pprint/pprint (:actual-data error)))]]]]])]])))

(defmethod pages :pages/admin
  [_ page-data]
  (let [{:keys [valid? data error]} page-data
        {:keys [loading?]} data]
    [mm-ui-structure/mateuszmazurczak-page-structure
     (cond
       (not valid?) [:div
                     [mm-ui-errors/internal-error
                      {:title "Page Data Error"
                       :description "There was an error loading the admin page. Please refresh."
                       :back-home-text "Refresh Page"}]
                     (when (and (config/development?) error)
                       [:div {:class "container mx-auto px-4 py-8"}
                        [:div {:class "bg-red-50 border border-red-200 rounded p-4"}
                         [:h3 {:class "font-bold mb-2"}
                          "Validation Error Details:"]
                         [:pre {:class "text-xs overflow-auto"}
                          (str "Explained: " (pr-str (get-in error [:explanation :explained])))
                          "\n\n"
                          "Raw data: "
                          (pr-str (:actual-data error))]]])]
       (false? loading?) [pages-admin/admin-page data]
       :else [:div {:class "flex items-center justify-center h-full"}
              [mm-ui-spinner/spinner {:class "size-8"}]])]))

(defmethod pages :pages/qr-codes
  [_ page-data]
  (let [{:keys [valid? data error]} page-data]
    [mm-ui-structure/mateuszmazurczak-page-structure
     (if valid?
       [pages-qr-codes/qr-codes-page data]
       [:div
        [mm-ui-errors/internal-error
         {:title "Page Data Error"
          :description "There was an error loading the QR codes page. Please refresh."
          :back-home-text "Refresh Page"}]
        (when (and (config/development?) error)
          [:div {:class "container mx-auto px-4 py-8"}
           [:div {:class "bg-red-50 border border-red-200 rounded p-4"}
            [:h3 {:class "font-bold mb-2"}
             "Validation Error Details:"]
            [:pre {:class "text-xs overflow-auto"}
             (str "Explained: " (pr-str (:data error)))
             "\n\n"
             "Raw data: "
             (pr-str (:actual-data error))]]])])]))
