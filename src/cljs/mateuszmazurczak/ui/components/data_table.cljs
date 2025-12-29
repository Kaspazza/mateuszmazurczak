(ns mateuszmazurczak.ui.components.data-table
  "Data table component with sorting, filtering, pagination, and selection.
  
  A comprehensive data table built on TanStack Table with full feature support:
  sorting, filtering, faceting, pagination, row selection.
  
  All TanStack Table interactions happen in this component"
  (:require
   ["@tanstack/react-table"                      :refer [flexRender
                                                         getCoreRowModel
                                                         getFacetedRowModel
                                                         getFacetedUniqueValues
                                                         getFilteredRowModel
                                                         getPaginationRowModel
                                                         getSortedRowModel
                                                         useReactTable]]
   ["lucide-react"                               :refer [ArrowDown
                                                         ArrowUp
                                                         Check
                                                         ChevronLeft
                                                         ChevronRight
                                                         ChevronsLeft
                                                         ChevronsRight
                                                         ChevronsUpDown
                                                         EyeOff
                                                         PlusCircle
                                                         X]]
   [mateuszmazurczak.ui.components.badge         :as mateuszmazurczak-badge]
   [mateuszmazurczak.ui.components.button        :as mateuszmazurczak-button]
   [mateuszmazurczak.ui.components.command       :as mateuszmazurczak-command]
   [mateuszmazurczak.ui.components.dropdown-menu :as mateuszmazurczak-dropdown-menu]
   [mateuszmazurczak.ui.components.input         :as mateuszmazurczak-input]
   [mateuszmazurczak.ui.components.popover       :as mateuszmazurczak-popover]
   [mateuszmazurczak.ui.components.select        :as mateuszmazurczak-select]
   [mateuszmazurczak.ui.components.separator     :as mateuszmazurczak-separator]
   [mateuszmazurczak.ui.components.table         :as mateuszmazurczak-table]
   [mateuszmazurczak.utils.styles                :refer [merge-classes]]
   [reagent.core                                 :as    r
                                                 :refer [defc]]
   [reagent.hooks                                :as rhooks]))



