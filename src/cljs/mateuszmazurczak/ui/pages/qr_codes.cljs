(ns mateuszmazurczak.ui.pages.qr-codes
  (:require
   ["lucide-react"                          :refer [Download QrCode RefreshCw]]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.input    :as input]
   [mateuszmazurczak.ui.components.label    :as label]
   [mateuszmazurczak.ui.components.select   :as select]
   [mateuszmazurczak.ui.components.switch   :as switch]
   [mateuszmazurczak.ui.components.textarea :as textarea]))

(defn- qr-svg
  "Render QR code as SVG element using viewBox for scaling."
  [{:keys [viewbox background foreground path label]} display-size]
  (let [has-label? (and label (not (empty? label)))]
    [:div {:class "flex flex-col items-center gap-2"}
     [:svg {:xmlns "http://www.w3.org/2000/svg"
            :viewBox viewbox
            :width display-size
            :height display-size
            :class "rounded"}
      [:rect {:width "100%"
              :height "100%"
              :fill background}]
      [:path {:d path
              :fill foreground}]]
     (when has-label?
       [:div {:class "text-xs text-center font-medium max-w-[150px] break-words"}
        label])]))

(defn- qr-preview-card
  "Single QR code preview card."
  [{:keys [svg-data preview-display-size]}]
  [:div {:class "flex flex-col items-center p-4 border rounded-lg bg-card"}
   [qr-svg svg-data preview-display-size]])

(defn- preview-section
  "Preview section showing generated QR codes."
  [{:keys [preview-codes preview-count input-count showing-preview? preview-display-size text]}]
  (when showing-preview?
    [:div {:class "space-y-4"}
     [:div {:class "flex items-center justify-between"}
      [:h3 {:class "text-lg font-semibold"}
       (:preview text)]
      [:span {:class "text-sm text-muted-foreground"}
       (:showing-preview-count text)]]
     [:div {:class "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4"}
      (for [{:keys [svg-data filename]} preview-codes]
        ^{:key filename}
        [qr-preview-card {:svg-data svg-data
                          :preview-display-size preview-display-size}])]
     (when (> input-count preview-count)
       [:p {:class "text-sm text-muted-foreground text-center"}
        (:more-codes-hidden text)])]))

(defn- error-display
  "Display validation errors."
  [{:keys [errors text]}]
  (when (seq errors)
    [:div {:class "p-4 border border-destructive/50 bg-destructive/10 rounded-lg"}
     [:h4 {:class "font-semibold text-destructive mb-2"}
      (:errors text)]
     [:ul {:class "list-disc list-inside text-sm text-destructive"}
      (for [[idx error] (map-indexed vector errors)] ^{:key idx} [:li error])]]))

(defn- size-selector
  "QR code size selector."
  [{:keys [size size-options text handlers]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "size"}
    (:qr-code-size text)]
   [select/select {:value (str size)
                   :on-value-change #((:on-update-size handlers) (js/parseInt % 10))}
    [select/select-trigger {:id "size"
                            :class "w-full"}
     [select/select-value {:placeholder (:select-size text)}]]
    [select/select-content {}
     (for [{:keys [value label]} size-options]
       ^{:key value}
       [select/select-item {:value (str value)}
        label])]]])

(defn- format-selector
  "Output format selector."
  [{:keys [format format-options text handlers]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "format"}
    (:output-format text)]
   [select/select {:value (name format)
                   :on-value-change #((:on-update-format handlers) (keyword %))}
    [select/select-trigger {:id "format"
                            :class "w-full"}
     [select/select-value {:placeholder (:select-format text)}]]
    [select/select-content {}
     (for [{:keys [value label]} format-options]
       ^{:key value}
       [select/select-item {:value (name value)}
        label])]]])

