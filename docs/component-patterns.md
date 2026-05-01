# Component Patterns & Conventions

This document describes the patterns, conventions, and architecture decisions used across all components in the library. Understanding these will help you modify existing components or build new ones that fit naturally into the system.

---

## Component Categories

Components fall into three categories based on their behaviour layer:

### 1. Radix-Wrapped Components

These use [Radix UI](https://www.radix-ui.com/) primitives for behaviour (accessibility, keyboard navigation, focus management) and add Tailwind styling on top.

**Examples:** `dialog`, `dropdown-menu`, `select`, `tooltip`, `popover`, `switch`, `checkbox`, `radio-group`, `collapsible`, `avatar`, `separator`, `label`

**Pattern:**
```clojure
(ns your.ui.components.dialog
  (:require
   ["@radix-ui/react-dialog" :as RadixDialog]
   [your.utils.styles :refer [merge-classes]]))

(defn dialog [props & children]
  (into [:> RadixDialog/Root (assoc props :data-slot "dialog")] children))

(defn dialog-content [{:keys [class] :as props} & children]
  [:> RadixDialog/Content
   (-> props
       (assoc :data-slot "dialog-content"
              :class (merge-classes "bg-background ..." class))
       (dissoc :class-name))])
```

**Key traits:**
- Use `:>` (Reagent interop) to render React components.
- Each Radix primitive gets its own Clojure wrapper function.
- Props are forwarded after consuming component-specific keys.
- `data-[state=open/closed]` selectors drive animations.

### 2. Custom Styled Components

These don't use Radix — they're pure HTML elements with Tailwind styling.

**Examples:** `button`, `badge`, `input`, `textarea`, `table`, `skeleton`, `spinner`, `loader`, `separator`, `field` (and sub-components)

**Pattern:**
```clojure
(defn input [{:keys [class type] :or {type "text"} :as props}]
  [:input
   (-> props
       (assoc :data-slot "input"
              :type type
              :class (merge-classes "flex h-10 w-full rounded-md ..." class))
       (dissoc :class-name))])
```

**Key traits:**
- Render plain HTML elements (`:div`, `:input`, `:button`, `:span`).
- All styling is Tailwind classes.
- No JS dependencies beyond `tailwind-merge`.

### 3. JS-Library Components

These wrap third-party JS libraries that provide complex behaviour beyond what Radix offers.

**Examples:** `carousel` (Embla), `data-table` (TanStack Table + dnd-kit), `notification` (Sonner), `code-block` (Shiki), `command` (cmdk), `drawer` (Vaul)

**Pattern:**
```clojure
(ns your.ui.components.carousel
  (:require
   ["embla-carousel-react" :as embla-carousel]
   [reagent.core :as r :refer [defc]]
   [reagent.hooks :as rhooks]))

(defc carousel-component [{:keys [...]}]
  (let [[embla-ref embla-api] (rhooks/use-state nil)
        ;; hook into JS library
        ...]
    [:div {:data-slot "carousel"} ...]))
```

**Key traits:**
- Use `defc` (Reagent hooks component) when hooks are needed.
- React context (`react/createContext`) for parent-child communication.
- Convert between ClojureScript data and JS objects at the boundary.

---

## The Props Convention

Every component follows this props pattern:

```clojure
(defn component-name
  [{:keys [class variant size some-specific-prop]
    :or {variant :default
         size :default}
    :as props}
   & children]
  ...)
```

### Rules:

1. **First argument is always a map** — Even if empty: `[button {} "Click"]`.
2. **`:class` is always supported** — Merged with defaults via `merge-classes`.
3. **Component-specific keys are destructured and dissoc'd** — `:variant`, `:size`, `:as-child`, etc.
4. **Everything else passes through to the DOM** — `:on-click`, `:disabled`, `:aria-label`, `:id`, etc.
5. **Keywords for enums, not strings** — `:destructive` not `"destructive"`, `:sm` not `"sm"`.

### The dissoc pattern:

```clojure
(-> props
    (assoc :data-slot "button" :class combined)
    (dissoc :variant :size :as-child :class-name))
```

`:class-name` is dissoc'd as a safety measure — it catches cases where JS interop might pass `className` as a keyword.

---

## Variant Resolution

Variants are resolved using `case` on keywords:

```clojure
(defn- variant-classes [variant]
  (case variant
    :default     "bg-primary text-primary-foreground hover:bg-primary/90"
    :destructive "bg-destructive text-destructive-foreground hover:bg-destructive/90"
    :outline     "border border-input bg-background hover:bg-accent"
    ;; fallback matches the default
    "bg-primary text-primary-foreground hover:bg-primary/90"))
```

The `case` fallback (string at the end, no keyword) catches unexpected values gracefully.

Some components have multiple variant dimensions (e.g., button has both `:variant` and `:size`), each resolved by a separate function, then merged:

```clojure
(merge-classes base-classes (variant-classes variant) (size-classes size) class)
```

The order matters: `class` (user override) comes last and wins in conflicts.

---

## Polymorphic Rendering (`:as-child`)

Several components support `:as-child` via Radix's `Slot` component:

```clojure
["@radix-ui/react-slot" :refer [Slot]]

(let [component (if as-child Slot "button")]
  [:> component {:class combined} ...])
```

When `:as-child` is `true`, the component doesn't render its own element. Instead, it merges its props into its child element. This lets you render a button as an `<a>` tag:

```clojure
[button {:as-child true}
  [:a {:href "/login"} "Login"]]
;; Renders: <a href="/login" class="...button-classes...">Login</a>
```

---

## Context Pattern (React Context in Reagent)

Complex components that need parent-child communication use React Context:

```clojure
(def ^:private my-context (react/createContext nil))

;; Provider component
(defn parent [{:keys [value]} & children]
  (into [:> (.-Provider my-context) {:value (clj->js value)}]
        children))

;; Consumer component (using hooks)
(defc child [props]
  (let [ctx (rhooks/use-context my-context)
        some-value (.-someValue ctx)]
    [:div ...]))
```

**Used by:** `stepper`, `carousel`, `sidebar`

The data flows through `clj->js` at the provider and is read via property access (`.-`) in consumers. This is a pragmatic choice — React Context requires JS objects.

---

## Accessibility Patterns

### Radix Components

Radix handles accessibility automatically:
- `aria-labelledby` / `aria-describedby` — Linked to `Title` and `Description` primitives.
- Focus trap — Modals trap focus within the dialog.
- Scroll lock — Background scrolling is prevented when modals are open.
- `role` attributes — Correct roles for menus, dialogs, tabs, etc.
- Keyboard navigation — Arrow keys for menus, ESC to close, Tab to move.

### Custom Components

Non-Radix components follow these patterns:

- **Screen reader text:** `[:span {:class "sr-only"} "Close"]`
- **ARIA labels:** Always accept and forward `:aria-label`
- **Role attributes:** `{:role "alert"}` for error messages, `{:role "group"}` for field sets
- **Disabled states:** `disabled:pointer-events-none disabled:opacity-50`
- **Focus visible:** `focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring`

---

## Animation Patterns

Animated components use Radix's `data-[state=*]` selectors combined with `tw-animate-css`:

```clojure
;; Entry animation
"data-[state=open]:animate-in data-[state=open]:fade-in-0 data-[state=open]:zoom-in-95"

;; Exit animation
"data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=closed]:zoom-out-95"

;; Directional slides (based on which side the popover appears)
"data-[side=bottom]:slide-in-from-top-2"
"data-[side=left]:slide-in-from-right-2"
"data-[side=right]:slide-in-from-left-2"
"data-[side=top]:slide-in-from-bottom-2"
```

This means animations are purely declarative — defined in CSS classes, triggered by data attributes that Radix manages.

---

## File Structure Convention

Each component is a single `.cljs` file:

```
ui/components/
├── button.cljs         ← single component
├── dialog.cljs         ← compound component (dialog, dialog-content, dialog-title, etc.)
├── field.cljs          ← compound component (field, field-label, field-error, etc.)
└── ...
```

Compound components (dialog, field, dropdown-menu) export multiple functions from one namespace. This keeps related pieces together while maintaining single-file simplicity.

### Namespace Docstring

Every component file starts with a comprehensive ns docstring:

```clojure
(ns your.ui.components.dialog
  "Dialog (modal) component with overlay and content area.
  https://www.radix-ui.com/primitives/docs/components/dialog

  Version: 1.0.0
  Last updated: 2026-02-06

  Based on Radix UI primitives.
  Documentation: https://www.radix-ui.com/primitives/docs/components/dialog"
  (:require ...))
```

### Function Docstrings

Every public function has a docstring documenting:
1. What the component does
2. All props with types and defaults
3. Usage examples

Private helper functions (variant resolvers, internal components) use `defn-` and have shorter docstrings.

---

## Portfolio Showcase Convention

Each component has a corresponding portfolio file:

```
portfolio/ui_components/
├── button.cljs
├── dialog.cljs
├── ...
```

Every portfolio file follows this structure:

```clojure
(ns your.portfolio.ui-components.button
  (:require
   [your.ui.components.button :as button]
   [your.portfolio.utils :as utils]
   [portfolio.reagent-18 :refer-macros [defscene configure-scenes]])
  (:require-macros [your.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Button"})

;; 1. Installation scene (always first)
(defscene installation ...)

;; 2. API reference scene
(defscene api-reference ...)

;; 3. Individual variant/usage scenes
(defscene button-default ...)
(defscene button-destructive ...)
```

The `embed-source` macro reads the component source file at compile time, so the portfolio shows the actual code — always in sync, never stale.

---

## Utility: `merge-classes`

The single most important utility function:

```clojure
(ns your.utils.styles
  (:require ["tailwind-merge" :refer [twMerge]]))

(defn merge-classes
  [& classes]
  (twMerge (clj->js (remove nil? classes))))
```

It accepts:
- Strings: `"px-4 py-2"`
- Vectors of strings: `["px-4" "py-2"]` (Tailwind classes are space-separated anyway, but vectors work)
- `nil` values (filtered out)
- Multiple arguments: `(merge-classes base variant size user-class)`

It resolves conflicts intelligently:
- `(merge-classes "px-4" "px-8")` → `"px-8"` (last wins)
- `(merge-classes "text-sm" "text-lg")` → `"text-lg"` (last wins)
- `(merge-classes "bg-red-500" "bg-primary")` → `"bg-primary"` (last wins)
- `(merge-classes "p-4" "px-2")` → `"p-4 px-2"` (no conflict, `px` overrides only horizontal)
