# Portfolio Components — Improvement Instruction

## Context

We have a portfolio system (like Storybook) built with [portfolio](https://github.com/cjohansen/portfolio) that showcases our UI components. An LLM generated 43 portfolio files in `src/cljs/mateuszmazurczak/portfolio/ui_components/`. The quality is insufficient — every file has **only 1 defscene** with minimal examples. They need to be reworked to match the depth of our already-committed reference files and the shadcn documentation style.

---

## Your Primary Tool: shadcn MCP Server

You have access to a **shadcn MCP server** with these tools. **You must use them for every shadcn-derived component**:

1. **`search_items_in_registries`** — Search with `["@shadcn"]` registry and the component name (e.g., query `"button"`). This returns the full list of `registry:example` entries that shadcn provides for that component. **This list is your scene blueprint.**

2. **`get_item_examples_from_registries`** — Fetch the actual code for each example (e.g., query `"button-loading"`). Use this to understand **what each example demonstrates** and translate it to our ClojureScript/Reagent equivalent.

3. **`view_items_in_registries`** — View component metadata and dependencies (e.g., `["@shadcn/button"]`).

### Workflow Per Component

```
1. Search: search_items_in_registries(["@shadcn"], "<component-name>")
   → Get list of all examples (e.g., button-demo, button-loading, button-icon, ...)
   
2. Fetch: get_item_examples_from_registries(["@shadcn"], "<example-name>")
   → Get the actual TSX code for each example
   
3. Read: Read the source component at ui/components/<name>.cljs
   → Understand our ClojureScript wrapper's props, variants, differences from shadcn
   
4. Write: Create defscene entries that cover:
   a) Every shadcn example (translated to CLJS)
   b) Any additional props/variants our wrapper adds beyond shadcn
   c) Composition examples from shadcn (e.g., command-dialog, drawer-dialog)
```

---

## Reference Standard

Look at these 4 already-committed files as your quality benchmark:

- `portfolio/ui_components/data_table.cljs` — **7 scenes**: basic, with toolbar, expandable rows, drag-and-drop, combined features, empty state. Rich helper fns, realistic data.
- `portfolio/ui_components/drawer.cljs` — **4 scenes**: bottom drawer, right drawer, scrollable content, no-scale option.
- `portfolio/ui_components/tag_combobox.cljs` — **5 scenes**: basic, custom placeholder, custom width, multiple instances, creation workflow with log.
- `portfolio/ui_components/app_skeleton.cljs` — **3 scenes**: full app, sidebar only, main content only.

**Pattern to follow**: Each scene = one focused concept. Rich docstrings. Realistic data. Interactive state management with `r/atom`.

---

## Rules

### 1. Scene Count: Minimum 3, Target 5–8 for Complex Components

Every file must have **at minimum 3 defscene entries**. Complex components (button, dialog, sheet, sidebar, stepper, loader, select, dropdown-menu, carousel, command, table) should have 5–8+.

### 2. shadcn Examples → defscene Mapping

For every `registry:example` entry returned by the shadcn MCP, create a corresponding `defscene`. You don't need to cover `registry:block` entries (those are full page layouts) or `registry:internal` entries, but all `registry:example` entries should be represented.

Ignore form-framework examples (`form-rhf-*`, `form-tanstack-*`) as we don't use those libraries. Also ignore examples for components we don't have (e.g., `button-group` — we don't have that component).

### 3. Component Source Props Must Be Fully Covered

After covering shadcn examples, check the source component (`ui/components/<name>.cljs`) for any props/variants **our wrapper adds** that shadcn doesn't have. Create additional scenes for those. Examples:
- Our `stepper` has `:vertical`, `:circle`, `:reverse-progress?` variants — none of which exist in shadcn
- Our `loader` has 12 variants — shadcn's spinner is much simpler
- Our `header`, `footer`, `navigation` are custom components not from shadcn at all

### 4. Rich Docstrings with Attribution

Every `defscene` docstring must follow this structure:

```clojure
(defscene
 button-variants
 "All button style variants displayed side by side.

  Based on shadcn/ui Button — https://ui.shadcn.com/docs/components/button
  Radix primitive: @radix-ui/react-slot (for :as-child polymorphism)
  
  Our CLJS wrapper uses keyword props (:variant, :size) instead of 
  string className variants. Supports the same variants as shadcn:
  :default, :destructive, :outline, :secondary, :ghost, :link
  
  Note: We also support :xs size which is not in shadcn's default button."
 []
 ...)
```

For custom (non-shadcn) components, note that explicitly:

```clojure
(defscene
 loader-all-variants
 "All 12 loader animation variants.

  Custom component — not from shadcn/ui.
  Built with pure CSS animations and Tailwind classes.
  
  Available variants: :circular, :classic, :pulse, :pulse-dot, :dots,
  :typing, :wave, :bars, :terminal, :text-blink, :text-shimmer, :loading-dots
  
  Each variant supports three sizes: :sm, :md, :lg"
 []
 ...)
```

### 5. Consistent Invocation Style

Follow the conventions established in the committed portfolio files:

- **Button** is called as a **function** (returns hiccup): `(button/button {:variant :outline} "text")` — NOT `[button/button {} "text"]`
- **All other components** use Reagent component syntax: `[sut/dialog {...} ...]`
- For components that are raw React `def` bindings (like `sheet`, `sheet-trigger`), use `[:> sut/sheet {...} ...]`
- For components that are `defn` Reagent wrappers (like `sheet-content`, `dialog`), use `[sut/sheet-content {...} ...]`
- Check the source: if it's `(def name (.-Something Lib))` → use `[:>`, if it's `(defn name ...)` → use `[sut/name ...]`
- Use kebab-case for all props — Reagent converts them appropriately

### 6. Scene Design Principles

- **One scene = one concept**. Don't stuff all variants into a single scene.
- **Show states side by side** when demonstrating variants (e.g., all badge variants in a row)
- **Interactive scenes** with `r/atom` for stateful components (toggles, dialogs, sheets, etc.)
- **Realistic data** — not "Item 1, Item 2" but "Dashboard, Projects, Settings"
- **Composition scenes** — at least one scene per file showing the component used with others (e.g., input+label, badge inside table, command inside dialog)

### 7. Do NOT Touch These Files

- `portfolio/ui_components/app_skeleton.cljs` — already committed, good quality
- `portfolio/ui_components/data_table.cljs` — already committed, good quality
- `portfolio/ui_components/drawer.cljs` — already committed, good quality
- `portfolio/ui_components/tag_combobox.cljs` — already committed, good quality
- `shadow-cljs.edn` — revert any changes (the flow-storm comment-out is unrelated)

---

## Component-by-Component Requirements

### shadcn-derived Components (use MCP to get examples)

For each component below, the shadcn MCP search results show the examples that MUST be covered as scenes. Additional scenes from source-component props are noted.

| Component | shadcn examples to cover | Additional from our source |
|---|---|---|
| **button** | `button-default`, `button-destructive`, `button-outline`, `button-ghost`, `button-link`, `button-icon`, `button-with-icon`, `button-loading`, `button-as-child`, `button-size`, `button-rounded` | `:xs` size |
| **badge** | `badge-demo`, `badge-outline`, `badge-secondary`, `badge-destructive`, `spinner-badge` | `:as-child` |
| **input** | `input-demo`, `input-file`, `input-disabled`, `input-with-label`, `input-with-button`, `input-with-text` | controlled with `r/atom` |
| **textarea** | `textarea-demo`, `textarea-disabled`, `textarea-with-label`, `textarea-with-button`, `textarea-with-text` | — |
| **checkbox** | `checkbox-demo`, `checkbox-disabled`, `checkbox-with-text`, `field-checkbox` | indeterminate state |
| **switch** | `switch-demo`, `field-switch` | disabled state |
| **select** | `select-demo`, `select-scrollable`, `field-select` | disabled items |
| **radio-group** | `radio-group-demo`, `field-radio` | disabled, horizontal layout |
| **label** | `label-demo`, `input-with-label`, `textarea-with-label` | — |
| **dialog** | `dialog-demo`, `dialog-close-button`, `command-dialog`, `drawer-dialog` | — |
| **sheet** | `sheet-demo`, `sheet-side` (all 4 sides) | scrollable content |
| **dropdown-menu** | `dropdown-menu-demo`, `dropdown-menu-checkboxes`, `dropdown-menu-radio-group`, `dropdown-menu-dialog` | — |
| **popover** | `popover-demo`, `combobox-popover` | — |
| **command** | `command-demo`, `command-dialog` | — |
| **collapsible** | `collapsible-demo` | multiple items, nested |
| **tooltip** | `tooltip-demo`, `kbd-tooltip` | custom content |
| **carousel** | `carousel-demo`, `carousel-size`, `carousel-orientation`, `carousel-spacing`, `carousel-api` | — |
| **table** | `table-demo`, `typography-table` | with badges, with actions |
| **separator** | `separator-demo`, `breadcrumb-separator` | vertical, with custom class |
| **breadcrumb** | `breadcrumb-demo`, `breadcrumb-ellipsis`, `breadcrumb-dropdown`, `breadcrumb-separator`, `breadcrumb-responsive`, `breadcrumb-link` | — |
| **skeleton** | `skeleton-demo`, `skeleton-card` | — |
| **avatar** | `avatar-demo`, `empty-avatar`, `empty-avatar-group` | custom sizes |
| **sidebar** | `sidebar-demo`, `sidebar-menu`, `sidebar-menu-sub`, `sidebar-group`, `sidebar-menu-collapsible`, `sidebar-controlled` | collapsible, mobile |
| **spinner** | `spinner-demo`, `spinner-size`, `spinner-button`, `spinner-badge`, `spinner-basic`, `spinner-color` | — |
| **empty** | `empty-demo`, `empty-icon`, `empty-outline`, `empty-avatar`, `empty-background` | — |

### Custom Components (NOT in shadcn — derive scenes from source props)

| Component | Required scenes (from source API analysis) |
|---|---|
| **stepper** | horizontal basic, vertical variant, circle variant, reverse-progress, disabled steps, label-orientation vertical, custom icons, with form content |
| **loader** | all 12 variants showcase, sizes comparison (:sm/:md/:lg), text-based variants with custom text, unified `loader` component with `:variant` prop |
| **message** | basic message, message with avatar + fallback, message with markdown, message with actions, user vs assistant styling |
| **chat-container** | basic with scroll, with many messages (auto-scroll), with scroll anchor |
| **prompt-input** | basic input, with speech recognition, with multiple actions, disabled/loading state |
| **speech-recognition-button** | standalone, inside prompt-input, with transcript display |
| **code-block** | single file, with copy button, multiple languages, with filename header |
| **markdown** | headings, bold/italic, code blocks, links, lists, combined |
| **system-message** | all variants (default, warning, error), with CTA, without CTA |
| **notification** | basic toast, with description, with action, different durations |
| **navigation** | forward navigation, back navigation, dark mode variant |
| **header** | basic, with logo, with right section, sticky, different sizes, with nav items |
| **footer** | basic (may be single scene if component is simple) |
| **admin** | admin badge, delete button, combined admin panel |
| **image** | optimized-img, avatar-img, different sizes, loading/error states |
| **scroll-button** | inside chat container, standalone, with custom styling |
| **theme-toggle** | basic toggle, in header context |
| **field** | field with input, field with textarea, field with select, field with error, fieldset with multiple fields |

---

## Checklist Before Submitting

- [ ] Every file has **minimum 3 defscene entries**
- [ ] Every shadcn `registry:example` for the component has a corresponding scene
- [ ] Source component props are fully demonstrated (read `ui/components/<name>.cljs`)
- [ ] Docstrings include shadcn URL or "Custom component" note
- [ ] Docstrings explain **what**, **when/why**, and **what to notice**
- [ ] Button uses function-call style `(sut/button {...} ...)` everywhere
- [ ] Raw React `def` components use `[:> sut/name ...]`
- [ ] Reagent `defn` components use `[sut/name ...]`
- [ ] All props use kebab-case
- [ ] Stateful components use `r/atom` with Form-2 components `(fn [] ...)`
- [ ] At least one composition scene per file (component used with others)
- [ ] Realistic data in examples (not "Item 1, Item 2")
- [ ] Run `bb format` on all changed files
- [ ] Run portfolio (`bb dev-launch`, open `:9630`) and verify all scenes render
- [ ] `shadow-cljs.edn` changes reverted (or committed separately if intentional)
- [ ] `portfolio.cljs` requires are complete and sorted
