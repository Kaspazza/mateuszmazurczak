(ns mateuszmazurczak.portfolio.utils
  (:require
   ["lucide-react"                            :refer [Check ChevronDown ChevronUp Copy]]
   [clojure.string                            :as str]
   [mateuszmazurczak.ui.components.button     :as button]
   [mateuszmazurczak.ui.components.code-block :as code-block]
   [reagent.core                              :as r]))

(defn wrap-component
  "A simple wrapper to enable additional code to be added for each scene.
   Right now it's empty"
  [& cmps]
  (into [:<>] cmps))

(defn copy-to-clipboard!
  "Copy text to clipboard with feedback.
  
  Args:
  - text: String to copy
  - copied-atom: Reagent atom to set to true on success (auto-resets after 2s)
  
  Returns: Function that performs the copy when called"
  [text copied-atom]
  (fn []
    (-> (js/navigator.clipboard.writeText text)
        (.then (fn [] (reset! copied-atom true) (js/setTimeout #(reset! copied-atom false) 2000)))
        (.catch (fn [err] (js/console.error "Failed to copy:" err))))))

(defn expandable-code-block
  "Renders an expandable code block with copy functionality.
  
  Props:
  - :source-code - The source code string to display
  - :language - Programming language for syntax highlighting (default: \"clojure\")
  - :filename - Filename to display in header (e.g., \"avatar.cljs\")
  - :collapsed-height - Height class when collapsed (default: \"max-h-64\")
  
  Example:
  [expandable-code-block {:source-code code-str
                          :filename \"button.cljs\"}]"
  [{:keys [source-code language filename collapsed-height]
    :or {language "clojure"
         collapsed-height "max-h-64"}}]
  ;; Use Form-2 component to maintain state across re-renders
  (let [expanded? (r/atom false)
        copied? (r/atom false)]
    (fn []
      [:div {:class "relative"}
       [:div {:class (when-not @expanded? (str collapsed-height " overflow-hidden relative"))}
        [code-block/code-block
         [code-block/code-block-group {:class "px-4 py-2 border-b"}
          [:span {:class "text-xs text-muted-foreground"}
           filename]
          [button/button {:size :sm
                          :variant :ghost
                          :on-click (copy-to-clipboard! source-code copied?)
                          :class "gap-1"}
           (if @copied?
             [:<> [:> Check {:class "size-4"}] "Copied"]
             [:<> [:> Copy {:class "size-4"}] "Copy"])]]
         [code-block/code-block-code {:code source-code
                                      :language language}]]
        (when-not @expanded?
          [:div
           {:class
            "absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-t from-card to-transparent pointer-events-none"}])]
       [:div {:class "mt-2 flex justify-center"}
        [button/button {:variant :ghost
                        :size :sm
                        :on-click #(swap! expanded? not)
                        :class "gap-1"}
         (if @expanded?
           [:<> [:> ChevronUp {:class "size-4"}] [:span "Show less"]]
           [:<> [:> ChevronDown {:class "size-4"}] [:span "Show more"]])]]])))

(defn installation-scene
  "Standard installation scene for portfolio components.
  
  Props:
  - :description - Brief description of the component
  - :npm-install - NPM install command (e.g., \"npm install @radix-ui/react-avatar\")
  - :source-code - Embedded source code string
  - :namespace-path - Display path for where to copy the file (e.g., \"src/cljs/...\")
  - :filename - Filename for the code block header (e.g., \"avatar.cljs\")
  
  Example:
  [installation-scene {:description \"Avatar component based on Radix UI primitives.\"
                       :npm-install \"npm install @radix-ui/react-avatar\"
                       :source-code (embed-source my.namespace)
                       :namespace-path \"src/cljs/my/namespace.cljs\"
                       :filename \"namespace.cljs\"}]"
  [{:keys [description npm-install source-code namespace-path filename]}]
  (wrap-component
   [:div {:class "p-6 max-w-4xl"}
    [:div {:class "space-y-6"}
     [:div
      [:p {:class "text-sm text-muted-foreground"}
       description]]
     [:div
      [:h4 {:class "text-sm font-semibold mb-2"}
       "1. Install Dependencies"]
      [:div {:class "rounded-md bg-muted p-4"}
       [:code {:class "text-sm"}
        npm-install]]]
     [:div
      [:h4 {:class "text-sm font-semibold mb-2"}
       "2. Copy Component Code"]
      [:p {:class "text-xs text-muted-foreground mb-3"}
       "Copy and paste the following code into your project at: "
       [:code {:class "bg-muted px-1 py-0.5 rounded"}
        namespace-path]]
      [expandable-code-block {:source-code source-code
                              :filename filename}]]]]))

(defn- prop-type-badges
  "Renders one or more type badges split on ' | '."
  [type-str]
  (let [parts (str/split (or type-str "—") #"\s*\|\s*")]
    (into [:<>]
          (interpose [:span {:class "text-xs text-muted-foreground mx-0.5"} "|"]
                     (for [p parts]
                       [:code {:class "text-xs bg-muted px-1.5 py-0.5 rounded"} p])))))

(defn api-prop-row
  "Renders a single prop row in a table-based API documentation.

  Args (map):
  - :name        - Prop name string, e.g. \":class\"
  - :type        - Type string, e.g. \"string\" or \"boolean | :indeterminate\"
  - :default     - Default value string, or nil for none (renders —)
  - :description - Description shown as a second sub-row when provided

  Example:
  [api-prop-row {:name \":class\" :type \"string\" :default nil
                 :description \"Additional Tailwind classes merged with defaults.\"}]"
  [{:keys [name type default description]}]
  [:<>
   [:tr {:class "border-b last:border-0"}
    [:td {:class "py-3 pr-4 align-top"}
     [:code {:class "text-xs bg-blue-500/10 text-blue-600 dark:text-blue-400 px-1.5 py-0.5 rounded font-medium"}
      name]]
    [:td {:class "py-3 pr-4 align-top"}
     [prop-type-badges type]]
    [:td {:class "py-3 align-top"}
     (if default
       [:code {:class "text-xs bg-muted px-1.5 py-0.5 rounded"} default]
       [:span {:class "text-xs text-muted-foreground"} "—"])]]
   (when description
     [:tr {:class "border-b last:border-0"}
      [:td {:col-span 3 :class "pb-3 pt-0 text-xs text-muted-foreground"}
       description]])])

(defn api-component-card
  "Renders a component card in API documentation with a Prop/Type/Default table.

  Props:
  - :component-name - Name of the component
  - :link           - Optional map {:href \"...\" :label \"...\"} rendered below the name
  - :description    - Brief description of the component
  - :props          - Vector of prop maps, each:
                      {:name \"...\" :type \"...\" :default \"...\" :description \"...\"}
                      :default and :description are optional (nil renders —/nothing)

  Example:
  [api-component-card
   {:component-name \"checkbox\"
    :link           {:href \"https://radix-ui.com/...\" :label \"Radix UI Docs\"}
    :description    \"Root checkbox component.\"
    :props          [{:name \":checked\" :type \"boolean\" :default nil
                      :description \"Controlled checked state.\"}]}]"
  [{:keys [component-name link description props]}]
  [:div {:class "border rounded-lg p-4"}
   [:h4 {:class "text-sm font-semibold mb-1"}
    component-name]
   (when link
     [:a {:href   (:href link)
          :target "_blank"
          :rel    "noopener noreferrer"
          :class  "text-xs text-primary underline underline-offset-2 hover:opacity-80 mb-2 inline-block"}
      (:label link)])
   [:p {:class "text-sm text-muted-foreground mb-3 mt-2"}
    description]
   [:table {:class "w-full text-sm"}
    [:thead {}
     [:tr {:class "border-b"}
      [:th {:class "pb-2 text-left text-xs font-semibold"} "Prop"]
      [:th {:class "pb-2 text-left text-xs font-semibold"} "Type"]
      [:th {:class "pb-2 text-left text-xs font-semibold"} "Default"]]]
    [:tbody {}
     (for [{:keys [name] :as prop} props]
       ^{:key name}
       [api-prop-row prop])]]])
