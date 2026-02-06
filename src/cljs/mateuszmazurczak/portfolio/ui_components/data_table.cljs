(ns mateuszmazurczak.portfolio.ui-components.data-table
  (:require
   ["lucide-react"                               :refer [ChevronDown MoreHorizontal]]
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge         :as badge]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.checkbox      :as checkbox]
   [mateuszmazurczak.ui.components.data-table    :as sut]
   [mateuszmazurczak.ui.components.dropdown-menu :as dropdown-menu]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]
   [reagent.core                                 :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Data Table"})

(defscene
 installation
 "Install dependencies and copy the component code into your project."
 []
 [mm-portfolio-utils/installation-scene
  {:description "Data table component with sorting, filtering, pagination, and selection."
   :npm-install
   "npm install @dnd-kit/core @dnd-kit/modifiers @dnd-kit/sortable @dnd-kit/utilities @tanstack/react-table lucide-react"
   :source-code (embed-source mateuszmazurczak.ui.components.data_table)
   :namespace-path "src/cljs/mateuszmazurczak/ui/components/data_table.cljs"
   :filename "data_table.cljs"}])

(defscene
 api-reference
 "Complete reference for all Data Table component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Data Table components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "drag-handle-cell-ui"
                                             :description "Drag handle cell ui component"
                                             :props [[":listeners" "map, required - DnD listeners map from @dnd-kit"]
                                                     [":attributes" "map, required - DnD attributes map from @dnd-kit"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "faceted-filter-ui"
                                             :description "Faceted filter ui component"
                                             :props
                                             [[":title" "string, required - Human-readable title"]
                                              [":options" "vector<map>, required - Options. Each option: {:label string :value string :icon component?}"]
                                              [":selected-values" "set<string>, required - Selected filter values"]
                                              [":on-change" "function, required - Callback (fn [new-selected-set])"]
                                              [":facet-counts" "map<string, number>, optional - Counts by option value"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "toolbar-ui"
       :description "Toolbar ui component"
       :props [[":text-filter-value" "string, optional - Current text filter value"]
               [":on-text-filter-change" "function, optional - Callback (fn [value]) for text filter changes"]
               [":text-placeholder" "string, optional (default 'Filter items...') - Text filter placeholder"]
               [":faceted-filters" "vector<map>, optional - Faceted filter configs"]
               [":is-filtered?" "boolean, optional (default false) - Whether any filters are active"]
               [":on-reset-filters" "function, optional - Callback (fn []) to clear filters"]
               [":toolbar-end" "hiccup | component, optional - Right-side toolbar content"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "column-header-ui"
       :description "Column header ui component"
       :props [[":title" "string, required - Human-readable title"]
               [":can-sort?" "boolean, optional (default false) - Whether column is sortable"]
               [":sort-state" "string | false | nil, optional - One of: 'asc' | 'desc' | false | nil"]
               [":on-toggle-sort" "function, optional - Callback (fn [descending?])"]
               [":on-clear-sort" "function, optional - Callback (fn [])"]
               [":on-toggle-visibility" "function, optional - Callback (fn [])"]
               [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "pagination-ui"
       :description "Pagination ui component"
       :props [[":page-size" "number, required - Current page size"]
               [":page-index" "number, required - Current page index (0-based)"]
               [":page-count" "number, required - Total page count"]
               [":selected-count" "number, optional (default 0) - Number of selected rows"]
               [":total-count" "number, required - Total row count"]
               [":can-previous?" "boolean, required - Whether previous page is available"]
               [":can-next?" "boolean, required - Whether next page is available"]
               [":on-page-size-change" "function, required - Callback (fn [size])"]
               [":on-first-page" "function, optional - Callback (fn [])"]
               [":on-previous-page" "function, optional - Callback (fn [])"]
               [":on-next-page" "function, optional - Callback (fn [])"]
               [":on-last-page" "function, optional - Callback (fn [])"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[drag-handle-cell-ui {}]"]]]]]]))

(defn make-task-data
  []
  [{:id "TASK-001"
    :title "Fix navigation bug"
    :status "in-progress"
    :priority "high"
    :assignee "John Doe"}
   {:id "TASK-002"
    :title "Update documentation"
    :status "todo"
    :priority "medium"
    :assignee "Jane Smith"}
   {:id "TASK-003"
    :title "Refactor API calls"
    :status "in-progress"
    :priority "high"
    :assignee "John Doe"}
   {:id "TASK-004"
    :title "Design new landing page"
    :status "done"
    :priority "low"
    :assignee "Bob Johnson"}
   {:id "TASK-005"
    :title "Add unit tests"
    :status "todo"
    :priority "medium"
    :assignee "Jane Smith"}])

;; Column helpers
(defn make-checkbox-column
  []
  #js {:id "select"
       :header (fn [^js ctx]
                 (let [table (.-table ctx)]
                   [checkbox/checkbox {:checked (or (and (.getIsAllPageRowsSelected table) "all")
                                                    (and (.getIsSomePageRowsSelected table)
                                                         "indeterminate")
                                                    false)
                                       :on-checked-change #(.toggleAllPageRowsSelected table %)
                                       :aria-label "Select all"}]))
       :cell (fn [^js ctx]
               (let [row (.-row ctx)]
                 [checkbox/checkbox {:checked (.getIsSelected row)
                                     :on-checked-change #(.toggleSelected row %)
                                     :aria-label "Select row"}]))
       :enableSorting false
       :enableHiding false})

(defn make-columns
  []
  #js
   [(make-checkbox-column)
    #js {:accessorKey "id"
         :header (fn [^js ctx] [sut/column-header-ui
                                {:title "ID"
                                 :can-sort? true
                                 :sort-state (.getIsSorted (.-column ctx))
                                 :on-toggle-sort #(.toggleSorting (.-column ctx) %)
                                 :on-clear-sort #(.clearSorting (.-column ctx))
                                 :on-toggle-visibility #(.toggleVisibility (.-column ctx) false)}])
         :cell (fn [^js ctx] [:div {:class "font-mono text-xs"}
                              (.getValue ctx)])}
    #js {:accessorKey "title"
         :header (fn [^js ctx] [sut/column-header-ui
                                {:title "Title"
                                 :can-sort? true
                                 :sort-state (.getIsSorted (.-column ctx))
                                 :on-toggle-sort #(.toggleSorting (.-column ctx) %)
                                 :on-clear-sort #(.clearSorting (.-column ctx))
                                 :on-toggle-visibility #(.toggleVisibility (.-column ctx) false)}])
         :cell (fn [^js ctx] (.getValue ctx))}
    #js {:accessorKey "status"
         :header (fn [^js ctx] [sut/column-header-ui
                                {:title "Status"
                                 :can-sort? true
                                 :sort-state (.getIsSorted (.-column ctx))
                                 :on-toggle-sort #(.toggleSorting (.-column ctx) %)
                                 :on-clear-sort #(.clearSorting (.-column ctx))}])
         :cell (fn [^js ctx]
                 (let [status (.getValue ctx)]
                   [badge/badge {:variant (case status
                                            "done" :default
                                            "in-progress" :secondary
                                            "todo" :outline
                                            :default)}
                    status]))
         :filterFn (fn [row _column-id filter-value]
                     (some #(= % (aget (.-original row) "status")) (js->clj filter-value)))}
    #js {:accessorKey "priority"
         :header (fn [^js ctx] [sut/column-header-ui
                                {:title "Priority"
                                 :can-sort? true
                                 :sort-state (.getIsSorted (.-column ctx))
                                 :on-toggle-sort #(.toggleSorting (.-column ctx) %)
                                 :on-clear-sort #(.clearSorting (.-column ctx))}])
         :cell (fn [^js ctx]
                 (let [priority (.getValue ctx)]
                   [:span {:class (case priority
                                    "high" "text-red-600 font-medium"
                                    "medium" "text-yellow-600"
                                    "low" "text-green-600"
                                    "")}
                    priority]))
         :filterFn (fn [row _column-id filter-value]
                     (some #(= % (aget (.-original row) "priority")) (js->clj filter-value)))}
    #js {:accessorKey "assignee"
         :header "Assignee"
         :cell (fn [^js ctx] (.getValue ctx))}
    #js {:id "actions"
         :cell (fn [^js _ctx] [dropdown-menu/dropdown-menu {}
                               [dropdown-menu/dropdown-menu-trigger {:as-child true}
                                (button/button {:variant :ghost
                                                :size :icon
                                                :class "h-8 w-8"}
                                               [:> MoreHorizontal {:class "h-4 w-4"}])]
                               [dropdown-menu/dropdown-menu-content {:align "end"}
                                [dropdown-menu/dropdown-menu-label {}
                                 "Actions"]
                                [dropdown-menu/dropdown-menu-item {}
                                 "Edit"]
                                [dropdown-menu/dropdown-menu-item {}
                                 "Delete"]]])
         :enableSorting false
         :enableHiding false}])

(defscene
 basic-data-table
 "Basic data table with sorting and pagination.
   
   Click column headers to sort, use pagination controls at the bottom."
 []
 (let [data (r/atom (clj->js (make-task-data)))
       columns (r/atom (make-columns))]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-8"}
                                         [:h3 {:class "text-lg font-semibold mb-4"}
                                          "Tasks"]
                                         [sut/data-table {:columns @columns
                                                          :data @data
                                                          :initial-page-size 5}]]))))

(defscene
 table-with-toolbar
 "Data table with search and filters.
   
   Use the search box to filter by title, or use the faceted filters
   for status and priority."
 []
 (let [data (r/atom (clj->js (make-task-data)))
       columns (r/atom (make-columns))
       toolbar-config {:text-filter {:column-id "title"
                                     :placeholder "Search tasks..."}
                       :faceted-filters [{:column-id "status"
                                          :title "Status"
                                          :options [{:label "Todo"
                                                     :value "todo"}
                                                    {:label "In Progress"
                                                     :value "in-progress"}
                                                    {:label "Done"
                                                     :value "done"}]}
                                         {:column-id "priority"
                                          :title "Priority"
                                          :options [{:label "High"
                                                     :value "high"}
                                                    {:label "Medium"
                                                     :value "medium"}
                                                    {:label "Low"
                                                     :value "low"}]}]}]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-8"}
                                         [:h3 {:class "text-lg font-semibold mb-4"}
                                          "Tasks with Filters"]
                                         [sut/data-table {:columns @columns
                                                          :data @data
                                                          :toolbar-config toolbar-config
                                                          :initial-page-size 5}]]))))

(defscene
 table-with-expandable-rows
 "Data table with expandable rows.
   
   NEW FEATURE: Click the chevron to expand rows and see additional details."
 []
 (let [data (r/atom (clj->js (make-task-data)))
       expand-column #js {:id "expand"
                          :header ""
                          :cell (fn [^js ctx]
                                  (let [row (.-row ctx)]
                                    (when (.getCanExpand row)
                                      (button/button {:variant :ghost
                                                      :size :icon
                                                      :class "h-8 w-8"
                                                      :on-click #(.toggleExpanded row)}
                                                     [:>
                                                      ChevronDown
                                                      {:class (str "h-4 w-4 transition-transform "
                                                                   (when (.getIsExpanded row)
                                                                     "rotate-180"))}]))))
                          :enableSorting false
                          :enableHiding false}
       columns (r/atom (into-array (cons expand-column (make-columns))))
       render-sub-component (fn [row]
                              (let [task (js->clj (.-original row) :keywordize-keys true)]
                                [:div {:class "p-4 bg-muted"}
                                 [:h4 {:class "font-semibold mb-2"}
                                  "Task Details"]
                                 [:div {:class "space-y-2 text-sm"}
                                  [:p [:strong "ID: "] (:id task)]
                                  [:p
                                   [:strong "Description: "]
                                   "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
                                  [:p [:strong "Created: "] "2024-01-15"]
                                  [:p [:strong "Due date: "] "2024-02-01"]]]))]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8"}
       [:h3 {:class "text-lg font-semibold mb-4"}
        "Expandable Rows"]
       [:p {:class "text-sm text-muted-foreground mb-4"}
        "Click the chevron icon to expand a row and see more details"]
       [sut/data-table {:columns @columns
                        :data @data
                        :render-sub-component render-sub-component
                        :initial-page-size 5}]]))))

(defscene
 table-with-drag-and-drop
 "Data table with drag-and-drop reordering.
   
   NEW FEATURE: Drag rows by the handle on the left to reorder them."
 []
 (let [data (r/atom (make-task-data))
       drag-handle-column (fn []
                            #js {:id "drag-handle"
                                 :header ""
                                 :cell (fn [^js ctx]
                                         (let [row (.-row ctx)
                                               listeners (aget row "dndListeners")
                                               attributes (aget row "dndAttributes")]
                                           (when (and listeners attributes)
                                             [sut/drag-handle-cell-ui {:listeners listeners
                                                                       :attributes attributes}])))
                                 :size 40
                                 :enableSorting false
                                 :enableHiding false})
       columns (r/atom (into-array (cons (drag-handle-column) (make-columns))))
       move-row
       (fn [active-id over-id]
         (swap! data
           (fn [tasks]
             (let [old-index (.findIndex (clj->js tasks) (fn [task] (= (.-id task) active-id)))
                   new-index (.findIndex (clj->js tasks) (fn [task] (= (.-id task) over-id)))]
               (when (and (>= old-index 0) (>= new-index 0))
                 (let [tasks-vec (vec tasks)
                       item (nth tasks-vec old-index)
                       without (vec (concat (subvec tasks-vec 0 old-index)
                                            (subvec tasks-vec (inc old-index))))]
                   (vec
                    (concat (subvec without 0 new-index) [item] (subvec without new-index)))))))))]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8"}
       [:h3 {:class "text-lg font-semibold mb-4"}
        "Drag-and-Drop Reordering"]
       [:p {:class "text-sm text-muted-foreground mb-4"}
        "Grab the handle (⋮⋮) on the left and drag to reorder rows"]
       [sut/data-table {:columns @columns
                        :data (clj->js @data)
                        :dnd-config {:get-row-id (fn [row] (.-id (.-original row)))
                                     :on-drag-end move-row}
                        :initial-page-size 10}]]))))

(defscene
 table-with-dnd-and-expandable
 "Data table with BOTH drag-and-drop AND expandable rows.
   
   Demonstrates using both new features together."
 []
 (let [data (r/atom (make-task-data))
       drag-handle-column (fn []
                            #js {:id "drag-handle"
                                 :header ""
                                 :cell (fn [^js ctx]
                                         (let [row (.-row ctx)
                                               listeners (aget row "dndListeners")
                                               attributes (aget row "dndAttributes")]
                                           (when (and listeners attributes)
                                             [sut/drag-handle-cell-ui {:listeners listeners
                                                                       :attributes attributes}])))
                                 :size 40
                                 :enableSorting false
                                 :enableHiding false})
       expand-column #js {:id "expand"
                          :header ""
                          :cell (fn [^js ctx]
                                  (let [row (.-row ctx)]
                                    (when (.getCanExpand row)
                                      (button/button {:variant :ghost
                                                      :size :icon
                                                      :class "h-8 w-8"
                                                      :on-click #(.toggleExpanded row)}
                                                     [:>
                                                      ChevronDown
                                                      {:class (str "h-4 w-4 transition-transform "
                                                                   (when (.getIsExpanded row)
                                                                     "rotate-180"))}]))))
                          :enableSorting false
                          :enableHiding false}
       columns (r/atom (into-array (concat [(drag-handle-column) expand-column] (make-columns))))
       render-sub-component
       (fn [row]
         (let [task (js->clj (.-original row) :keywordize-keys true)]
           [:div {:class "p-4 bg-muted"}
            [:h4 {:class "font-semibold mb-2"}
             "Task Details"]
            [:div {:class "space-y-2 text-sm"}
             [:p
              [:strong "Full Description: "]
              "This is a detailed description of the task. "
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
             [:p [:strong "Notes: "] "Additional notes and context about this task."]]]))
       move-row
       (fn [active-id over-id]
         (swap! data
           (fn [tasks]
             (let [old-index (.findIndex (clj->js tasks) (fn [task] (= (.-id task) active-id)))
                   new-index (.findIndex (clj->js tasks) (fn [task] (= (.-id task) over-id)))]
               (when (and (>= old-index 0) (>= new-index 0))
                 (let [tasks-vec (vec tasks)
                       item (nth tasks-vec old-index)
                       without (vec (concat (subvec tasks-vec 0 old-index)
                                            (subvec tasks-vec (inc old-index))))]
                   (vec
                    (concat (subvec without 0 new-index) [item] (subvec without new-index)))))))))]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8"}
       [:h3 {:class "text-lg font-semibold mb-4"}
        "Full-Featured Table"]
       [:p {:class "text-sm text-muted-foreground mb-4"}
        "Drag to reorder AND expand for details"]
       [sut/data-table {:columns @columns
                        :data (clj->js @data)
                        :render-sub-component render-sub-component
                        :dnd-config {:get-row-id (fn [row] (.-id (.-original row)))
                                     :on-drag-end move-row}
                        :initial-page-size 10}]]))))

(defscene table-empty-state
          "Data table with custom empty state."
          []
          (let [columns (r/atom (make-columns))
                empty-state [:div {:class "py-12 text-center"}
                             [:p {:class "text-muted-foreground"}
                              "No tasks found. Create your first task to get started."]]]
            (fn []
              (mm-portfolio-utils/wrap-component [:div {:class "p-8"}
                                                  [:h3 {:class "text-lg font-semibold mb-4"}
                                                   "Empty State"]
                                                  [sut/data-table {:columns @columns
                                                                   :data #js []
                                                                   :empty-state empty-state}]]))))
