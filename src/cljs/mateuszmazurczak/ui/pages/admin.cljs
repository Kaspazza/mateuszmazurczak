(ns mateuszmazurczak.ui.pages.admin
  "Admin page UI - login form and dashboard."
  (:require
   ["lucide-react"                        :refer [Lock Shield]]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.input  :as input]
   [mateuszmazurczak.ui.components.label  :as label]))

(defn admin-login-form
  "Admin login form.
   
   Props:
   - :form - Form data {:admin-key string}
   - :submitting? - Whether form is submitting
   - :text - Map of translated text
   - :on-login - Login handler (fn [])
   - :on-update-form - Form update handler (fn [field value])"
  [{:keys [form submitting? text on-login on-update-form]}]
  [:div {:class "min-h-screen bg-background flex items-center justify-center p-4"}
   [:div {:class "w-full max-w-md"}
    [:div {:class "bg-card border rounded-lg shadow-lg p-8"}
     [:div {:class "flex items-center justify-center mb-6"}
      [:> Lock {:class "size-12 text-primary"}]]
     [:h1 {:class "text-2xl font-bold text-center mb-2"}
      (:admin-login text)]
     [:p {:class "text-muted-foreground text-center mb-6"}
      (:enter-admin-key text)]
     [:form {:on-submit (fn [e]
                          (.preventDefault e)
                          (when (and (not submitting?) (seq (:admin-key form))) (on-login)))}
      [:div {:class "space-y-4"}
       [:div {:class "space-y-2"}
        [label/label {:htmlFor "admin-key"}
         (:enter-admin-key text)]
        [input/input {:id "admin-key"
                      :type "password"
                      :placeholder (:admin-key-placeholder text)
                      :value (:admin-key form)
                      :disabled submitting?
                      :on-change #(on-update-form :admin-key (.. % -target -value))}]]
       [button/button {:type "submit"
                       :class "w-full"
                       :disabled (or submitting? (empty? (:admin-key form)))}
        (if submitting? (:submitting text) (:login text))]]]]]])

(defn admin-dashboard
  "Admin dashboard (shown when logged in).
   
   Props:
   - :text - Map of translated text
   - :on-logout - Logout handler
   - :on-navigate-aoc - Navigate to AoC page handler"
  [{:keys [text on-logout on-navigate-aoc]}]
  [:div {:class "min-h-screen bg-background"}
   [:div {:class "container mx-auto px-4 py-8 max-w-4xl"}
    [:div {:class "flex items-center justify-between mb-8"}
     [:div {:class "flex items-center gap-3"}
      [:> Shield {:class "size-8 text-primary"}]
      [:div
       [:h1 {:class "text-3xl font-bold"}
        (:admin-dashboard text)]
       [:p {:class "text-muted-foreground text-sm"}
        (:logged-in-as-admin text)]]]
     [button/button {:variant :outline
                     :on-click on-logout}
      (:admin-logout text)]]
    [:div {:class "space-y-6"}
     [:div {:class "bg-card border rounded-lg p-6"}
      [:h2 {:class "text-xl font-semibold mb-4"}
       (:manage-aoc-solutions text)]
      [:p {:class "text-muted-foreground mb-4"}
       (:admin-description text)]
      [button/button {:on-click on-navigate-aoc}
       (:go-to-aoc-page text)]]]]])

(defn admin-page
  "Main admin page - shows login form or dashboard based on login state.
   
   Props:
   - :logged-in? - Whether admin is logged in (nil/false = not logged in, true = logged in)
   - :form - Form data for login
   - :submitting? - Whether form is submitting
   - :text - Map of translated text
   - :handlers - Map of event handlers"
  [{:keys [logged-in? form submitting? text handlers]}]
  (let [{:keys [on-login on-logout on-update-form on-navigate-aoc]} handlers]
    (if logged-in?
      [admin-dashboard {:text text
                        :on-logout on-logout
                        :on-navigate-aoc on-navigate-aoc}]
      [admin-login-form {:form form
                         :submitting? submitting?
                         :text text
                         :on-login on-login
                         :on-update-form on-update-form}])))
