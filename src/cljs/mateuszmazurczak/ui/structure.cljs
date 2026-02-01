(ns mateuszmazurczak.ui.structure
  (:require
   ["lucide-react"                          :refer [Menu]]
   [reagent.core                                :as r :refer [defc]]
   [mateuszmazurczak.adapters.navigation.routes :as mm-routes]
   [mateuszmazurczak.frontend-i18n              :as fi18n]
   [mateuszmazurczak.ports.events               :as events]
   [mateuszmazurczak.ports.navigation           :as navigation]
   [mateuszmazurczak.ports.state                :as state]
   [mateuszmazurczak.ui.components.admin        :as admin]
   [mateuszmazurczak.ui.components.button       :as ui-button]
   [mateuszmazurczak.ui.components.footer       :as mm-ui-footer]
   [mateuszmazurczak.ui.components.header       :as mm-ui-header]
   [mateuszmazurczak.ui.components.sheet        :as ui-sheet]
   [mateuszmazurczak.ui.components.theme-toggle :as theme-toggle]))

(defn structure
  [{:keys [header footer class]} & components]
  [:div {:class (vec (concat class ["h-fit min-h-screen flex flex-col relative bg-background"]))}
   header
   (into [:div {:class ["grow"]}]
         (for [comp components] comp))
   footer])

(defn- nav-link
  "Single navigation link that subscribes to active route state directly.
  This ensures instant highlighting when route changes."
  [{:keys [title href route-name class active-class inactive-class attrs]
    :or {class "text-sm font-semibold leading-6 transition-colors"
         active-class "text-primary hover:text-primary/80"
         inactive-class "text-foreground hover:text-foreground/80"
         attrs {}}}]
  (let [active? @(state/watch [:nav/active-route? route-name])
        attrs-class (get attrs :class)
        combined-class (str class " " (if active? active-class inactive-class)
                            (when attrs-class (str " " attrs-class)))]
    [:a (merge attrs {:href href
                      :class combined-class})
     title]))

(defc structure-sheet [menu-items]
  (r/with-let [open? (r/atom false)]
    [:> ui-sheet/sheet {:open @open?
                        :on-open-change #(reset! open? %)}
     [:> ui-sheet/sheet-trigger {:asChild true}
      (ui-button/button {:variant :ghost
                         :size :icon
                         :class "lg:hidden"}
                        [:> Menu {:class "h-5 w-5"}]
                        [:span {:class "sr-only"}
                         "Open menu"])]
     [ui-sheet/sheet-content {:side :right
                              :class "w-72"}
      [ui-sheet/sheet-header {}
       [ui-sheet/sheet-title {}
        "Menu"]]
      [:nav {:class "mt-6 flex flex-col gap-4"}
       (for [{:keys [title href route-name]} menu-items]
         ^{:key href}
         [nav-link {:title title
                    :href href
                    :route-name route-name
                    :class "text-base font-semibold transition-colors"
                    :active-class "text-primary hover:text-primary/80"
                    :inactive-class "text-foreground hover:text-foreground/80"
                    :attrs {:on-click #(reset! open? false)}}])]]]))

(defc header
  "Main header component with navigation menu, theme toggle, language selector, and admin badge.
  
  Each nav link subscribes directly to its active state for instant highlighting.
  Includes scroll-based border toggling for a dynamic appearance.
  The border appears/disappears based on scroll position.
  
  Props:
  - :size (:full | :half) - Header width (default: :full)
  - :border? (boolean) - Initial border state (default: true)
  - :sticky? (boolean) - Sticky positioning (default: true)
  - :class - Additional CSS classes
  - :admin-logged-in? (boolean) - Whether admin is logged in
  - :admin-text - Map with :admin-mode and :admin-logout keys
  - :on-admin-logout - Admin logout handler function
  
  Menu items:
  - Mateusz Mazurczak (home)
  - Articles
  - AoC Solutions
  - QR Codes"
  [{:keys [_size _border? _sticky? _class _admin-logged-in? _admin-text _on-admin-logout]}]
  (r/create-class
   {:component-did-mount (fn [_]
                           (.addEventListener js/window "scroll" mm-ui-header/toggle-header-border))
    :component-will-unmount
    (fn [_] (.removeEventListener js/window "scroll" mm-ui-header/toggle-header-border))
    :reagent-render (fn [{:keys
                          [size border? sticky? class admin-logged-in? admin-text on-admin-logout]}]
                      (let [menu-items [{:title "Mateusz Mazurczak"
                                         :href (navigation/href ::mm-routes/home)
                                         :route-name ::mm-routes/home}
                                        {:title (fi18n/tr :articles)
                                         :href (navigation/href ::mm-routes/articles)
                                         :route-name ::mm-routes/articles}
                                        {:title (fi18n/tr :aoc-solutions)
                                         :href (navigation/href ::mm-routes/aoc)
                                         :route-name ::mm-routes/aoc}
                                        {:title (fi18n/tr :qr-codes)
                                         :href (navigation/href ::mm-routes/qr-codes)
                                         :route-name ::mm-routes/qr-codes}]]
                        [mm-ui-header/base-header {:size size
                                                   :sticky? sticky?
                                                   :border? border?
                                                   :class class}
                         [:nav {:class "flex items-center justify-between px-6 lg:px-8"}
                          nil ;; no logo
                          [:div {:class "hidden lg:flex lg:gap-x-12"}
                           (for [{:keys [title href route-name]} menu-items]
                             ^{:key href}
                             [nav-link {:title title
                                        :href href
                                        :route-name route-name}])]
                          [:div {:class "flex items-center gap-4"}
                           (when admin-logged-in?
                             [admin/admin-badge {:text admin-text
                                                 :on-logout on-admin-logout}])
                           [structure-sheet menu-items]
                           
                           [theme-toggle/theme-toggle]
                           [mm-ui-header/lang-select]]]]))}))

(defn mateuszmazurczak-page-structure
  "Page structure with header and footer."
  [& components]
  (let [admin-logged-in? @(state/watch [:admin/logged-in?])
        admin-text {:admin-mode (fi18n/tr :admin-mode)
                    :admin-logout (fi18n/tr :admin-logout)}]
    (apply structure
           {:header [header {:size :full
                             :sticky? true
                             :border? true
                             :admin-logged-in? admin-logged-in?
                             :admin-text admin-text
                             :on-admin-logout #(events/dispatch! [:admin/logout])}]
            :footer [mm-ui-footer/footer]}
           components)))
