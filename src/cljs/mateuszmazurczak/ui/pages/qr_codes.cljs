(ns mateuszmazurczak.ui.pages.qr-codes
  "QR Code Generator page UI - pure presentation layer."
  (:require
   ["lucide-react"                          :refer [Download QrCode RefreshCw]]
   [mateuszmazurczak.ports.events           :as events]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.label    :as label]
   [mateuszmazurczak.ui.components.select   :as select]
   [mateuszmazurczak.ui.components.switch   :as switch]
   [mateuszmazurczak.ui.components.textarea :as textarea]))

;; =============================================================================
;; Sub-components
;; =============================================================================

(def ^:private preview-size
  "Size for QR code preview display (actual export uses full size)."
  150)

(defn- qr-svg
  "Render QR code as proper SVG element (no innerHTML needed).
   Uses viewBox for scaling - display-size controls rendered size."
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
  [{:keys [svg-data]}]
  [:div {:class "flex flex-col items-center p-4 border rounded-lg bg-card"}
   [qr-svg svg-data preview-size]])

(defn- preview-section
  "Preview section showing generated QR codes."
  [{:keys [preview-codes preview-count input-count showing-preview?]}]
  (when showing-preview?
    [:div {:class "space-y-4"}
     [:div {:class "flex items-center justify-between"}
      [:h3 {:class "text-lg font-semibold"}
       "Preview"]
      [:span {:class "text-sm text-muted-foreground"}
       (str "Showing " preview-count " of " input-count " codes")]]
     [:div {:class "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4"}
      (for [{:keys [svg-data filename]} preview-codes]
        ^{:key filename}
        [qr-preview-card {:svg-data svg-data}])]
     (when (> input-count preview-count)
       [:p {:class "text-sm text-muted-foreground text-center"}
        (str "... and " (- input-count preview-count) " more codes")])]))

(defn- error-display
  "Display validation errors."
  [errors]
  (when (seq errors)
    [:div {:class "p-4 border border-destructive/50 bg-destructive/10 rounded-lg"}
     [:h4 {:class "font-semibold text-destructive mb-2"}
      "Errors"]
     [:ul {:class "list-disc list-inside text-sm text-destructive"}
      (for [[idx error] (map-indexed vector errors)] ^{:key idx} [:li error])]]))

(defn- size-selector
  "QR code size selector."
  [{:keys [size size-options]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "size"}
    "QR Code Size"]
   [select/select {:value (str size)
                   :on-value-change #(events/dispatch! [:qr-codes/update-size (js/parseInt % 10)])}
    [select/select-trigger {:id "size"
                            :class "w-full"}
     [select/select-value {:placeholder "Select size"}]]
    [select/select-content {}
     (for [{:keys [value label]} size-options]
       ^{:key value}
       [select/select-item {:value (str value)}
        label])]]])

(defn- format-selector
  "Output format selector."
  [{:keys [format format-options]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "format"}
    "Output Format"]
   [select/select {:value (name format)
                   :on-value-change #(events/dispatch! [:qr-codes/update-format (keyword %)])}
    [select/select-trigger {:id "format"
                            :class "w-full"}
     [select/select-value {:placeholder "Select format"}]]
    [select/select-content {}
     (for [{:keys [value label]} format-options]
       ^{:key value}
       [select/select-item {:value (name value)}
        label])]]])

(defn- label-toggle
  "Toggle to show QR code value as label."
  [{:keys [show-label?]}]
  [:div {:class "flex items-center justify-between"}
   [:div {:class "space-y-0.5"}
    [label/label {:htmlFor "show-label"}
     "Show Label"]
    [:p {:class "text-sm text-muted-foreground"}
     "Display QR code value below each code"]]
   [switch/switch
    {:id "show-label"
     :checked show-label?
     :on-checked-change #(events/dispatch! [:qr-codes/update-show-label %])}]])

(defn- input-section
  "Input textarea section."
  [{:keys [input input-count]}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "qr-input"}
    "QR Code Values"]
   [textarea/textarea
    {:id "qr-input"
     :value input
     :on-change #(events/dispatch! [:qr-codes/update-input
                                    (-> %
                                        .-target
                                        .-value)])
     :placeholder
     "Enter one value per line:\n01KD2HHD6WYDAB5YJS9WGFYW3V3687\n01KD2HHD70GDT9BG899TQGY2EF3687\nMy custom text"
     :rows 10
     :class "font-mono text-sm"}]
   [:p {:class "text-sm text-muted-foreground"}
    (if (pos? input-count)
      (str input-count " QR code" (when (not= 1 input-count) "s") " will be generated")
      "Enter values above, one per line")]])

(defn- action-buttons
  "Generate preview and download buttons."
  [{:keys [has-input? can-download?]}]
  [:div {:class "flex flex-col sm:flex-row gap-3"}
   (button/button {:variant :outline
                   :disabled (not has-input?)
                   :on-click #(events/dispatch! [:qr-codes/generate-preview])
                   :class "flex-1"}
                  [:> RefreshCw {:class "size-4 mr-2"}]
                  "Generate Preview")
   (button/button {:variant :default
                   :disabled (not can-download?)
                   :on-click #(events/dispatch! [:qr-codes/download])
                   :class "flex-1"}
                  [:> Download {:class "size-4 mr-2"}]
                  "Download")])

;; =============================================================================
;; Main Page Component
;; =============================================================================

(defn qr-codes-page
  "QR Code Generator page.
   
   Features:
   - Paste multiple values (one per line)
   - Configure size and output format
   - Preview first 10 QR codes
   - Download as ZIP (PNG files) or PDF"
  [data]
  [:div {:class "container mx-auto px-4 py-8 max-w-4xl"}
   ;; Header
   [:div {:class "flex items-center gap-3 mb-8"}
    [:div {:class "p-3 rounded-lg bg-primary/10"}
     [:> QrCode {:class "size-8 text-primary"}]]
    [:div
     [:h1 {:class "text-3xl font-bold"}
      "QR Code Generator"]
     [:p {:class "text-muted-foreground"}
      "Generate multiple QR codes in bulk"]]]
   ;; Main content
   [:div {:class "space-y-6"}
    ;; Input section
    [:div {:class "p-6 border rounded-lg bg-card"}
     [input-section data]]
    ;; Options grid
    [:div {:class "p-6 border rounded-lg bg-card space-y-6"}
     [:div {:class "grid grid-cols-1 sm:grid-cols-2 gap-4"}
      [size-selector data]
      [format-selector data]]
     ;; Label toggle
     [label-toggle data]]
    ;; Errors
    [error-display (:errors data)]
    ;; Actions
    [:div {:class "p-6 border rounded-lg bg-card"}
     [action-buttons data]]
    ;; Preview
    [preview-section data]]])
