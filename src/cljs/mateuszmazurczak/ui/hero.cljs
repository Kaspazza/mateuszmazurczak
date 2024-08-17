(ns mateuszmazurczak.ui.hero
  (:require
   [automaton-core.utils.uuid-gen   :as uuid-gen]
   [automaton-web.components.button :as web-button]
   [mateuszmazurczak.i18n.fe.translate       :as mateuszmazurczak-fe-translate]))

(def icons
  [{:href "https://www.michelin.com/"
    :src "/img/michelin.png"
    :alt "Michelin"}
   {:href "https://www.cgi.com/"
    :src "/img/unilog.jpeg"
    :alt "Unilog - now cgi"}
   {:href "https://www.stellantis.com/"
    :src "/img/psa.png"
    :alt "PSA Group - now stellantis"}
   {:href "https://www.hocoma.com/"
    :src "/img/hocoma.jpeg"
    :alt "Hocoma logo"}
   {:href "https://www.eramet.com/"
    :src "/img/eramet.png"
    :alt "Eramet logo"}
   {:href "https://amcvibro.com/"
    :src "/img/amc.png"
    :alt "AMC logo"}
   {:href "https://www.afrscm.fr/762_p_44058/bscm.html"
    :src "/img/apics.png"
    :alt "Apics Supply Chain Management certification"}
   {:href "https://theses.hal.science/tel-00713587"
    :src "/img/phd.png"
    :alt "PHD thesis about jobshop optimization"}
   {:href
    "https://www.opengroup.org/certifications/togaf-certification-portfolio"
    :src "/img/togaf.png"
    :alt "University of Clermont Auvergne"}
   {:href
    "https://www.sciencedirect.com/science/article/abs/pii/S0305054806002930"
    :src "/img/cor.jpeg"
    :alt
    "A memetic algorithm for the job-shop with time-lags posted in a Computers & Operations Research"}
   {:href
    "https://www.sciencedirect.com/science/article/abs/pii/S0377221708004608"
    :src "/img/ejor.png"
    :alt
    "An MILP for scheduling problems in an FMS with one vehicle posted in a European Journal of Operational Research"}
   {:href "https://www.uca.fr/"
    :src "/img/teacher.png"
    :alt "University of Clermont Auvergne"}])

(defn social-proof-icon
  [{:keys [href src alt]
    :or {href ""
         src ""
         alt ""}}]
  [:a {:href href
       :target "_blank"}
   [:img
    {:src src
     :alt alt
     :width "158"
     :height "48"
     :class
     ["col-span-2 max-h-16 lg:max-h-12 w-full object-contain lg:col-span-1 grayscale hover:grayscale-0"]}]])

(defn social-proof-container
  [{:keys [text]} & icons-props]
  [:div {:class
         ["mx-auto max-w-7xl px-6 lg:px-8 flex flex-col flex-1 gap-4 lg:gap-0"]}
   [:h2 {:class ["text-center leading-8 text-gray-500 text-base"]}
    text]
   [:div
    {:class
     ["mx-auto grid max-w-lg grid-cols-2 items-center gap-x-8 gap-y-10 sm:max-w-xl sm:gap-x-10 lg:mx-0 lg:max-w-none lg:grid-cols-3 grow items-center mt-0 md:mt-4"]}
    (for [props icons-props] ^{:key props} [social-proof-icon props])]])

(defn social-proof
  [& icon-groups]
  [:div {:class ["py-8 flex flex-col md:flex-row gap-8 md:gap-0"]}
   (for [icon-group icon-groups]
     ^{:key (uuid-gen/unguessable)}
     [apply
      social-proof-container
      {:text (:text icon-group)}
      (:icons icon-group)])])

(defn main-picture
  []
  [:div
   {:class
    ["hidden lg:block bg-gray-50 lg:absolute lg:inset-y-0 lg:right-0 lg:w-1/2 min-h-full max-h-[100vh]"]}
   [:div
    {:class
     ["h-full relative before:content-[''] before:absolute before:h-full before:w-full before:bg-black/[.4]"]}
    [:img
     {:class
      ["block aspect-[3/2] object-cover lg:aspect-auto h-full lg:h-full lg:w-full"]
      :src "/img/factory.jpg"
      :alt
      "Hephaistox consulting in factory, picture of a experienced man in factory talking to engineers."}]]])

(defn title
  []
  [:h1
   {:class
    ["flex flex-col text-black font-bold leading-tight lg:leading-none w-max m-auto pt-20 lg:pt-5 text-3xl lg:text-4xl 2xl:text-5xl"]}
   [:span (mateuszmazurczak-fe-translate/tr :reduce-costs)]
   [:span (mateuszmazurczak-fe-translate/tr :optimize-stock)]
   [:span (mateuszmazurczak-fe-translate/tr :improve-client-service)]
   [:span {:class ["text-end mt-2 ml-8 text-primary"]}
    (mateuszmazurczak-fe-translate/tr :no-compromises)]])

(defn subtitle
  []
  [:div {:class ["flex h-full items-center"]}
   [:p
    {:class
     ["flex flex-col text-gray-700 w-0 flex-grow text-justify 2xl:text-xl md:text-lg text-md mt-4 lg:mt-8 gap-4 lg:gap-2"]
     :style {:fontFamily "'Plus Jakarta Sans'"}}
    [:span {:class ["inline-block"]}
     (mateuszmazurczak-fe-translate/tr :hephaistox-offer-description)]
    [:span (mateuszmazurczak-fe-translate/tr :but-first)]]])

(defn cta
  []
  [web-button/link-button
   {:link
    "https://outlook.office365.com/owa/calendar/MatiAnthony@hephaistox.com/bookings/s/jCJNJ2lYPUmwF_13sgEmng2"}
   {:tr :lets-talk
    :text (mateuszmazurczak-fe-translate/tr :lets-talk)}])

(defn page-slope
  []
  [:svg
   {:class
    ["absolute inset-y-0 right-12 hidden h-full w-80 translate-x-1/2 transform fill-white lg:block"]
    :viewBox "0 0 100 100"
    :preserveAspectRatio "none"
    :aria-hidden "true"}
   [:polygon {:points "0,100 0,0 50,0 90,100"}]])

(defn main-section
  []
  [:div
   {:class
    ["flex flex-col inline-block mx-auto mt-0 mb-0 items-center lg:items-stretch gap-4 lg:gap-0 grow  lg:grow-0"]}
   [:div {:class ["grow lg:grow-0 items-center flex"]}
    [title]]
   [:div {:class ["w-full"]}
    [subtitle]]
   [:div {:class ["grow lg:grow-0"]}
    [cta]]])

(defn content-side
  []
  [:div {:class ["relative z-10 lg:w-full lg:max-w-[55%] h-auto"]}
   [page-slope]
   [:div
    {:class
     ["flex flex-col h-full justify-between relative pt-8 sm:pt-16 lg:pt-24 px-6 lg:px-8 lg:pr-0 py-20 lg:py-0"]}
    [main-section]
    [social-proof {:text (mateuszmazurczak-fe-translate/tr :commercial-experience)
                   :icons (take 6 icons)}
     {:text (mateuszmazurczak-fe-translate/tr :theory-experience)
      :icons (drop 6 icons)}]]])

(defn hero-section
  []
  [:div {:class ["relative mt-4 h-fit md:mt-0"]}
   [content-side]
   [main-picture]])
