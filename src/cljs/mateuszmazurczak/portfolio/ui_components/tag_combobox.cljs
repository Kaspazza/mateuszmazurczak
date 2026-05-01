(ns mateuszmazurczak.portfolio.ui-components.tag-combobox
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.tag-combobox :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]
   [reagent.core                                :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Tag Combobox"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Tag combobox component for selecting existing tags or creating new ones."
            :npm-install "npm install lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.tag_combobox")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/tag_combobox.cljs"
            :filename "tag_combobox.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "tag-combobox"
               :description "Responsive tag selector that supports both choosing existing tags and creating new tags. Uses Popover on desktop and Sheet on mobile for better ergonomics."
               :link {:href "https://ui.shadcn.com/docs/components/combobox" :label "Combobox Pattern (shadcn)"}
               :props [{:name ":tags"         :type "set<string> | vector<string>" :default nil           :description "Available tags shown in the list."}
                        {:name ":selected-tag" :type "string | nil"                 :default nil           :description "Currently selected tag."}
                        {:name ":on-select"    :type "function"                     :default nil           :description "Called when user selects a tag: (fn [tag] ...)."}
                        {:name ":on-create"    :type "function"                     :default nil           :description "Called when user creates a new tag: (fn [new-tag] ...)."}
                        {:name ":placeholder"  :type "string"                       :default "\"+ Add tag\"" :description "Trigger button text when no tag is selected."}
                        {:name ":class"        :type "string"                       :default nil           :description "Additional Tailwind classes merged into the trigger button."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "The component is controlled by your state: keep :selected-tag in sync via :on-select."]
                [:li "To allow creating missing tags, provide :on-create and persist the new tag in your tag source."]
                [:li "Desktop and mobile render different containers (Popover vs Sheet) but share the same API."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [tags (r/atom #{\"bug\" \"feature\"})\n      selected (r/atom nil)]\n  [tag-combobox {:tags @tags\n                 :selected-tag @selected\n                 :on-select #(reset! selected %)\n                 :on-create (fn [new-tag]\n                              (swap! tags conj new-tag)\n                              (reset! selected new-tag))}])"]]
               ]]]]]))

(defscene
 basic-tag-selection
 "Basic tag combobox with predefined tags.
   
   Select from existing tags or create new ones.
   Responsive: uses popover on desktop, sheet on mobile."
 []
 (let [available-tags (r/atom #{"urgent" "important" "review" "blocked" "in-progress"})
       selected-tag (r/atom nil)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8 space-y-4"}
       [:div
        [:h3 {:class "text-lg font-semibold mb-2"}
         "Select a Tag"]
        [:p {:class "text-sm text-muted-foreground mb-4"}
         "Choose from existing tags or create a new one"]
        [sut/tag-combobox {:tags @available-tags
                           :selected-tag @selected-tag
                           :on-select #(reset! selected-tag %)
                           :on-create (fn [new-tag]
                                        (swap! available-tags conj new-tag)
                                        (reset! selected-tag new-tag))}]]
       [:div {:class "mt-6 p-4 bg-muted rounded-md"}
        [:p {:class "text-sm font-medium"}
         "State:"]
        [:pre {:class "text-xs mt-2"}
         (str "Selected: "
              (pr-str @selected-tag)
              "\n"
              "Available: "
              (pr-str @available-tags))]]]))))

