(ns mateuszmazurczak.ui.pages.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.ui.articles              :as ui-articles]
   [mateuszmazurczak.ui.components.image      :as ui-img]
   [mateuszmazurczak.ui.components.navigation :as mm-ui-navigation]))

(defn about-me
  [{:keys [welcome-text description contact-info]}]
  [:div {:class ["w-full"
                 "overflow-hidden"
                 "relative"
                 "bg-gradient-to-r"
                 "from-[oklch(0.6270_0.2650_303.9/0.75)]"
                 "via-[oklch(0.6270_0.2650_303.9/0.60)]"
                 "to-[oklch(0.8600_0.030_300/0.85)]"
                 "bg-cover"
                 "bg-center"
                 "bg-no-repeat"
                 "before:content-['']"
                 "before:absolute"
                 "before:inset-[-8%_-4%_0]"
                 "before:bg-[url('/img/code.webp')]"
                 "before:bg-cover"
                 "before:bg-[60%_40%]"
                 "before:bg-no-repeat"
                 "before:opacity-10"
                 "before:pointer-events-none"
                 "before:[transform:perspective(500px)_rotateX(6deg)_skewY(-2deg)]"
                 "before:origin-top"]}
   [:div {:class ["relative"
                  "flex"
                  "flex-col"
                  "lg:flex-row"
                  "items-center"
                  "lg:items-end"
                  "gap-4"
                  "lg:gap-8"
                  "mx-auto"
                  "max-w-6xl"
                  "px-6"
                  "lg:px-12"
                  "pt-4"
                  "lg:pt-8"
                  "pb-2"
                  "text-center"
                  "lg:text-left"]}
    [:div
     {:class
      "block overflow-hidden self-center lg:self-end mx-auto lg:mx-0 -mb-4 lg:-mb-10 w-64 lg:w-[24rem]"}
     [ui-img/optimized-img {:src "/img/mateusz_mazurczak.webp"
                            :alt "Mateusz Mazurczak"
                            :width 512
                            :height 512
                            :loading "eager"
                            :fetchpriority "high"
                            :class "w-full h-auto"}]]
    [:div {:class "lg:self-center"}
     [:h1 {:class "text-2xl md:text-3xl lg:text-5xl font-bold text-accent text-shadow-lg"}
      welcome-text]
     [:p {:class "py-6 text-md md:text-xl lg:text-2xl text-accent text-shadow-lg"}
      description
      [:br]]
     [:p {:class "py-6 text-md md:text-xl lg:text-2xl text-accent text-shadow-lg"}
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
