(ns mateuszmazurczak.ui.comments)

(defn comment-settings-dropdown
  [{:keys [id on-edit on-remove on-report]}]
  [:div {:class "relative"}
   [:button
    {:id (str "dropdownComment" id "Button")
     :type "button"
     :class
     "inline-flex items-center p-2 text-sm font-medium text-muted-foreground bg-transparent rounded-lg hover:bg-accent hover:text-accent-foreground focus:ring-2 focus:outline-none focus:ring-ring transition-colors"}
    [:svg {:class "w-4 h-4"
           :fill "currentColor"
           :viewBox "0 0 16 3"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path
      {:d
       "M2 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm6.041 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM14 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Z"}]]
    [:span {:class "sr-only"}
     "Comment settings"]]
   [:div {:id (str "dropdownComment" id)
          :class "hidden z-10 w-36 bg-popover rounded-lg border border-border shadow-lg"}
    [:ul {:class "py-1 text-sm"}
     [:li
      [:a
       {:href "#"
        :on-click on-edit
        :class
        "block py-2 px-4 text-popover-foreground hover:bg-accent hover:text-accent-foreground transition-colors"}
       "Edit"]]
     [:li
      [:a {:href "#"
           :on-click on-remove
           :class "block py-2 px-4 text-destructive hover:bg-destructive/10 transition-colors"}
       "Remove"]]
     [:li
      [:a
       {:href "#"
        :on-click on-report
        :class
        "block py-2 px-4 text-popover-foreground hover:bg-accent hover:text-accent-foreground transition-colors"}
       "Report"]]]]])

(defn comment-card
  [{:keys [id
           author
           content
           created-at
           avatar-url
           on-reply
           on-edit
           on-remove
           on-report
           replies
           parent?
           border-top?]}]
  [:article {:class (into ["p-4" "text-base" "bg-card" "rounded-lg" "border" "border-border"]
                          (cond-> []
                            parent? (conj "ml-6")
                            (seq replies) (conj "mb-3")
                            border-top? (conj "mt-4")))}
   [:footer {:class "flex justify-between items-center mb-3"}
    [:div {:class "flex items-center gap-3"}
     (when avatar-url
       [:img {:class "w-8 h-8 rounded-full"
              :src avatar-url
              :alt (:name author)}])
     [:div
      [:p {:class "text-sm font-semibold text-foreground"}
       (:name author)]
      [:p {:class "text-xs text-muted-foreground"}
       [:time {:dateTime created-at
               :title created-at}
        created-at]]]]
    [comment-settings-dropdown {:id id
                                :on-edit on-edit
                                :on-remove on-remove
                                :on-report on-report}]]
   [:p {:class "text-foreground leading-relaxed"}
    content]
   [:div {:class "flex items-center mt-4 gap-4"}
    [:button
     {:type "button"
      :class
      "flex items-center gap-2 text-sm text-muted-foreground hover:text-primary font-medium transition-colors"
      :on-click #(on-reply id)}
     [:svg {:class "w-4 h-4"
            :aria-hidden "true"
            :fill "none"
            :viewBox "0 0 20 18"
            :xmlns "http://www.w3.org/2000/svg"}
      [:path
       {:stroke "currentColor"
        :strokeLinecap "round"
        :strokeLinejoin "round"
        :strokeWidth "2"
        :d
        "M5 5h5M5 8h2m6-3h2m-5 3h6m2-7H2a1 1 0 0 0-1 1v9a1 1 0 0 0 1 1h3v5l5-5h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1Z"}]]
     "Reply"]]
   (when (seq replies)
     [:div {:class "mt-4 space-y-3"}
      (for [reply replies] ^{:key (:id reply)} [comment-card (assoc reply :parent? true)])])])

(defn comment-form
  [{:keys [content on-content-change on-submit]}]
  [:form {:class "mb-8"
          :on-submit on-submit}
   [:div {:class "py-3 px-4 mb-4 bg-card rounded-lg border border-border"}
    [:label {:class "sr-only"
             :for "comment"}
     "Your comment"]
    [:textarea
     {:id "comment"
      :rows 6
      :class
      "px-0 w-full text-sm text-foreground border-0 focus:ring-0 focus:outline-none bg-transparent placeholder:text-muted-foreground"
      :placeholder "Write a comment..."
      :required true
      :value content
      :on-change on-content-change}]]
   [:button
    {:type "submit"
     :class
     "inline-flex items-center gap-2 py-2.5 px-4 text-sm font-medium text-primary-foreground bg-primary rounded-md hover:bg-primary/90 focus:ring-4 focus:ring-ring focus:outline-none transition-colors"}
    "Post comment"]])

(defn comments-section
  [{:keys [comments count add-comment-props on-reply]}]
  [:section {:class "bg-background py-8 lg:py-16 antialiased border-t border-border mt-12"}
   [:div {:class "max-w-2xl mx-auto px-4"}
    [:div {:class "flex justify-between items-center mb-6"}
     [:h2 {:class "text-2xl font-bold text-foreground"}
      (str "Discussion (" (or count (count comments)) ")")]]
    [comment-form add-comment-props]
    (doall (map-indexed (fn [idx
                             {:keys [id]
                              :as c}]
                          ^{:key id}
                          [comment-card
                           (merge c
                                  {:on-reply on-reply
                                   :border-top? (pos? idx)})])
                        comments))]])

(defn replies-list
  [{:keys [comments parent-id on-reply-click render-replies]}]
  (into [:<>]
        (doall (for [{:keys [id]
                      :as c}
                     (filter #(= (:parent-id %) parent-id) comments)]
                 ^{:key id}
                 [comment-card
                  (merge c
                         {:on-reply on-reply-click
                          :children (when render-replies (render-replies id))
                          :parent? true})]))))
