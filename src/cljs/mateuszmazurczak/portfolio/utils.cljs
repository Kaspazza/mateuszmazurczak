(ns mateuszmazurczak.portfolio.utils
  (:require
   ["lucide-react"                            :refer [Check ChevronDown ChevronUp Copy]]
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

(defn api-prop-row
  "Renders a single prop row in API documentation.
  
  Args:
  - prop-name: Name of the prop (e.g., \":class\")
  - description: Description of the prop (e.g., \"string, optional - Additional classes\")
  
  Example:
  [api-prop-row \":class\" \"string, optional - Additional Tailwind classes\"]"
  [prop-name description]
  [:div {:class "flex gap-2"}
   [:code {:class "text-xs bg-muted px-2 py-1 rounded"}
    prop-name]
   [:span {:class "text-xs text-muted-foreground"}
    description]])

(defn api-component-card
  "Renders a component card in API documentation.
  
  Props:
  - :component-name - Name of the component
  - :description - Brief description
  - :props - Vector of [prop-name description] tuples
  
  Example:
  [api-component-card {:component-name \"avatar\"
                       :description \"Root container component\"
                       :props [[\": class\" \"string, optional - Additional classes\"]]}]"
  [{:keys [component-name description props]}]
  [:div {:class "border rounded-lg p-4"}
   [:h4 {:class "text-sm font-semibold mb-2"}
    component-name]
   [:p {:class "text-sm text-muted-foreground mb-3"}
    description]
   [:div {:class "space-y-2"}
    (for [[prop-name prop-desc] props] ^{:key prop-name} [api-prop-row prop-name prop-desc])]])
