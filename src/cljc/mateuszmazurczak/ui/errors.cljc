(ns mateuszmazurczak.ui.errors
  (:require
   [mateuszmazurczak.ui.nav :as mm-ui-nav]))

(defn not-found
  [{:keys [title description back-home-text back-link]
    :or {back-link "/"}}]
  [:main {:class ["relative isolate min-h-full h-full"]}
   [:div {:class
          ["before:content-[''] before:absolute before:h-full before:w-full before:bg-black/[.5]"]}
    [:img {:src "/img/not_found.webp"
           :alt ""
           :width 1920
           :height 1080
           :loading "eager"
           :decoding "async"
           :class ["absolute inset-0 -z-10 h-full w-full object-cover object-top "]}]]
   [:div {:class ["absolute left-1/2 top-1/2 text-center -translate-x-1/2 -translate-y-1/2"]}
    [:p {:class ["text-base font-semibold leading-8 text-white text-border-red"]}
     "404"]
    [:h1 {:class
          ["mt-4 text-3xl font-bold tracking-tight text-white sm:text-5xl text-border-red-thin"]}
     title]
    [:p {:class ["mt-4 text-base text-white/70 sm:mt-6 text-border-black"]}
     description]
    [:div {:class ["mt-10 flex justify-center text-border-black"]}
     (mm-ui-nav/back-navigation {:href back-link
                                 :text back-home-text
                                 :dark? false})]]])

(defn internal-error
  [{:keys [title description back-home-text back-link]
    :or {back-link "/"}}]
  [:main {:class ["relative isolate min-h-full h-full"]}
   [:div {:class
          ["before:content-[''] before:absolute before:h-full before:w-full before:bg-black/[.5]"]}
    [:img {:src "/img/not_found.webp"
           :alt ""
           :width 1920
           :height 1080
           :loading "eager"
           :decoding "async"
           :class ["absolute inset-0 -z-10 h-full w-full object-cover object-top "]}]]
   [:div {:class ["absolute left-1/2 top-1/2 text-center -translate-x-1/2 -translate-y-1/2"]}
    [:p {:class ["text-base font-semibold leading-8 text-white text-border-red"]}
     "500"]
    [:h1 {:class
          ["mt-4 text-3xl font-bold tracking-tight text-white sm:text-5xl text-border-red-thin"]}
     title]
    [:p {:class ["mt-4 text-base text-white/70 sm:mt-6 text-border-black"]}
     description]
    [:div {:class ["mt-10 flex justify-center text-border-black"]}
     (mm-ui-nav/back-navigation {:href back-link
                                 :text back-home-text
                                 :dark? false})]]])
