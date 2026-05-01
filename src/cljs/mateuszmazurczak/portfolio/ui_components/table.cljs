(ns mateuszmazurczak.portfolio.ui-components.table
  (:require
   [clojure.string                               :as str]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge         :as badge]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.dropdown-menu :as dropdown-menu]
   [mateuszmazurczak.ui.components.table         :as sut]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Table"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Table component primitives for building data tables."
            :npm-install "No external dependencies"
            :source-code (embed-source "mateuszmazurczak.ui.components.table")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/table.cljs"
            :filename "table.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "table"
                :description "Table wrapper that adds horizontal overflow container and forwards extra props to <table>."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to native <table>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-header"
                :description "Semantic <thead> section with row border styling."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <thead>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-body"
                :description "Semantic <tbody> container for data rows."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <tbody>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-footer"
                :description "Semantic <tfoot> section with muted background and stronger typography."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <tfoot>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-row"
                :description "Table row with hover and selected-state styling via data-state attribute."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name ":data-state"     :type "string"      :default nil :description "e.g. \"selected\" for selected row styling."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <tr>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-head"
                :description "Header cell (<th>) with default left alignment and checkbox spacing helpers."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <th>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-cell"
                :description "Body/footer cell (<td>) with whitespace and checkbox alignment helpers."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <td>."}]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "table-caption"
                :description "Caption text shown below table body (caption-bottom styling)."
                :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil :description "Forwarded to <caption>."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "table wraps <table> in an overflow-x container, so horizontal scroll is built in."]
                [:li "Use table-row {:data-state \"selected\"} to activate selected-row styling."]
                [:li "Checkbox alignment utilities are already included in table-head/table-cell styles."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[table {}\n [table-header {}\n  [table-row {}\n   [table-head {} \"Name\"]\n   [table-head {} \"Status\"]]]\n [table-body {}\n  [table-row {:data-state \"selected\"}\n   [table-cell {} \"Project Alpha\"]\n   [table-cell {} \"Active\"]]]]" ]]]]]]))

(defscene
 table-demo
 "Invoice table with header, body, and footer.

  Native elements: <table>, <thead>, <tbody>, <tfoot>

  Use for structured data with semantic table markup."
 []
 (let [invoices [{:id "INV001"
                  :status "Paid"
                  :method "Credit Card"
                  :amount "$250.00"}
                 {:id "INV002"
                  :status "Pending"
                  :method "PayPal"
                  :amount "$150.00"}
                 {:id "INV003"
                  :status "Unpaid"
                  :method "Bank Transfer"
                  :amount "$350.00"}
                 {:id "INV004"
                  :status "Paid"
                  :method "Credit Card"
                  :amount "$450.00"}
                 {:id "INV005"
                  :status "Paid"
                  :method "PayPal"
                  :amount "$550.00"}
                 {:id "INV006"
                  :status "Pending"
                  :method "Bank Transfer"
                  :amount "$200.00"}
                 {:id "INV007"
                  :status "Unpaid"
                  :method "Credit Card"
                  :amount "$300.00"}]]
   (mm-portfolio-utils/wrap-component
    [:div {:class "p-6"}
     [sut/table {}
      [sut/table-caption {}
       "A list of your recent invoices."]
      [sut/table-header {}
       [sut/table-row {}
        [sut/table-head {:class "w-[120px]"}
         "Invoice"]
        [sut/table-head {}
         "Status"]
        [sut/table-head {}
         "Method"]
        [sut/table-head {:class "text-right"}
         "Amount"]]]
      [sut/table-body {}
       (for [{:keys [id status method amount]} invoices]
         ^{:key id}
         [sut/table-row {}
          [sut/table-cell {:class "font-medium"}
           id]
          [sut/table-cell {}
           status]
          [sut/table-cell {}
           method]
          [sut/table-cell {:class "text-right"}
           amount]])]
      [sut/table-footer {}
       [sut/table-row {}
        [sut/table-cell {:col-span 3}
         "Total"]
        [sut/table-cell {:class "text-right"}
         "$2,500.00"]]]]])))

(defscene
 typography-table
 "Typographic table styling example.

  Native elements: <table>

  This example mirrors the typography docs table layout."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [:div {:class "my-6 w-full overflow-y-auto"}
    [:table {:class "w-full"}
     [:thead
      [:tr {:class "even:bg-muted m-0 border-t p-0"}
       [:th {:class "border px-4 py-2 text-left font-bold"}
        "King's Treasury"]
       [:th {:class "border px-4 py-2 text-left font-bold"}
        "People's happiness"]]]
     [:tbody
      [:tr {:class "even:bg-muted m-0 border-t p-0"}
       [:td {:class "border px-4 py-2 text-left"}
        "Empty"]
       [:td {:class "border px-4 py-2 text-left"}
        "Overflowing"]]
      [:tr {:class "even:bg-muted m-0 border-t p-0"}
       [:td {:class "border px-4 py-2 text-left"}
        "Modest"]
       [:td {:class "border px-4 py-2 text-left"}
        "Satisfied"]]
      [:tr {:class "even:bg-muted m-0 border-t p-0"}
       [:td {:class "border px-4 py-2 text-left"}
        "Full"]
       [:td {:class "border px-4 py-2 text-left"}
        "Ecstatic"]]]]]]))

(defscene
 table-with-actions
 "Table with status badges and row actions.

  Shows how to combine badge and dropdown menu inside table cells.

  Useful for admin dashboards and data management."
 []
 (let [rows [{:id "PRJ-104"
              :title "Marketing Site"
              :status :active}
             {:id "PRJ-105"
              :title "Mobile App"
              :status :paused}
             {:id "PRJ-106"
              :title "Data Pipeline"
              :status :blocked}]
       row->view
       (fn [{:keys [id title status]}] [sut/table-row {:key id}
                                        [sut/table-cell {}
                                         [:div {:class "font-medium"}
                                          title]
                                         [:div {:class "text-xs text-muted-foreground"}
                                          id]]
                                        [sut/table-cell {}
                                         [badge/badge {:variant (case status
                                                                  :active :default
                                                                  :paused :secondary
                                                                  :blocked :destructive
                                                                  :default)}
                                          (str/capitalize (name status))]]
                                        [sut/table-cell {:class "text-right"}
                                         [dropdown-menu/dropdown-menu {}
                                          [dropdown-menu/dropdown-menu-trigger {:as-child true}
                                           (button/button {:variant :ghost
                                                           :size :icon}
                                                          "⋯")]
                                          [dropdown-menu/dropdown-menu-content {:align "end"}
                                           [dropdown-menu/dropdown-menu-item {}
                                            "View"]
                                           [dropdown-menu/dropdown-menu-item {}
                                            "Edit"]
                                           [dropdown-menu/dropdown-menu-item {}
                                            "Archive"]]]]])]
   (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                       [sut/table {}
                                        [sut/table-header {}
                                         [sut/table-row {}
                                          [sut/table-head {}
                                           "Project"]
                                          [sut/table-head {}
                                           "Status"]
                                          [sut/table-head {:class "text-right"}
                                           "Actions"]]]
                                        (into [sut/table-body {}]
                                              (map row->view rows))]])))