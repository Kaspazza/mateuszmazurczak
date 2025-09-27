(ns mateuszmazurczak.ui.pages.articles
  (:require
   [mateuszmazurczak.articles.core     :as articles]
   [mateuszmazurczak.i18n.translate    :as mm-i18n-translate]
   [mateuszmazurczak.navigation.routes :as-alias mm-routes]
   [mateuszmazurczak.ui.articles       :as ui-articles]
   [mateuszmazurczak.ui.comments       :as ui-comments]
   [re-frame.core                      :as rf]
   [reagent.core                       :as r]))

(defn articles-page
  []
  [:div {:class ["mt-12"]}
   [:h1 {:class ["text-4xl/7 font-bold ml-4 mb-8"]}
    (mm-i18n-translate/tr :articles)]
   [:div {:class ["grid justify-items-stretch gap-6 mx-auto w-full"]}
    (doall (for [{:keys [title id]
                  :as article}
                 articles/articles]
             ^{:key title}
             [ui-articles/article-card
              (merge article
                     {:on-click #(rf/dispatch [:nav/navigate
                                               ::mm-routes/article
                                               {:article-id (name id)}])})]))]])
(defonce comments
  (r/atom
   [{:id 1
     :content "Great article!"
     :author {:name "Jane Doe"
              :type :guest}
     :created-at "2024-06-01T12:00"}
    {:id 2
     :content "Thank you!"
     :parent-id 1
     :author {:name "Mateusz Mazurczak"
              :type :owner}
     :created-at "2024-06-01T12:05"}
    {:id 3
     :content "Much appreciated! Glad you liked it ☺️"
     :author {:name "Jese Leos"
              :type :guest}
     :created-at "2024-06-01T12:10"
     :parent-id 2}
    {:id 4
     :content "Thanks for sharing this."
     :author {:name "Helene Engels"
              :type :guest}
     :created-at "2024-06-01T12:15"}]))


(defonce replying-to (r/atom nil))

(defonce reply-form-state
  (r/atom {:content ""
           :name ""}))

(defn handle-reply-click
  [comment-id]
  (reset! replying-to comment-id)
  (reset! reply-form-state {:content ""
                            :name ""}))

(defonce form-state
  (r/atom {:content ""
           :name ""}))

(defn handle-content-change
  [e]
  (swap! form-state assoc :content (.. e -target -value)))

(defn handle-submit
  [e]
  (.preventDefault e)
  (let [{:keys [content name]} @form-state]
    (when (seq content)
      (swap! comments conj
        {:id (random-uuid)
         :content content
         :author {:name (or name "Anonymous")
                  :type :guest}
         :created-at (.toISOString (js/Date.))})
      (reset! form-state {:content ""
                          :name ""}))))

(defn nest-comments
  "Takes a flat vector of comments and returns a vector of top-level comments,
   each with a :replies vector of nested replies."
  [comments]
  (let [by-parent (group-by :parent-id comments)
        nest (fn nest [parent-id]
               (mapv #(assoc % :replies (vec (nest (:id %))))
                     (get by-parent parent-id)))]
    (nest nil)))

;;TODO - move to reframe
;;TODO - re-think re-frame initial db-state and location
;;TODO - Add loading comments from db
;;TODO - Add adding comments (and replys) to db and make it work from frontend
;;TODO - Add loading state when they are being read from DB

(defn article-page
  [article]
  (let [nested-comments (nest-comments @comments)
        comments-count (count @comments)
        add-comment-props {:content (:content @form-state)
                           :on-content-change handle-content-change
                           :on-submit handle-submit}
        on-reply handle-reply-click]
    [:div
     [ui-articles/article-wrap article]
     [ui-comments/comments-section {:comments nested-comments
                                    :count comments-count
                                    :add-comment-props add-comment-props
                                    :on-reply on-reply}]]))

