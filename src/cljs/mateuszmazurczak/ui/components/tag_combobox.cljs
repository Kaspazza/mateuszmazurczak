(ns mateuszmazurczak.ui.components.tag-combobox
  "Tag combobox component for selecting existing tags or creating new ones.
  
  Responsive component that uses popover on desktop and sheet on mobile.
  Allows filtering through existing tags and creating new ones if no match found."
  (:require
   ["lucide-react"                           :refer [Check Plus]]
   [clojure.string                           :as str]
   [mateuszmazurczak.ui.components.button    :as mateuszmazurczak-button]
   [mateuszmazurczak.ui.components.command   :as command]
   [mateuszmazurczak.ui.components.popover   :as popover]
   [mateuszmazurczak.ui.components.sheet     :as sheet]
   [mateuszmazurczak.ui.hooks.use-is-mobile  :as use-mobile]
   [mateuszmazurczak.utils.styles            :refer [merge-classes]]
   [reagent.core                             :as    r
                                             :refer [defc]]
   [reagent.hooks                            :as hooks]))

(defc tag-list
 "Command list component for tag selection.
  
  Props:
  - `:tags` - Set of available tag strings
  - `:selected-tag` - Currently selected tag (string or nil)
  - `:on-select` - Callback when tag is selected (fn [tag])
  - `:on-create` - Callback when new tag should be created (fn [tag])
  - `:set-open` - Callback to close the popover/sheet (fn [open?])"
 [{:keys [tags selected-tag on-select on-create set-open]}]
 (let [[search-value set-search-value] (hooks/use-state "")
       filtered-tags (if (and search-value (not= search-value ""))
                       (filter #(re-find (re-pattern (str "(?i)" search-value)) %) tags)
                       tags)
       exact-match? (some #(= (str/lower-case search-value) (str/lower-case %)) tags)
       show-create? (and search-value (not= search-value "") (not exact-match?))]
   [command/command {}
    [command/command-input {:placeholder "Search tags..."
                            :value search-value
                            :onValueChange #(set-search-value %)}]
    [command/command-list {}
     [command/command-empty {}
      "No tags found."]
     (when (seq filtered-tags)
       [command/command-group {}
        (for [tag (sort filtered-tags)]
          ^{:key tag}
          [command/command-item {:value tag
                                 :on-select
                                 (fn [_] (when on-select (on-select tag)) (set-open false))}
           [:>
            Check
            {:class (merge-classes "size-4" (if (= selected-tag tag) "opacity-100" "opacity-0"))}]
           [:span tag]])])
     (when show-create?
       [:<>
        (when (seq filtered-tags)
          [command/command-separator {}])
        [command/command-group {:forceMount true}
         [command/command-item {:on-select
                                (fn [_] (when on-create (on-create search-value)) (set-open false))
                                :forceMount true}
          [:> Plus {:class "size-4"}]
          [:span (str "Create \"" search-value "\"")]]]])]]))

(defc tag-combobox
 "Responsive tag combobox component.
  
  Displays a popover on desktop and a sheet (drawer) on mobile.
  Allows selecting from existing tags or creating new ones.
  
  Props:
  - `:tags` - Set or vector of available tag strings
  - `:selected-tag` - Currently selected tag (string or nil)
  - `:on-select` - Callback when tag is selected (fn [tag])
  - `:on-create` - Callback when new tag should be created (fn [tag])
  - `:placeholder` - Placeholder text for trigger button (default: '+ Add tag')
  - `:class` - Additional Tailwind classes for trigger button
  
  Example:
  ```clojure
  (let [available-tags #{\"important\" \"urgent\" \"review\"}
        selected-tag (r/atom nil)]
    [tag-combobox
     {:tags available-tags
      :selected-tag @selected-tag
      :on-select #(reset! selected-tag %)
      :on-create #(do
                    ;; Add to available tags
                    ;; Dispatch event to backend
                    (reset! selected-tag %))}])
  ```"
 [{:keys [tags selected-tag on-select on-create placeholder class]
   :or {placeholder "+ Add tag"}}]
 (let [[open? set-open] (hooks/use-state false)
       is-mobile? (use-mobile/use-is-mobile)
       tags-set (if (set? tags) tags (set tags))]
   (if is-mobile?
     ;; Mobile: use sheet (drawer)
     [sheet/sheet {:open open?
                   :on-open-change (fn [is-open] (set-open is-open))}
      [sheet/sheet-trigger {:as-child true}
       (mateuszmazurczak-button/button {:variant :outline
                                        :class (merge-classes "w-[150px] justify-start" class)}
                                       (if selected-tag selected-tag placeholder))]
      [sheet/sheet-content {:side :bottom}
       [:div {:class "mt-4"}
        [tag-list {:tags tags-set
                   :selected-tag selected-tag
                   :on-select on-select
                   :on-create on-create
                   :set-open set-open}]]]]
     ;; Desktop: use popover
     [popover/popover {:open open?
                       :onOpenChange (fn [is-open] (set-open is-open))}
      [popover/popover-trigger {:asChild true}
       (mateuszmazurczak-button/button {:variant :outline
                                        :class (merge-classes "w-[150px] justify-start" class)}
                                       (if selected-tag selected-tag placeholder))]
      [popover/popover-content {:class "w-[200px] p-0"
                                :align "start"}
       [tag-list {:tags tags-set
                  :selected-tag selected-tag
                  :on-select on-select
                  :on-create on-create
                  :set-open set-open}]]])))
