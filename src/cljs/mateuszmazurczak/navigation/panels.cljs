(ns mateuszmazurczak.navigation.panels
  "Describes the link between panel names and contents"
  (:require
   [automaton-core.log                  :as core-log]
   [automaton-web.components.errors     :as web-comp-errors]
   [automaton-web.components.navigation :as web-navigation]
   [automaton-web.components.spinner    :as web-spinner]
   [mateuszmazurczak.i18n.translate     :as mm-i18n-translate]
   [mateuszmazurczak.navigation.router  :as mm-nav-router]
   [mateuszmazurczak.navigation.utils   :as mm-fe-nav]
   [mateuszmazurczak.routes             :as mm-routes]
   [mateuszmazurczak.ui.home            :as mm-home]
   [mateuszmazurczak.ui.structure       :as mm-ui-structure]))


(defn- match-to-panel-id
  "Transform a match coming from routing into a panel id that will be displayed
  Params:
  * `match`"
  [match]
  (cond
    (nil? match) (let [default-panel-id :panels/not-found]
                   ;; This should not happen, but it does during dev,
                   ;; it is preferrable to display a page and a message
                   ;; than displaying a white page
                   (core-log/warn "Render panel default to `"
                                  default-panel-id
                                  "`, as match `"
                                  match
                                  "` was empty")
                   default-panel-id)
    (= match :pending) :panels/pending
    :else (let [panel-id (mm-nav-router/panel-id match)]
            (core-log/info "Render panel `" panel-id "`")
            panel-id)))

(defmulti panels match-to-panel-id)

(defmethod panels :default
  []
  (web-comp-errors/not-found
   {:title (mm-i18n-translate/tr :not-found-page)
    :description (mm-i18n-translate/tr :not-found-description)
    :back-home-text (mm-i18n-translate/tr :back-home)}))

(defmethod panels :panels/pending [] [:div [web-spinner/spinner]])

(defmethod panels :panels/home
  []
  [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home]])

(defmethod panels :panels/articles
  []
  [mm-ui-structure/mateuszmazurczak-page-structure
   [:div {:class ["mt-12"]}
    (web-navigation/navigation {:href (mm-fe-nav/href-delta ::mm-routes/home)
                                :text "Back home"
                                :dark? true})
    [:div
     (repeat
      50
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque lacinia bibendum accumsan. Sed dignissim nisl sit amet ipsum consectetur eleifend. Pellentesque magna mi, molestie quis lorem ac, malesuada consectetur erat. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vestibulum vel orci sollicitudin, consectetur diam ut, laoreet tortor. Vestibulum at auctor orci. Sed vulputate interdum mollis. Cras condimentum accumsan purus vel sollicitudin. Sed bibendum metus placerat vestibulum tincidunt.")]
    (web-navigation/navigation {:href (mm-fe-nav/href-delta ::mm-routes/home)
                                :text "Back home"
                                :dark? true})
    [:div#clue.mt-12.mb-12
     "Here is a text that is fragment ided and I want to match to oit"]
    [:div (repeat 12 "And some more text ")]]])
