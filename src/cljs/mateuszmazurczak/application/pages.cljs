(ns mateuszmazurczak.application.pages
  "Describes the link between page names and contents"
  (:require
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
  (let [{:keys [valid? data error]} page-data]
    (if valid?
      [mm-ui-structure/mateuszmazurczak-page-structure [pages-aoc/aoc-page data]]
      [:div
       [mm-ui-errors/internal-error {:title "Page Data Error"
                                     :description
                                     "There was an error loading the page data. Please refresh."
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
            (pr-str (:actual-data error))]]])])))

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
