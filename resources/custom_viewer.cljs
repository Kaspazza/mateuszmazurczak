(ns custom-viewer
  "Working example of custom viewer, I can't spend to much time now, but may be very useful in future to filter logs in portal"
  (:require
   ;; [shadow.resource     :refer [inline]] ;; for hot reloading
   [portal.colors              :as c]
   ;; [portal.resources    :refer [inline]]
   [portal.ui.api              :as p]
   [portal.ui.filter           :as-alias f]
   [portal.ui.inspector        :as ins]
   [portal.ui.rpc              :as rpc]
   [portal.ui.styled           :as d]
   [portal.ui.theme            :as theme]
   [portal.ui.select           :as select]
   [portal.ui.viewer.date-time :as date-time]))
(defn- parse
  [xml-string]
  (let [parser (js/DOMParser.)
        dom (.parseFromString parser xml-string "text/xml")]
    (aget (.getElementsByTagName dom "svg") 0)))

(defn- stringify [dom] (.serializeToString (js/XMLSerializer.) dom))

(defn- resolve-color
  [color]
  (if-let [[_ var] (re-matches #"var\((.*)\)" color)]
    (-> js/document
        .-documentElement
        js/getComputedStyle
        (.getPropertyValue var))
    color))

(defn- theme-svg
  [svg color]
  (let [color (resolve-color color)]
    (doseq [el (.querySelectorAll svg "[fill]")]
      (.setAttribute el "fill" color))
    (doseq [el (.querySelectorAll svg "[stroke]")]
      (.setAttribute el "stroke" color)))
  svg)

(def ^:private runtime->logo
  {:clj {:color ::c/package
         :title "Clojure"}
   :cljr {:color ::c/string
          :title "Clojure CLR"}
   :cljs {:color ::c/tag
          :title "ClojureScript"}
   :bb {:color ::c/exception
        :title "Babashka"}
   :nbb {:color ::c/diff-add
         :title "Node Babashka"}
   :portal {:color ::c/boolean
            :title "Portal"}
   :joyride {:color ::c/exception
             :title "Joyride"}})

(defn icon
  ([value]
   (let [theme (theme/use-theme)]
     [icon value (get theme (get-in runtime->logo [value :color] ::c/text))]))
  ([value color]
   (let [{:keys [icon title]} (runtime->logo value)]
     (when icon
       [d/img {:title title
               :style {:height 22
                       :width 22}
               :src (str "data:image/svg+xml;base64,"
                         (-> icon
                             parse
                             (theme-svg color)
                             stringify
                             js/btoa))}]))))

;;; :spec
(def ^:private levels [:trace :debug :info :warn :error :fatal :report])


;;;

(def ^:private malli-spec
  [:map {:closed false}
   [:id :any]
   [:level [:enum [:trace :debug :info :warn :error :fatal :report]]]])

(defn log? [value] (rpc/call 'malli.core/validate malli-spec value))

(def ^:private level->color
  {:trace ::c/text
   :debug ::c/string
   :info ::c/boolean
   :warn ::c/tag
   :error ::c/exception
   :fatal ::c/exception
   :report ::c/border})

(defn inspect-source
  [value]
  (let [theme (theme/use-theme)]
    [d/div {:on-click (fn [e]
                        (.stopPropagation e)
                        (rpc/call 'portal.runtime.jvm.editor/goto-definition
                                  value))
            :style/hover {:opacity 1
                          :text-decoration :underline}
            :style {:opacity 0.75
                    :cursor :pointer
                    :color (::c/uri theme)}}
     [ins/highlight-words
      (str (or (:label value) (:ns value) (:file value)) ":" (:line value))]]))


(defn custom-view
  [log]
  (let [theme (theme/use-theme)
        background (ins/get-background)
        color (-> log
                  :level
                  level->color
                  theme)
        runtime (:runtime log)
        runtime? (contains? runtime->logo runtime)
        options (ins/use-options)
        expanded? (:expanded? options)
        border (cond-> {:border-top [1 :solid (::c/border theme)]}
                 (not expanded?) (assoc :border-bottom
                                        [1 :solid (::c/border theme)]))
        flex {:box-sizing :border-box
              :padding (:padding theme)
              :display :flex
              :align-items :center}]
    [d/div {:style {:background background}}
     [d/div {:style {:display :grid
                     :grid-template-columns
                     (if-not runtime?
                       "auto auto 1fr auto auto auto"
                       " auto auto 1fr auto auto auto auto")
                     :border-left [5 :solid color]
                     :border-top-left-radius (:border-radius theme)
                     :border-bottom-left-radius (when-not expanded?
                                                  (:border-radius theme))}}
      [ins/toggle-expand {:style (merge {:padding-left (:padding theme)}
                                        border)}]
      [d/div {:style (merge flex border)}
       [date-time/inspect-time (:time log)]]
      [d/div {:style (merge flex
                            {:border-top [1 :solid (::c/border theme)]
                             :flex "1"}
                            border)}
       [select/with-position {:row -1
                              :column 0}
        [ins/with-collection
         log
         [ins/with-key :result [ins/dec-depth [ins/inspector (:result log)]]]]]]
      [d/div {:style (merge flex
                            {:border-top [1 :solid (::c/border theme)]
                             :justify-content :flex-end}
                            border
                            (when-not runtime?
                              {:border-right [1 :solid (::c/border theme)]
                               :border-top-right-radius (:border-radius theme)
                               :border-bottom-right-radius (:border-radius
                                                            theme)}))}
       [inspect-source log]]
      [d/button {:style {:margin-right "5px"
                         :margin-left "2px"}
                 :on-click (fn [e]
                             (.stopPropagation e)
                             (rpc/call 'xacto.logging/id-filter (:result log)))}
       "ID"]
      [d/button {:on-click (fn [e]
                             (.stopPropagation e)
                             (rpc/call 'xacto.logging/ns-filter (:ns log)))}
       "NS"]
      (when runtime?
        [d/div {:style (merge {:padding (* 0.5 (:padding theme))
                               :display :flex
                               :align-items :center
                               :color (::c/uri theme)
                               :border-top [1 :solid (::c/border theme)]
                               :border-left [1 :solid (::c/border theme)]
                               :border-right [1 :solid (::c/border theme)]
                               :border-top-right-radius (:border-radius theme)
                               :border-bottom-right-radius
                               (when-not expanded? (:border-radius theme))}
                              border)}
         [icon runtime]])]
     (when (:expanded? options)
       [ins/with-collection
        log
        [ins/inspect-map-k-v
         (dissoc log :level :result :line :column :ns :runtime)]])]))

(def viewer
  {:predicate log?
   :component #'custom-view
   :name :lekta.viewer/log-viewer
   :doc
   "Useful for conveying a value in a specific context (what/where/when)."})

(p/register-viewer! viewer)
