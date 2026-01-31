(ns mateuszmazurczak.ui.pages.articles
  (:require
   [mateuszmazurczak.adapters.navigation.routes :as-alias mm-routes]
   [mateuszmazurczak.domain.articles.core       :as articles]
   [mateuszmazurczak.frontend-i18n              :as fi18n]
   [mateuszmazurczak.ports.events               :as events]
   [mateuszmazurczak.ui.articles                :as ui-articles]
   [mateuszmazurczak.ui.components.tag-combobox :refer [tag-combobox]]
   [reagent.core                                :as r]))

(defn articles-page
  []
  [:div {:class "min-h-screen bg-background"}
   [:div {:class "container mx-auto px-4 py-8 max-w-5xl"}
    [:div {:class "mb-8"}
     [:h1 {:class "text-4xl font-bold mb-2 text-foreground"}
      (fi18n/tr :articles)]
     [:p {:class "text-muted-foreground"}
      "Thoughts on software development, architecture, and more."]]
    [:div
     (let [available-tags (r/atom #{"urgent" "important" "review" "blocked" "in-progress"})
           selected-tag (r/atom nil)]
       [:div {:class "p-8 space-y-4"}
        [:div
         [:h3 {:class "text-lg font-semibold mb-2"} "Select a Tag"]
         [:p {:class "text-sm text-muted-foreground mb-4"}
          "Choose from existing tags or create a new one"]
         
         [tag-combobox
          {:tags @available-tags
           :selected-tag @selected-tag
           :on-select #(reset! selected-tag %)
           :on-create (fn [new-tag]
                        (swap! available-tags conj new-tag)
                        (reset! selected-tag new-tag))
           :placeholder "Search tags!" 
                                  :class "w-full"}
          ]]
        [:div {:class "mt-6 p-4 bg-muted rounded-md"}
         [:p {:class "text-sm font-medium"} "State:"]
         [:pre {:class "text-xs mt-2"}
          (str "Selected: " (pr-str @selected-tag) "\n"
               "Available: " (pr-str @available-tags))]]])]
    [:div {:class "grid gap-6 w-full"}
     (doall (for [{:keys [title id]
                   :as article}
                  articles/articles]
              ^{:key title}
              [ui-articles/article-card
               (merge article
                      {:on-click #(events/dispatch! [:nav/navigate
                                                     ::mm-routes/article
                                                     {:article-id (name id)}])})]))]]])
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

(defn handle-content-change [e] (swap! form-state assoc :content (.. e -target -value)))

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
               (mapv #(assoc % :replies (vec (nest (:id %)))) (get by-parent parent-id)))]
    (nest nil)))

;;TODO - move to reframe
;;TODO - re-think re-frame initial db-state and location
;;TODO - Add loading comments from db
;;TODO - Add adding comments (and replys) to db and make it work from frontend
;;TODO - Add loading state when they are being read from DB

(defn article-page
  [article]
  #_(let [nested-comments (nest-comments @comments)
          comments-count (count @comments)
          add-comment-props {:content (:content @form-state)
                             :on-content-change handle-content-change
                             :on-submit handle-submit}
          on-reply handle-reply-click])
  [:div {:class "min-h-screen bg-background"}
   [ui-articles/article-wrap article]
   #_[ui-comments/comments-section {:comments nested-comments
                                    :count comments-count
                                    :add-comment-props add-comment-props
                                    :on-reply on-reply}]])