(defn- png-options
  "Image export options for background and quiet-zone margin."
  [{:keys [format text handlers png-background png-margin]}]
  (when (contains? #{:zip :jpg} format)
    [:div {:class "space-y-3"}
     (when (= format :zip)
       [:div {:class "space-y-2"}
        [label/label {:htmlFor "png-background"}
         (:png-background text)]
        [select/select {:value (name png-background)
                        :on-value-change #((:on-update-png-background handlers) (keyword %))}
         [select/select-trigger {:id "png-background"
                                 :class "w-full"}
          [select/select-value {:placeholder (:select-png-background text)}]]
         [select/select-content {}
          [select/select-item {:value "transparent"}
           (:png-background-transparent text)]
          [select/select-item {:value "white"}
           (:png-background-white text)]]]])
     (when (or (= format :jpg) (and (= format :zip) (= png-background :white)))
       [:div {:class "space-y-2"}
        [label/label {:htmlFor "png-margin"}
         (:png-margin text)]
        [input/input {:id "png-margin"
                      :type "number"
                      :min 0
                      :step 1
                      :value png-margin
                      :on-change #((:on-update-png-margin handlers) (.. % -target -value))}]
        [:p {:class "text-xs text-muted-foreground"}
         (:png-margin-description text)]])]))

(defn- pdf-layout-selector
  "PDF layout controls (always visible for PDF format)."
  [{:keys [format text handlers pdf-custom-cols pdf-custom-rows pdf-custom-qr-size-cm]}]
  (when (= format :pdf)
    [:div {:class "space-y-3"}
     [:p {:class "text-xs text-muted-foreground"}
      (:pdf-layout-description text)]
     [:div {:class "grid grid-cols-1 sm:grid-cols-3 gap-3"}
      [:div {:class "space-y-2"}
       [label/label {:htmlFor "pdf-custom-cols"}
        (:pdf-custom-cols text)]
       [input/input {:id "pdf-custom-cols"
                     :type "number"
                     :min 1
                     :step 1
                     :value pdf-custom-cols
                     :on-change #((:on-update-pdf-custom handlers) :cols (.. % -target -value))}]]
      [:div {:class "space-y-2"}
       [label/label {:htmlFor "pdf-custom-rows"}
        (:pdf-custom-rows text)]
       [input/input {:id "pdf-custom-rows"
                     :type "number"
                     :min 1
                     :step 1
                     :value pdf-custom-rows
                     :on-change #((:on-update-pdf-custom handlers) :rows (.. % -target -value))}]]
      [:div {:class "space-y-2"}
       [label/label {:htmlFor "pdf-custom-size"}
        (:pdf-custom-size-cm text)]
       [input/input {:id "pdf-custom-size"
                     :type "number"
                     :min 0.5
                     :step 0.1
                     :value pdf-custom-qr-size-cm
                     :on-change
                     #((:on-update-pdf-custom handlers) :qr-size-cm (.. % -target -value))}]]]]))

(defn- label-toggle
  "Toggle to show QR code value as label."
  [{:keys [show-label? text handlers]}]
  [:div {:class "flex items-center justify-between"}
   [:div {:class "space-y-0.5"}
    [label/label {:htmlFor "show-label"}
     (:show-label text)]
    [:p {:class "text-sm text-muted-foreground"}
     (:show-label-description text)]]
   [switch/switch {:id "show-label"
                   :checked show-label?
                   :on-checked-change (:on-update-show-label handlers)}]])

(defn- input-section
  "Input textarea section."
  [{:keys [input input-hint text handlers]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "qr-input"}
    (:qr-code-values text)]
   [textarea/textarea {:id "qr-input"
                       :value input
                       :on-change #((:on-update-input handlers)
                                     (-> %
                                         .-target
                                         .-value))
                       :placeholder (:enter-values-placeholder text)
                       :rows 10
                       :auto-size? false
                       :class "font-mono text-sm"}]
   [:p {:class "text-sm text-muted-foreground"}
    input-hint]])

(defn- action-buttons
  "Generate preview and download buttons."
  [{:keys [has-input? can-download? loading? text handlers download-progress]}]
  [:div {:class "flex flex-col gap-2"}
   [:div {:class "flex flex-col sm:flex-row gap-3"}
    (button/button {:variant :outline
                    :disabled (not has-input?)
                    :on-click (:on-generate-preview handlers)
                    :class "flex-1"}
                   [:> RefreshCw {:class "size-4 mr-2"}]
                   (:generate-preview text))
    (button/button {:variant :default
                    :disabled (not can-download?)
                    :on-click (:on-download handlers)
                    :class "flex-1"}
                   [:> Download {:class (str "size-4 mr-2" (when loading? " animate-spin"))}]
                   (:download text))]
   (when (and download-progress (:generating-qr-codes text))
     [:p {:class "text-sm text-muted-foreground"}
      (:generating-qr-codes text)])])

(defn qr-codes-page
  "QR Code Generator page."
  [data]
  (let [{:keys [text]} data]
    [:div {:class "container mx-auto px-4 py-8 max-w-4xl"}
     [:div {:class "flex items-center gap-3 mb-8"}
      [:div {:class "p-3 rounded-lg bg-primary/10"}
       [:> QrCode {:class "size-8 text-primary"}]]
      [:div
       [:h1 {:class "text-3xl font-bold"}
        (:title text)]
       [:p {:class "text-muted-foreground"}
        (:description text)]]]
     [:div {:class "space-y-6"}
      [:div {:class "p-6 border rounded-lg bg-card"}
       [input-section data]]
      [:div {:class "p-6 border rounded-lg bg-card space-y-6"}
       [format-selector data]
       (when (contains? #{:zip :jpg} (:format data)) [size-selector data])
       [png-options data]
       [pdf-layout-selector data]
       [label-toggle data]]
      [error-display data]
      [:div {:class "p-6 border rounded-lg bg-card"}
       [action-buttons data]]
      [preview-section data]]]))