(defn- extract-toolbar-data
  "Extracts toolbar state and callbacks from table instance and config.
  
  Transforms toolbar-config into a reactive data structure with callbacks
  for the toolbar-ui component. Handles both text filter and faceted filters.
  
  Args:
  - toolbar-config: Map with :text-filter, :faceted-filters, :toolbar-end
  - table-instance: TanStack Table instance
  
  Returns:
  Map with extracted state for toolbar-ui component."
  [toolbar-config table-instance]
  (let [text-filter-config (:text-filter toolbar-config)
        text-column-id (:column-id text-filter-config)
        text-column (when text-column-id (.getColumn table-instance text-column-id))
        text-filter-value (when text-column (or (.getFilterValue text-column) ""))
        text-placeholder (:placeholder text-filter-config "Filter items...")
        table-state (.getState table-instance)
        column-filters-state (.-columnFilters table-state)
        is-filtered? (pos? (.-length column-filters-state))
        faceted-filters-with-state
        (vec
         (for [filter-config (:faceted-filters toolbar-config)]
           (let [column-id (:column-id filter-config)
                 column (.getColumn table-instance column-id)
                 filter-value (when column (or (.getFilterValue column) #js []))
                 selected-values (set (js->clj filter-value))
                 facets (when column (.getFacetedUniqueValues column))
                 facet-counts (when facets
                                (into {}
                                      (map (fn [^js entry] [(aget entry 0) (aget entry 1)])
                                           (js/Array.from facets))))]
             {:title (:title filter-config)
              :options (:options filter-config)
              :selected-values selected-values
              :facet-counts facet-counts
              :on-change (fn [new-selected-set]
                           (when column
                             (let [filter-values (vec new-selected-set)]
                               (.setFilterValue column
                                                (if (seq filter-values)
                                                  (clj->js filter-values)
                                                  js/undefined)))))})))]
    {:text-filter-value text-filter-value
     :on-text-filter-change (fn [value] (when text-column (.setFilterValue text-column value)))
     :text-placeholder text-placeholder
     :faceted-filters faceted-filters-with-state
     :is-filtered? is-filtered?
     :on-reset-filters #(.resetColumnFilters table-instance)
     :toolbar-end (when-let [end-content (:toolbar-end toolbar-config)]
                    (if (fn? end-content) (end-content table-instance) end-content))}))

(defn- extract-pagination-data
  "Extracts pure data and callbacks from table instance for pagination.
  This ensures the pagination component receives reactive data."
  [table-instance]
  (let [pagination-state (.. table-instance getState -pagination)
        page-size (.-pageSize pagination-state)
        page-index (.-pageIndex pagination-state)
        selected-rows (.. table-instance getFilteredSelectedRowModel -rows)
        total-rows (.. table-instance getFilteredRowModel -rows)
        can-previous? (.getCanPreviousPage table-instance)
        can-next? (.getCanNextPage table-instance)
        page-count (.getPageCount table-instance)]
    {:page-size page-size
     :page-index page-index
     :page-count page-count
     :selected-count (.-length selected-rows)
     :total-count (.-length total-rows)
     :can-previous? can-previous?
     :can-next? can-next?
     :on-page-size-change #(.setPageSize table-instance %)
     :on-first-page #(.setPageIndex table-instance 0)
     :on-previous-page #(.previousPage table-instance)
     :on-next-page #(.nextPage table-instance)
     :on-last-page #(.setPageIndex table-instance (dec page-count))}))

(defn faceted-filter-ui
  "Data table faceted filter with multi-select support.
  
  Pure UI component - receives all state and callbacks from parent.
  Displays a popover with a searchable command menu for selecting multiple filter values.
  Shows selected values as badges in the trigger button.
  Displays facet counts (number of rows with each value) when available.
  
  Props:
  - `:title`            - Filter title to display in button and search placeholder (required)
  - `:options`          - Vector of filter option maps (required)
    - Each option map:
      - `:label`        - Display label (string, required)
      - `:value`        - Filter value (string, required)
      - `:icon`         - Optional React component for icon (e.g., CheckIcon)
  - `:selected-values`  - Set of currently selected filter values (required)
  - `:on-change`        - Callback (fn [new-selected-set]) called when selection changes (required)
  - `:facet-counts`     - Map of value -> count for displaying facet counts (optional)
  
  Features:
  - Multi-select with checkbox-style indicators
  - Search/filter options via command input
  - Display selected count as badge
  - Show individual badges for selected values (up to 2, then shows count)
  - Show facet counts (how many rows match each value)
  - Clear all filters button
  - Responsive badges (hidden on mobile, visible on desktop)"
  [{:keys [title options selected-values on-change facet-counts]
    :or {facet-counts {}}}]
  (let [selected-count (count selected-values)]
    [mateuszmazurczak-popover/popover {}
     [mateuszmazurczak-popover/popover-trigger {:as-child true}
      (mateuszmazurczak-button/button
       {:variant :outline
        :size :sm
        :class "h-8 border-dashed"}
       [:> PlusCircle]
       title
       (when (pos? selected-count)
         [:<>
          [mateuszmazurczak-separator/separator {:orientation :vertical
                                                 :class "mx-2 h-4"}]
          [mateuszmazurczak-badge/badge {:variant :secondary
                                         :class "rounded-sm px-1 font-normal lg:hidden"}
           selected-count]
          [:div {:class "hidden gap-1 lg:flex"}
           (if (> selected-count 2)
             [mateuszmazurczak-badge/badge {:variant :secondary
                                            :class "rounded-sm px-1 font-normal"}
              (str selected-count " selected")]
             (for [option (filter #(contains? selected-values (:value %)) options)]
               ^{:key (:value option)}
               [mateuszmazurczak-badge/badge {:variant :secondary
                                              :class "rounded-sm px-1 font-normal"}
                (:label option)]))]]))]
     [mateuszmazurczak-popover/popover-content {:class "w-[200px] p-0"
                                                :align "start"}
      [mateuszmazurczak-command/command {}
       [mateuszmazurczak-command/command-input {:placeholder title}]
       [mateuszmazurczak-command/command-list {}
        [mateuszmazurczak-command/command-empty {}
         "No results found."]
        [mateuszmazurczak-command/command-group {}
         (for [option options]
           (let [is-selected (contains? selected-values (:value option))
                 option-icon (:icon option)]
             ^{:key (:value option)}
             [mateuszmazurczak-command/command-item
              {:onSelect (fn []
                           (let [new-selected (if is-selected
                                                (disj selected-values (:value option))
                                                (conj selected-values (:value option)))]
                             (when on-change (on-change new-selected))))}
              [:div {:class (merge-classes
                             "flex size-4 items-center justify-center rounded-[4px] border"
                             (if is-selected
                               "bg-primary border-primary text-primary-foreground"
                               "border-input [&_svg]:invisible"))}
               [:> Check {:class "text-primary-foreground size-3.5"}]]
              (when option-icon [:> option-icon {:class "text-muted-foreground size-4"}])
              [:span (:label option)]
              (when-let [facet-count (get facet-counts (:value option))]
                [:span
                 {:class
                  "text-muted-foreground ml-auto flex size-4 items-center justify-center font-mono text-xs"}
                 facet-count])]))]
        (when (pos? selected-count)
          [:<>
           [mateuszmazurczak-command/command-separator {}]
           [mateuszmazurczak-command/command-group {}
            [mateuszmazurczak-command/command-item {:onSelect #(when on-change (on-change #{}))
                                                    :class "justify-center text-center"}
             "Clear filters"]]])]]]]))

(defn toolbar-ui
  "Renders a toolbar for data tables with text search and faceted filters.
  
  Pure UI component - receives all state and callbacks from parent.
  
  Props:
  - `:text-filter-value`      - Current value for text search filter (string, optional)
  - `:on-text-filter-change`  - Callback (fn [value]) when search input changes (optional)
  - `:text-placeholder`       - Placeholder for search input (default: 'Filter items...')
  - `:faceted-filters`        - Vector of faceted filter configs (optional)
  - `:is-filtered?`           - Whether any filters are active (boolean, default: false)
  - `:on-reset-filters`       - Callback (fn []) called when reset button is clicked (optional)
  - `:toolbar-end`            - Component to render on the right side of toolbar (optional)
  
  Faceted filter config shape:
  ```clojure
  {:title 'Status'                           ; Filter button label
   :options [{:label 'Backlog' :value 'backlog'}   ; Available options
             {:label 'Done' :value 'done'}]
   :selected-values #{'backlog'}             ; Currently selected values (set)
   :on-change (fn [new-selected-set] ...)    ; Callback when selection changes
   :facet-counts {'backlog' 5 'done' 3}}     ; Optional counts for each value
  ```
  "
  [{:keys [text-filter-value
           on-text-filter-change
           text-placeholder
           faceted-filters
           is-filtered?
           on-reset-filters
           toolbar-end]
    :or {text-placeholder "Filter items..."
         is-filtered? false}}]
  [:div {:class "flex items-center justify-between"}
   [:div {:class "flex flex-1 items-center gap-2"}
    (when on-text-filter-change
      [mateuszmazurczak-input/input {:placeholder text-placeholder
                                     :value (or text-filter-value "")
                                     :on-change #(let [value (-> %
                                                                 .-target
                                                                 .-value)]
                                                   (on-text-filter-change value))
                                     :class "h-8 w-[150px] lg:w-[250px]"}])
    (for [[idx filter-config] (map-indexed vector faceted-filters)]
      ^{:key idx} [faceted-filter-ui filter-config])
    (when is-filtered?
      [mateuszmazurczak-button/button {:variant :ghost
                                       :size :sm
                                       :on-click #(when on-reset-filters (on-reset-filters))
                                       :class "h-8 px-2 lg:px-3"}
       "Reset"
       [:> X {:class "ml-2 h-4 w-4"}]])]
   (when toolbar-end toolbar-end)])

(defn column-header-ui
  "Data table column header with sorting and visibility controls.
  
  Pure UI component - receives all state and callbacks from parent.
  
  Behavior:
  - If `:on-toggle-visibility` is provided: Shows dropdown menu with sort options and hide
  - Otherwise: Clicking cycles through sort states (unsorted → asc → desc → unsorted)
  - If column cannot be sorted: Renders simple div with title
  
  Props:
  - `:title`                 - Column title to display (required)
  - `:can-sort?`             - Whether column supports sorting (default: false)
  - `:sort-state`            - Current sort state: 'asc', 'desc', or false/nil (default: false)
  - `:on-toggle-sort`        - Callback (fn [descending?]) called when sort is toggled (optional)
  - `:on-clear-sort`         - Callback (fn []) called to clear sorting (optional)
  - `:on-toggle-visibility`  - Callback (fn []) called when visibility is toggled (optional)
  - `:class`                 - Additional Tailwind classes to merge with defaults (optional)"
  [{:keys [title can-sort? sort-state on-toggle-sort on-clear-sort on-toggle-visibility class]
    :or {can-sort? false
         sort-state false}}]
  (if-not can-sort?
    [:div {:class (merge-classes class)}
     title]
    [:div {:class (merge-classes "flex items-center gap-2" class)}
     (if on-toggle-visibility
       [mateuszmazurczak-dropdown-menu/dropdown-menu {}
        [mateuszmazurczak-dropdown-menu/dropdown-menu-trigger {:as-child true}
         (mateuszmazurczak-button/button {:variant :ghost
                                          :size :sm
                                          :class "data-[state=open]:bg-accent -ml-3 h-8"}
                                         [:span title]
                                         (case sort-state
                                           "desc" [:> ArrowDown]
                                           "asc" [:> ArrowUp]
                                           [:> ChevronsUpDown]))]
        [mateuszmazurczak-dropdown-menu/dropdown-menu-content {:align :start}
         [mateuszmazurczak-dropdown-menu/dropdown-menu-item {:on-select #(when on-toggle-sort
                                                                           (on-toggle-sort false))}
          [:> ArrowUp]
          "Asc"]
         [mateuszmazurczak-dropdown-menu/dropdown-menu-item {:on-select #(when on-toggle-sort
                                                                           (on-toggle-sort true))}
          [:> ArrowDown]
          "Desc"]
         (when (and on-clear-sort sort-state)
           [:<>
            [mateuszmazurczak-dropdown-menu/dropdown-menu-separator {}]
            [mateuszmazurczak-dropdown-menu/dropdown-menu-item {:on-select #(on-clear-sort)}
             [:> ChevronsUpDown]
             "Clear sort"]])
         [mateuszmazurczak-dropdown-menu/dropdown-menu-separator {}]
         [mateuszmazurczak-dropdown-menu/dropdown-menu-item {:on-select #(on-toggle-visibility)}
          [:> EyeOff]
          "Hide"]]]
       [mateuszmazurczak-button/button {:variant :ghost
                                        :size :sm
                                        :class "-ml-3 h-8"
                                        :on-click
                                        (fn []
                                          (case sort-state
                                            false (when on-toggle-sort (on-toggle-sort false))
                                            "asc" (when on-toggle-sort (on-toggle-sort true))
                                            "desc" (when on-clear-sort (on-clear-sort))))}
        [:span title]
        (case sort-state
          "desc" [:> ArrowDown]
          "asc" [:> ArrowUp]
          [:> ChevronsUpDown])])]))

(defn- table-ui
  "Renders the table structure with headers and rows.
  
  Pure UI component - all data is pre-extracted from table instance.
  
  Props:
  - `:header-groups`     - Header groups from table instance (array)
  - `:rows`              - Row model rows from table instance (array)
  - `:columns-count`     - Total number of columns (for empty state colspan)
  - `:row-selection`     - Row selection state
  - `:empty-state`       - Component to render when table is empty (optional)
  - `:no-results-state`  - Component or function to render when filters yield no results (optional)
  - `:on-reset-filters`  - Callback to reset filters (passed to no-results-state if it's a function)"
  [{:keys [header-groups rows empty-state no-results-state on-reset-filters]
    :as _props}]
  (let [has-rows? (pos? (.-length rows))]
    (if has-rows?
      ;; Table with data
      [:div {:class "flex min-h-0 flex-1 flex-col overmateuszmazurczak-auto rounded-md border"}
       [mateuszmazurczak-table/table {}
        [mateuszmazurczak-table/table-header
         {:class
          "sticky top-0 z-10 bg-background after:absolute after:bottom-0 after:left-0 after:right-0 after:h-px after:bg-border"}
         (for [header-group header-groups]
           ^{:key (.-id header-group)}
           [mateuszmazurczak-table/table-row {}
            (for [header (.-headers header-group)]
              ^{:key (.-id header)}
              [mateuszmazurczak-table/table-head {:col-span (.-colSpan header)}
               (when-not (.-isPlaceholder header)
                 (flexRender (.. header -column -columnDef -header) (.getContext header)))])])]
        [mateuszmazurczak-table/table-body {}
         (for [row rows]
           [mateuszmazurczak-table/table-row {:key (.-id row)
                                              :data-state (when (.getIsSelected row) "selected")}
            (for [cell (.getVisibleCells row)]
              [mateuszmazurczak-table/table-cell {:key (.-id cell)}
               (flexRender (.. cell -column -columnDef -cell) (.getContext cell))])])]]]
      ;; Empty state - render outside table structure
      [:div {:class "flex min-h-0 flex-1 flex-col rounded-md border"}
       [mateuszmazurczak-table/table {}
        [mateuszmazurczak-table/table-header
         {:class
          "sticky top-0 z-10 bg-background after:absolute after:bottom-0 after:left-0 after:right-0 after:h-px after:bg-border"}
         (for [header-group header-groups]
           ^{:key (.-id header-group)}
           [mateuszmazurczak-table/table-row {}
            (for [header (.-headers header-group)]
              ^{:key (.-id header)}
              [mateuszmazurczak-table/table-head {:col-span (.-colSpan header)}
               (when-not (.-isPlaceholder header)
                 (flexRender (.. header -column -columnDef -header) (.getContext header)))])])]]
       [:div {:class "flex flex-1 items-center justify-center"}
        (cond
          ;; If no-results-state is provided and is a function, call it with reset callback
          (and no-results-state (fn? no-results-state)) (no-results-state on-reset-filters)
          ;; If no-results-state is provided and is a component, render it
          no-results-state no-results-state
          ;; Otherwise, show empty-state or default text
          :else (or empty-state "No results."))]])))



(defn pagination-ui
  "Pagination controls for data table.
  
  Pure UI component - receives all state and callbacks from parent.
  
  Displays:
  - Selected rows count (left side)
  - Rows per page selector (10, 20, 25, 30, 40, 50)
  - Current page number and total pages
  - Navigation buttons (first, previous, next, last)
  
  Props:
  - `:page-size`           - Current page size (number, required)
  - `:page-index`          - Current page index (0-based, number, required)
  - `:page-count`          - Total number of pages (number, required)
  - `:selected-count`      - Number of selected rows (number, default: 0)
  - `:total-count`         - Total number of rows (number, required)
  - `:can-previous?`       - Whether previous page navigation is enabled (boolean, required)
  - `:can-next?`           - Whether next page navigation is enabled (boolean, required)
  - `:on-page-size-change` - Callback (fn [size]) when page size changes (required)
  - `:on-first-page`       - Callback (fn []) to go to first page (optional)
  - `:on-previous-page`    - Callback (fn []) to go to previous page (optional)
  - `:on-next-page`        - Callback (fn []) to go to next page (optional)
  - `:on-last-page`        - Callback (fn []) to go to last page (optional)
  
  The component automatically disables navigation buttons based on `:can-previous?` and `:can-next?`.
  First/last page buttons are hidden on mobile (< lg breakpoint) to save space."
  [{:keys [page-size
           page-index
           page-count
           selected-count
           total-count
           can-previous?
           can-next?
           on-page-size-change
           on-first-page
           on-previous-page
           on-next-page
           on-last-page]
    :or {selected-count 0}}]
  [:div {:class "flex items-center justify-between px-2"}
   [:div {:class "text-muted-foreground flex-1 text-sm"}
    selected-count
    " of "
    total-count
    " row(s) selected."]
   [:div {:class "flex items-center space-x-6 lg:space-x-8"}
    [:div {:class "flex items-center space-x-2"}
     [:p {:class "text-sm font-medium"}
      "Rows per page"]
     [mateuszmazurczak-select/select {:value (str page-size)
                                      :onValueChange (fn [value]
                                                       (when on-page-size-change
                                                         (on-page-size-change (js/Number value))))}
      [mateuszmazurczak-select/select-trigger {:class "h-8 w-[70px]"}
       [mateuszmazurczak-select/select-value {:placeholder page-size}]]
      [mateuszmazurczak-select/select-content {:side "top"}
       (for [size [10 20 25 30 40 50]]
         ^{:key size}
         [mateuszmazurczak-select/select-item {:value (str size)}
          size])]]]
    [:div {:class "flex w-[100px] items-center justify-center text-sm font-medium"}
     "Page " (inc page-index)
     " of " page-count]
    [:div {:class "flex items-center space-x-2"}
     [mateuszmazurczak-button/button {:variant :outline
                                      :size :icon
                                      :class "hidden size-8 lg:flex"
                                      :on-click #(when on-first-page (on-first-page))
                                      :disabled (not can-previous?)}
      [:span {:class "sr-only"}
       "Go to first page"]
      [:> ChevronsLeft]]
     [mateuszmazurczak-button/button {:variant :outline
                                      :size :icon
                                      :class "size-8"
                                      :on-click #(when on-previous-page (on-previous-page))
                                      :disabled (not can-previous?)}
      [:span {:class "sr-only"}
       "Go to previous page"]
      [:> ChevronLeft]]
     [mateuszmazurczak-button/button {:variant :outline
                                      :size :icon
                                      :class "size-8"
                                      :on-click #(when on-next-page (on-next-page))
                                      :disabled (not can-next?)}
      [:span {:class "sr-only"}
       "Go to next page"]
      [:> ChevronRight]]
     [mateuszmazurczak-button/button {:variant :outline
                                      :size :icon
                                      :class "hidden size-8 lg:flex"
                                      :on-click #(when on-last-page (on-last-page))
                                      :disabled (not can-next?)}
      [:span {:class "sr-only"}
       "Go to last page"]
      [:> ChevronsRight]]]]])

(defc data-table
 "Comprehensive data table component with full TanStack Table integration.
  
  This component manages all TanStack Table state and API interactions.
  Child components are pure UI that receive extracted state and callbacks.
  
  Props:
  - `:columns`                    - Vector of TanStack Table column definitions (required)
  - `:data`                       - Vector of data rows (required)
  - `:initial-page-size`          - Initial rows per page (default: 25)
  - `:initial-column-visibility`  - Map of column-id to boolean for initial visibility (default: {})
  - `:toolbar-config`             - Configuration map for toolbar (optional)
  - `:empty-state`                - Component to render when no data exists (optional)
  - `:no-results-state`           - Component or function (fn [on-reset-filters]) to render when filters yield no results (optional)
  
  Toolbar config shape:
  ```clojure
  {:text-filter {:column-id 'title'            ; Column to apply text search on
                 :placeholder 'Search...'}      ; Placeholder for search input
   :faceted-filters [{:column-id 'status'      ; Multi-select dropdown filters
                      :title 'Status'
                      :options [{:label 'Done' :value 'done'}]}]
   :toolbar-end (fn [table] ...)}              ; Optional custom content on right
  ```
  
  Features:
  - **Sorting**: Click column headers to sort (if configured in column def)
  - **Filtering**: Text search and multi-select faceted filters
  - **Pagination**: Configurable page size with navigation controls
  - **Row Selection**: Multi-select with checkboxes (if configured in column def)
  - **Column Visibility**: Show/hide columns dynamically
  - **Responsive**: Mobile-friendly layout
  - **Empty State**: Displays custom empty state when no data or no results from filters"
 [{:keys [columns
          data
          initial-page-size
          initial-column-visibility
          toolbar-config
          empty-state
          no-results-state]
   :or {initial-page-size 25}}]
 (let [[row-selection set-row-selection] (rhooks/use-state #js {})
       [column-visibility set-column-visibility] (rhooks/use-state
                                                  (clj->js (or initial-column-visibility {})))
       [column-filters set-column-filters] (rhooks/use-state #js [])
       [sorting set-sorting] (rhooks/use-state #js [])
       table-config (rhooks/use-memo
                     (fn []
                       #js {:data data
                            :columns columns
                            :initialState #js {:pagination #js {:pageSize initial-page-size}}
                            :state #js {:sorting sorting
                                        :columnVisibility column-visibility
                                        :rowSelection row-selection
                                        :columnFilters column-filters}
                            :enableRowSelection true
                            :onRowSelectionChange set-row-selection
                            :onSortingChange set-sorting
                            :onColumnFiltersChange set-column-filters
                            :onColumnVisibilityChange set-column-visibility
                            :getCoreRowModel (getCoreRowModel)
                            :getFilteredRowModel (getFilteredRowModel)
                            :getPaginationRowModel (getPaginationRowModel)
                            :getSortedRowModel (getSortedRowModel)
                            :getFacetedRowModel (getFacetedRowModel)
                            :getFacetedUniqueValues (getFacetedUniqueValues)})
                     #js [data columns sorting column-visibility row-selection column-filters])
       table-instance (useReactTable table-config)
       toolbar-data (when toolbar-config (extract-toolbar-data toolbar-config table-instance))
       pagination-data (extract-pagination-data table-instance)
       has-data? (pos? (.-length data))
       is-filtered? (pos? (.-length column-filters))
       current-empty-state (cond
                             (not has-data?) empty-state
                             is-filtered? no-results-state
                             :else nil)
       table-data {:header-groups (.getHeaderGroups table-instance)
                   :rows (.. table-instance getRowModel -rows)
                   :columns-count (.-length columns)
                   :row-selection row-selection
                   :empty-state current-empty-state
                   :no-results-state (when is-filtered? no-results-state)
                   :on-reset-filters (when toolbar-data (:on-reset-filters toolbar-data))}]
   [:div {:class "flex min-h-0 flex-1 flex-col gap-4"}
    (when toolbar-data [toolbar-ui toolbar-data])
    [table-ui table-data]
    [pagination-ui pagination-data]]))
