# Portfolio Component Documentation Standard

## Overview
Add comprehensive documentation to portfolio components following the pattern established in `avatar.cljs`. This includes Installation and API Reference scenes with auto-embedded source code.

## Prerequisites
The following infrastructure is already in place:
- **Macro**: `mateuszmazurczak.portfolio.macros/embed-source` - Embeds source code at compile-time
- **Utilities**: `mateuszmazurczak.portfolio.utils` - Shared components for documentation scenes

## Task: Add Documentation to Portfolio Components

### 1. Update Component Implementation (e.g., `src/cljs/mateuszmazurczak/ui/components/button.cljs`)

Add version information to the namespace docstring:

```clojure
(ns mateuszmazurczak.ui.components.button
  "Button component for user interactions.
  
  Version: 1.0.0
  Last updated: 2025-02-05
  
  Based on shadcn/ui Button component.
  Documentation: https://ui.shadcn.com/docs/components/button"
  (:require ...))
```

**Guidelines:**
- Use semantic versioning (1.0.0)
- Update date when making changes
- Include origin (shadcn/ui, Radix UI, custom)
- Add documentation links if available

### 2. Update Portfolio File (e.g., `src/cljs/mateuszmazurczak/portfolio/ui_components/button.cljs`)

#### Required Imports

```clojure
(ns mateuszmazurczak.portfolio.ui-components.button
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button    :as sut]  ; component under test
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]])
  (:require-macros
   [mateuszmazurczak.portfolio.macros :refer [embed-source]]))
```

#### Add Installation Scene

Add as the FIRST scene after `configure-scenes`:

```clojure
(defscene
 installation
 "Install dependencies and copy the component code into your project."
 []
 [mm-portfolio-utils/installation-scene
  {:description "Button component for user interactions with multiple variants."
   :npm-install "npm install @radix-ui/react-slot"  ; or "No external dependencies" if none
   :source-code (embed-source mateuszmazurczak.ui.components.button)
   :namespace-path "src/cljs/mateuszmazurczak/ui/components/button.cljs"
   :filename "button.cljs"}])
```

**Props:**
- `:description` - Brief, one-sentence description
- `:npm-install` - Full npm command, or "No external dependencies" if pure component
- `:source-code` - Use `(embed-source your.component.namespace)` macro
- `:namespace-path` - Full path where user should paste the code
- `:filename` - Just the filename for the code block header

#### Add API Reference Scene

Add as the SECOND scene:

```clojure
(defscene
 api-reference
 "Complete reference for all Button component props, variants, and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Button component."]]
    
    [:div {:class "space-y-4"}
     ;; For each component/function, add a card:
     [mm-portfolio-utils/api-component-card
      {:component-name "button"
       :description "Main button component"
       :props [[":variant" "\"default\" | \"destructive\" | \"outline\" | \"secondary\" | \"ghost\" | \"link\" - Button visual style"]
               [":size" "\"default\" | \"sm\" | \"lg\" | \"icon\" - Button size"]
               [":class" "string, optional - Additional Tailwind classes"]
               [":disabled" "boolean, optional - Disable button interaction"]
               [":as-child" "boolean, optional - Compose with child element"]]}]
     
     ;; Add usage example at the end
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"} "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code
        "[button {:variant :primary\n"
        "         :size :lg\n"
        "         :on-click #(js/alert \"Clicked!\")}\n"
        " \"Click Me\"]"]]]]]]]))
```

**Guidelines for API Reference:**
- Use `api-component-card` for each component/function
- List ALL props with their types and descriptions
- For enum props, list all possible values with | separator
- Mark required vs optional clearly
- Add a usage example at the end showing typical usage

### 3. Docstring Best Practices

**IMPORTANT**: Portfolio displays the scene name AND the docstring, so:
- ❌ DON'T repeat the scene name: `"Installation\n\nInstall dependencies..."`
- ✅ DO write a brief description: `"Install dependencies and copy the component code..."`
- Keep docstrings brief (1-2 sentences max)
- The rendered scene will contain the detailed content

### 4. Examples of Different Component Types

#### Simple Component (no dependencies)
```clojure
[mm-portfolio-utils/installation-scene
 {:description "Loader component for indicating loading states."
  :npm-install "No external dependencies"
  :source-code (embed-source mateuszmazurczak.ui.components.loader)
  :namespace-path "src/cljs/mateuszmazurczak/ui/components/loader.cljs"
  :filename "loader.cljs"}]
```

#### Multi-Component API (e.g., Dialog with Dialog-Content, Dialog-Header, etc.)
```clojure
[mm-portfolio-utils/api-component-card
 {:component-name "dialog"
  :description "Root dialog component"
  :props [[":open" "boolean - Control dialog visibility"]
          [":onOpenChange" "function - Callback when dialog state changes"]]}]

[mm-portfolio-utils/api-component-card
 {:component-name "dialog-trigger"
  :description "Button that opens the dialog"
  :props [[":as-child" "boolean - Compose with child element"]]}]

[mm-portfolio-utils/api-component-card
 {:component-name "dialog-content"
  :description "The dialog content container"
  :props [[":class" "string, optional - Additional classes"]]}]
```

## Reference Implementation

See `src/cljs/mateuszmazurczak/portfolio/ui_components/avatar.cljs` for a complete, working example.

Key files to review:
- **Component**: `src/cljs/mateuszmazurczak/ui/components/avatar.cljs` (versioned docstring)
- **Portfolio**: `src/cljs/mateuszmazurczak/portfolio/ui_components/avatar.cljs` (documentation scenes)
- **Utilities**: `src/cljs/mateuszmazurczak/portfolio/utils.cljs` (shared helpers)
- **Macro**: `src/clj/mateuszmazurczak/portfolio/macros.clj` (embed-source)

## Benefits

✅ **Always in sync** - Source code embedded at compile-time, no copy-paste drift  
✅ **Copy button** - One-click copy with visual feedback  
✅ **Expandable** - Long code collapsed by default with "Show more" button  
✅ **Versioned** - Track component versions and update dates  
✅ **Consistent** - Standard format across all components  
✅ **DRY** - Reusable utilities eliminate boilerplate

## Checklist

For each portfolio component:
- [ ] Add version docstring to component implementation
- [ ] Add `:require-macros` with `embed-source`
- [ ] Add `installation` scene as first scene
- [ ] Add `api-reference` scene as second scene  
- [ ] Use utility functions from `mm-portfolio-utils`
- [ ] Keep docstrings brief (don't repeat scene names)
- [ ] List all props with types and descriptions
- [ ] Add usage example to API reference
- [ ] Test in browser that code is visible and copy works
