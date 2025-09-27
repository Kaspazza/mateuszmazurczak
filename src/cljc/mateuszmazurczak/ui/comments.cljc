(ns mateuszmazurczak.ui.comments)

(defn comment-settings-dropdown
  [{:keys [id on-edit on-remove on-report]}]
  [:div {:class ["relative"]}
   [:button {:id (str "dropdownComment" id "Button")
             :type "button"
             :class ["inline-flex"
                     "items-center"
                     "p-2"
                     "text-sm"
                     "font-medium"
                     "text-center"
                     "text-gray-500"
                     "dark:text-gray-400"
                     "bg-white"
                     "rounded-lg"
                     "hover:bg-gray-100"
                     "focus:ring-4"
                     "focus:outline-none"
                     "focus:ring-gray-50"
                     "dark:bg-gray-900"
                     "dark:hover:bg-gray-700"
                     "dark:focus:ring-gray-600"]}
    [:svg {:class ["w-4" "h-4"]
           :fill "currentColor"
           :viewBox "0 0 16 3"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path
      {:d
       "M2 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm6.041 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM14 0a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Z"}]]
    [:span {:class ["sr-only"]}
     "Comment settings"]]
   [:div {:id (str "dropdownComment" id)
          :class ["hidden"
                  "z-10"
                  "w-36"
                  "bg-white"
                  "rounded"
                  "divide-y"
                  "divide-gray-100"
                  "shadow"
                  "dark:bg-gray-700"
                  "dark:divide-gray-600"]}
    [:ul {:class ["py-1" "text-sm" "text-gray-700" "dark:text-gray-200"]}
     [:li
      [:a {:href "#"
           :on-click on-edit
           :class ["block"
                   "py-2"
                   "px-4"
                   "hover:bg-gray-100"
                   "dark:hover:bg-gray-600"
                   "dark:hover:text-white"]}
       "Edit"]]
     [:li
      [:a {:href "#"
           :on-click on-remove
           :class ["block"
                   "py-2"
                   "px-4"
                   "hover:bg-gray-100"
                   "dark:hover:bg-gray-600"
                   "dark:hover:text-white"]}
       "Remove"]]
     [:li
      [:a {:href "#"
           :on-click on-report
           :class ["block"
                   "py-2"
                   "px-4"
                   "hover:bg-gray-100"
                   "dark:hover:bg-gray-600"
                   "dark:hover:text-white"]}
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
  [:article {:class
             (into
              ["p-2" "text-base" "bg-white" "rounded-lg" "dark:bg-gray-900"]
              (cond-> []
                parent? identity
                (seq replies) (conj "mb-3")
                border-top?
                (conj "border-t" "border-gray-200" "dark:border-gray-700")))}
   [:footer {:class ["flex" "justify-between" "items-center" "mb-2"]}
    [:div {:class ["flex" "items-center"]}
     [:p {:class ["inline-flex"
                  "items-center"
                  "mr-3"
                  "text-sm"
                  "text-gray-900"
                  "dark:text-white"
                  "font-semibold"]}
      (when avatar-url
        [:img {:class ["mr-2" "w-6" "h-6" "rounded-full"]
               :src avatar-url
               :alt (:name author)}])
      (:name author)]
     [:p {:class ["text-sm" "text-gray-600" "dark:text-gray-400"]}
      [:time {:dateTime created-at
              :title created-at}
       created-at]]]
    [comment-settings-dropdown {:id id
                                :on-edit on-edit
                                :on-remove on-remove
                                :on-report on-report}]]
   [:p {:class ["text-gray-500" "dark:text-gray-400"]}
    content]
   [:div {:class ["flex" "items-center" "mt-4" "space-x-4"]}
    [:button {:type "button"
              :class ["flex"
                      "items-center"
                      "text-sm"
                      "text-gray-500"
                      "hover:underline"
                      "dark:text-gray-400"
                      "font-medium"]
              :on-click #(on-reply id)}
     [:svg {:class ["mr-1.5" "w-3.5" "h-3.5"]
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
     [:div {:class ["mt-4"]}
      (for [reply replies]
        ^{:key (:id reply)} [comment-card (assoc reply :parent? true)])])])

(defn comment-form
  [{:keys [content on-content-change on-submit]}]
  [:form {:class ["mb-6"]
          :on-submit on-submit}
   [:div {:class ["py-2"
                  "px-4"
                  "mb-4"
                  "bg-white"
                  "rounded-lg"
                  "rounded-t-lg"
                  "border"
                  "border-gray-200"
                  "dark:bg-gray-800"
                  "dark:border-gray-700"]}
    [:label {:class ["sr-only"]
             :for "comment"}
     "Your comment"]
    [:textarea {:id "comment"
                :rows 6
                :class ["px-0"
                        "w-full"
                        "text-sm"
                        "text-gray-900"
                        "border-0"
                        "focus:ring-0"
                        "focus:outline-none"
                        "dark:text-white"
                        "dark:placeholder-gray-400"
                        "dark:bg-gray-800"]
                :placeholder "Write a comment..."
                :required true
                :value content
                :on-change on-content-change}]]
   [:button {:type "submit"
             :class ["inline-flex"
                     "items-center"
                     "py-2.5"
                     "px-4"
                     "text-xs"
                     "font-medium"
                     "text-center"
                     "text-white"
                     "bg-primary-700"
                     "rounded-lg"
                     "focus:ring-4"
                     "focus:ring-primary-200"
                     "dark:focus:ring-primary-900"
                     "hover:bg-primary-800"]}
    "Post comment"]])

(defn comments-section
  [{:keys [comments count add-comment-props on-reply]}]
  [:section {:class
             ["bg-white" "dark:bg-gray-900" "py-8" "lg:py-16" "antialiased"]}
   [:div {:class ["max-w-2xl" "mx-auto" "px-4"]}
    [:div {:class ["flex" "justify-between" "items-center" "mb-6"]}
     [:h2
      {:class
       ["text-lg" "lg:text-2xl" "font-bold" "text-gray-900" "dark:text-white"]}
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
