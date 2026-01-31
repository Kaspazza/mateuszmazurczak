(ns mateuszmazurczak.ui.pages.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.ui.articles              :as ui-articles]
   [mateuszmazurczak.ui.components.image      :as ui-img]
   [mateuszmazurczak.ui.components.navigation :as mm-ui-navigation]))

(defn about-me
  [{:keys [welcome-text description contact-info]}]
  [:div {:class ["w-full"
                 "bg-gradient-to-r"
                 "from-primary/60"
                 "via-primary/40"
                 "to-background"]}
   [:div {:class ["flex"
                  "flex-col"
                  "lg:flex-row"
                  "items-center"
                  "gap-8"
                  "lg:gap-16"
                  "mx-auto"
                  "max-w-6xl"
                  "px-6"
                  "lg:px-12"
                  "py-10"
                  "lg:py-16"
                  "text-center"
                  "lg:text-left"]}
    [:div {:class "block overflow-hidden w-80"}
     [ui-img/optimized-img {:src "/img/mateusz_mazurczak.webp"
                            :alt "Mateusz Mazurczak"
                            :width 320
                            :height 320
                            :loading "eager"
                            :fetchpriority "high"
                            :class "w-full h-auto"}]]
    [:div
     [:h1 {:class "text-2xl md:text-3xl lg:text-5xl font-bold text-foreground"}
      welcome-text]
     [:p {:class "py-6 text-md md:text-xl lg:text-3xl text-foreground"}
      description
      [:br]]
     [:p {:class "py-6 text-md md:text-xl lg:text-3xl text-foreground"}
      contact-info]]]])

(defn home
  [{:keys [about-me-section navigation articles]}]
  [:div
   [about-me about-me-section]
   [:div {:class ["flex flex-col pt-8 p-12 lg:p-36 lg:pt-14 gap-14"]}
    [:span {:class ["text-4xl/7 font-bold ml-4"]}
     [mm-ui-navigation/navigation {:href (:href navigation)
                                   :text (:text navigation)
                                   :dark? (:dark-mode navigation)}]]
    [:div {:class ["grid justify-items-stretch gap-6 mx-auto w-full"]}
     (doall (for [{:keys [title]
                   :as article}
                  articles]
              ^{:key title} [ui-articles/article-card article]))]]])