(defscene tag-with-custom-placeholder
          "Tag combobox with custom placeholder text."
          []
          (let [tags (r/atom #{"feature" "bug" "enhancement" "documentation"})
                selected (r/atom nil)]
            (fn []
              (mm-portfolio-utils/wrap-component [:div {:class "p-8"}
                                                  [:h3 {:class "text-lg font-semibold mb-4"}
                                                   "Issue Type"]
                                                  [sut/tag-combobox
                                                   {:tags @tags
                                                    :selected-tag @selected
                                                    :placeholder "+ Select type"
                                                    :on-select #(reset! selected %)
                                                    :on-create (fn [new-tag]
                                                                 (swap! tags conj new-tag)
                                                                 (reset! selected new-tag))}]]))))

(defscene tag-with-custom-width
          "Tag combobox with custom width styling."
          []
          (let [tags (r/atom #{"red" "green" "blue" "yellow" "purple"})
                selected (r/atom nil)]
            (fn []
              (mm-portfolio-utils/wrap-component [:div {:class "p-8"}
                                                  [:h3 {:class "text-lg font-semibold mb-4"}
                                                   "Color Tag"]
                                                  [sut/tag-combobox
                                                   {:tags @tags
                                                    :selected-tag @selected
                                                    :class "w-[200px]"
                                                    :placeholder "+ Choose color"
                                                    :on-select #(reset! selected %)
                                                    :on-create (fn [new-tag]
                                                                 (swap! tags conj new-tag)
                                                                 (reset! selected new-tag))}]]))))

(defscene
 multiple-tag-comboboxes
 "Multiple independent tag comboboxes.
   
   Shows how to use multiple tag comboboxes with different tag sets."
 []
 (let [priority-tags (r/atom #{"low" "medium" "high" "critical"})
       status-tags (r/atom #{"todo" "doing" "done" "archived"})
       selected-priority (r/atom nil)
       selected-status (r/atom nil)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8 space-y-6"}
       [:div
        [:h3 {:class "text-lg font-semibold mb-2"}
         "Task Management"]
        [:div {:class "flex gap-4 mt-4"}
         [:div {:class "space-y-2"}
          [:label {:class "text-sm font-medium"}
           "Priority"]
          [sut/tag-combobox {:tags @priority-tags
                             :selected-tag @selected-priority
                             :placeholder "+ Priority"
                             :on-select #(reset! selected-priority %)
                             :on-create (fn [new-tag]
                                          (swap! priority-tags conj new-tag)
                                          (reset! selected-priority new-tag))}]]
         [:div {:class "space-y-2"}
          [:label {:class "text-sm font-medium"}
           "Status"]
          [sut/tag-combobox {:tags @status-tags
                             :selected-tag @selected-status
                             :placeholder "+ Status"
                             :on-select #(reset! selected-status %)
                             :on-create (fn [new-tag]
                                          (swap! status-tags conj new-tag)
                                          (reset! selected-status new-tag))}]]]]
       [:div {:class "mt-6 p-4 bg-muted rounded-md"}
        [:p {:class "text-sm font-medium"}
         "Selection:"]
        [:p {:class "text-xs mt-2"}
         (str "Priority: " @selected-priority)]
        [:p {:class "text-xs"}
         (str "Status: " @selected-status)]]]))))

(defscene
 tag-creation-workflow
 "Demonstrates the tag creation workflow.
   
   Type a tag name that doesn't exist and see the 'Create' option appear."
 []
 (let [tags (r/atom #{"existing-tag-1" "existing-tag-2"})
       selected (r/atom nil)
       creation-log (r/atom [])]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-8 space-y-4"}
       [:div
        [:h3 {:class "text-lg font-semibold mb-2"}
         "Tag Creation Demo"]
        [:p {:class "text-sm text-muted-foreground mb-4"}
         "Try typing 'new-tag' to see the create option"]
        [sut/tag-combobox {:tags @tags
                           :selected-tag @selected
                           :on-select #(reset! selected %)
                           :on-create (fn [new-tag]
                                        (swap! tags conj new-tag)
                                        (reset! selected new-tag)
                                        (swap! creation-log conj
                                          {:tag new-tag
                                           :timestamp (.toISOString (js/Date.))}))}]]
       [:div {:class "mt-6 p-4 bg-muted rounded-md"}
        [:p {:class "text-sm font-medium mb-2"}
         "Available tags:"]
        [:div {:class "flex flex-wrap gap-2 mb-4"}
         (for [tag @tags]
           ^{:key tag}
           [:span {:class "text-xs bg-primary text-primary-foreground px-2 py-1 rounded"}
            tag])]
        (when (seq @creation-log)
          [:div
           [:p {:class "text-sm font-medium mb-2"}
            "Creation log:"]
           [:div {:class "space-y-1"}
            (for [[idx entry] (map-indexed vector @creation-log)]
              ^{:key idx}
              [:p {:class "text-xs"}
               (str "Created: " (:tag entry))])]])]]))))
