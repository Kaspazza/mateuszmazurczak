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
            :source-code (embed-source mateuszmazurczak.ui.components.table)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/table.cljs"
            :filename "table.cljs"}])

(defscene
 api-reference
 "Complete reference for all Table component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Table components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "table"
                                             :description "Table component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-header"
                                             :description "Table header component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-body"
                                             :description "Table body component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-footer"
                                             :description "Table footer component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-row"
                                             :description "Table row component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-head"
                                             :description "Table head component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-cell"
                                             :description "Table cell component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "table-caption"
                                             :description "Table caption component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[table {}]"]]]]]]))

(defscene
 table-demo
 "Invoice table with header, body, and footer.

  Based on shadcn/ui Table — https://ui.shadcn.com/docs/components/table
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

  Based on shadcn/ui Typography Table — https://ui.shadcn.com/docs/components/table
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

  Custom example — not from shadcn/ui.
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