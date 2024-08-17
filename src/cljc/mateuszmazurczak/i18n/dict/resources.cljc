(ns mateuszmazurczak.i18n.dict.resources
  "Dictionary to translate resources for mateuszmazurczak project"
  (:require
   [mateuszmazurczak.ui.generated.about-us            :as about-us]
   [mateuszmazurczak.ui.generated.additional-outcomes :as additional-outcomes]
   [mateuszmazurczak.ui.generated.commitment          :as commitment]
   [mateuszmazurczak.ui.generated.network             :as network]
   [mateuszmazurczak.ui.generated.our-brand           :as our-brand]
   [mateuszmazurczak.ui.generated.our-values          :as our-values]
   [mateuszmazurczak.ui.generated.simulation          :as sim]))

(def dict
  "Dictionary to translate resources"
  {:en {:elevator-pitch "https://www.youtube.com/embed/36gLLI8IJfY"
        :simulation-page sim/notion-generated-simulation-en
        :simulation-page-preview "img/preview/simulation-en.png"
        :simulation-pitch-page sim/notion-generated-simulation-pitch-en
        :simulation-pitch-page-preview "img/preview/simulation-pitch.jpg"
        :about-us-page about-us/notion-generated-about-us-en
        :about-us-page-preview "img/preview/about-us-en.png"
        :our-brand-page our-brand/notion-generated-our-brand-en
        :our-brand-page-preview "img/preview/our-brand.png"
        :network-page network/notion-generated-network-en
        :network-page-preview "img/preview/network.jpg"
        :commitment-page commitment/notion-generated-commitment-en
        :commitment-page-preview "img/preview/commitment.jpg"
        :additional-outcomes-page
        additional-outcomes/notion-generated-additional-outcomes-en
        :additional-outcomes-page-preview "img/preview/additional-outcomes.jpg"
        :our-values-page our-values/notion-generated-our-values-en
        :our-values-page-preview "img/preview/our-brand.png"
        :page-preview "img/preview/en.png"}
   :fr {:elevator-pitch "https://www.youtube.com/embed/4NgrOHY1Yvs"
        :simulation-page sim/notion-generated-simulation-fr
        :simulation-page-preview "img/preview/simulation-fr.png"
        :simulation-pitch-page sim/notion-generated-simulation-pitch-fr
        :simulation-pitch-page-preview "img/preview/simulation-pitch.jpg"
        :about-us-page about-us/notion-generated-about-us-fr
        :about-us-page-preview "img/preview/about-us-fr.png"
        :our-brand-page our-brand/notion-generated-our-brand-fr
        :our-brand-page-preview "img/preview/our-brand.png"
        :network-page network/notion-generated-network-fr
        :network-page-preview "img/preview/network.jpg"
        :commitment-page commitment/notion-generated-commitment-fr
        :commitment-page-preview "img/preview/commitment.jpg"
        :additional-outcomes-page
        additional-outcomes/notion-generated-additional-outcomes-fr
        :additional-outcomes-page-preview "img/preview/additional-outcomes.jpg"
        :our-values-page our-values/notion-generated-our-values-fr
        :our-values-page-preview "img/preview/our-brand.png"
        :page-preview "img/preview/fr.png"}})
