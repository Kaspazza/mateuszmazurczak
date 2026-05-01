(ns mateuszmazurczak.portfolio.portfolio
  (:require
   [mateuszmazurczak.portfolio.mateuszmazurczak.home]
   [mateuszmazurczak.portfolio.application.admin]
   [mateuszmazurczak.portfolio.application.app-skeleton]
   [mateuszmazurczak.portfolio.ui-components.avatar]
   [mateuszmazurczak.portfolio.ui-components.badge]
   [mateuszmazurczak.portfolio.ui-components.breadcrumb]
   [mateuszmazurczak.portfolio.ui-components.button]
   [mateuszmazurczak.portfolio.ui-components.carousel]
   [mateuszmazurczak.portfolio.ui-components.chat-container]
   [mateuszmazurczak.portfolio.ui-components.checkbox]
   [mateuszmazurczak.portfolio.ui-components.code-block]
   [mateuszmazurczak.portfolio.ui-components.collapsible]
   [mateuszmazurczak.portfolio.ui-components.command]
   [mateuszmazurczak.portfolio.ui-components.data-table]
   [mateuszmazurczak.portfolio.ui-components.dialog]
   [mateuszmazurczak.portfolio.ui-components.drawer]
   [mateuszmazurczak.portfolio.ui-components.dropdown-menu]
   [mateuszmazurczak.portfolio.ui-components.empty]
   [mateuszmazurczak.portfolio.ui-components.field]
   [mateuszmazurczak.portfolio.application.footer]
   [mateuszmazurczak.portfolio.application.header]
   [mateuszmazurczak.portfolio.application.image]
   [mateuszmazurczak.portfolio.ui-components.input]
   [mateuszmazurczak.portfolio.ui-components.label]
   [mateuszmazurczak.portfolio.ui-components.loader]
   [mateuszmazurczak.portfolio.ui-components.markdown]
   [mateuszmazurczak.portfolio.ui-components.message]
   [mateuszmazurczak.portfolio.application.navigation]
   [mateuszmazurczak.portfolio.ui-components.notification]
   [mateuszmazurczak.portfolio.ui-components.popover]
   [mateuszmazurczak.portfolio.ui-components.prompt-input]
   [mateuszmazurczak.portfolio.ui-components.radio-group]
   [mateuszmazurczak.portfolio.ui-components.scroll-button]
   [mateuszmazurczak.portfolio.ui-components.select]
   [mateuszmazurczak.portfolio.ui-components.separator]
   [mateuszmazurczak.portfolio.ui-components.sheet]
   [mateuszmazurczak.portfolio.ui-components.sidebar]
   [mateuszmazurczak.portfolio.ui-components.skeleton]
   [mateuszmazurczak.portfolio.ui-components.speech-recognition-button]
   [mateuszmazurczak.portfolio.ui-components.spinner]
   [mateuszmazurczak.portfolio.ui-components.stepper]
   [mateuszmazurczak.portfolio.ui-components.switch]
   [mateuszmazurczak.portfolio.ui-components.system-message]
   [mateuszmazurczak.portfolio.ui-components.table]
   [mateuszmazurczak.portfolio.ui-components.tag-combobox]
   [mateuszmazurczak.portfolio.ui-components.textarea]
   [mateuszmazurczak.portfolio.ui-components.theme-toggle]
   [mateuszmazurczak.portfolio.ui-components.tooltip]
   [portfolio.data      :as data]
   [portfolio.ui        :as ui]
   [portfolio.ui.search :as search]))

(defonce app
  (ui/start!
   {:config {:css-paths ["/css/compiled/styles.css"]
             :viewport/options [{:title "<sm | small phone"
                                 :value {:viewport/width 390
                                         :viewport/height 640}}
                                {:title "Tailwind sm (iPhone)"
                                 :value {:viewport/width 640
                                         :viewport/height 1136}}
                                {:title "Tailwind md (iPad Mini)"
                                 :value {:viewport/width 768
                                         :viewport/height 1024}}
                                {:title "Tailwind lg (iPad Pro)"
                                 :value {:viewport/width 1024
                                         :viewport/height 1366}}
                                {:title "Tailwind xl (laptop)"
                                 :value {:viewport/width 1280
                                         :viewport/height 800}}
                                {:title "Tailwind 2xl (Macbook Pro)"
                                 :value {:viewport/width 1536
                                         :viewport/height 960}}
                                {:title ">2xl | Big monitor"
                                 :value {:viewport/width 2560
                                         :viewport/height 1440}}
                                {:title "Auto"
                                 :value {:viewport/width "100%"
                                         :viewport/height "100%"}}]
             ;; Default - tailwind sm
             :viewport/defaults {:viewport/width 640
                                 :viewport/height 1136}}
    :index (search/create-index)}))

(defn init [] app)

(data/register-collection! :mateuszmazurczak {:title "Mateuszmazurczak"})
