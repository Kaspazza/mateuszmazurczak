# UI Components Sync Note: `data_table` drift + missing `app_skeleton`, `drawer`, `tag_combobox`

## Context

We have two codebases:

- **Target (this repo):** `src/cljs/mateuszmazurczak/ui/components/*`
- **Source (reference):** `/Users/Mati/Projects/lekta/platform/bundle/bundle.flow/src/flow/ui/components/*`

Goal: identify **useful deltas** and **missing components** from flow that should be **ported into this repo**, while keeping the existing architectural rules (UI = presentation, no business logic).

This document is intentionally written so it can be pasted as a **prompt/spec** for implementing the port.

---

## 1) `data_table.cljs` — substantive differences

### Summary
Flow’s `data_table.cljs` is a superset of ours. It adds:

1) **Row drag-and-drop reordering**
2) **Expanded rows / subcomponent rendering**
3) DnD “drag handle” UI (Grip icon) and a draggable row wrapper

Our version has “classic” TanStack table functionality (sorting/filtering/pagination/selection + toolbar) and is otherwise similar, but **does not** include DnD + row expansion.

Additionally: this repo has a separate issue where some Tailwind `overflow-*` class strings are corrupted (`overmateuszmazurczak-*`). Flow has the correct `overflow-*` strings. That affects `data_table` too, but is logically separate from “feature drift”.

---

### Detailed diff bullets (flow vs here)

#### A) New imports / dependencies in flow
Flow adds dependencies used by `data_table`:

- `@dnd-kit/core` (`DndContext`, sensors, `closestCenter`, `useSensor`, `useSensors`)
- `@dnd-kit/modifiers` (`restrictToVerticalAxis`)
- `@dnd-kit/sortable` (`SortableContext`, `useSortable`, `verticalListSortingStrategy`)
- `@dnd-kit/utilities` (`CSS`)
- Additional icon: `GripVertical`
- TanStack addition: `getExpandedRowModel`

**Prompt requirement:** when porting, ensure the target repo’s NPM deps include the needed `@dnd-kit/*` packages and that they’re included in build pipeline (Shadow / npm lock).

#### B) New UI building blocks in flow
Flow defines:

- `drag-handle-cell-ui` — renders a button with drag handle icon and merges `listeners/attributes`
- `draggable-row` (a `defc`) using `useSortable`:
  - computes transform/transition styles using `CSS.Transform.toString`
  - stores listeners/attributes on the row object via `(aset row "dndListeners" ...)` etc.
  - renders normal row cells + optional “expanded subcomponent row”

**Prompt requirement:** port these two helper components as-is, but check for correctness of attaching listeners onto the row object (mutation is acceptable at UI boundary, but be deliberate).

#### C) Expanded rows support in flow
Flow can render `render-sub-component` when `(.getIsExpanded row)`.

This implies:
- table options include `getExpandedRowModel`
- column definitions likely include “expander” or some custom cell to toggle expansion (not necessarily in this file, but supported here).

**Prompt requirement:** preserve backwards compatibility:
- If consumer does not pass `render-sub-component`, no expansion UI is required.
- If they pass it, implement the expanded row rendering behavior.

#### D) Drag-and-drop behavior
Flow uses:
- `DndContext` and `SortableContext` to wrap the table body.
- Sensors (Mouse/Touch/Keyboard).
- Likely an `onDragEnd` callback to reorder data externally or via callback.

**Prompt requirement:** implement DnD in a “data-driven” way:
- The table component must not own the canonical data; it should accept:
  - `:data` current rows,
  - `:get-row-id` (or equivalent),
  - `:on-reorder` callback receiving new row order (or moved id + indices).
- The component must remain mostly a view + event emitter; orchestration stays above.

#### E) Risk: feature coupling vs simplicity
Flow’s version is significantly more complex. Port only if we truly need:
- reorderable rows and/or
- expandable rows.

Otherwise, keep our simpler `data_table` and only take bugfixes (especially the overflow class corruption).

---

### Implementation prompt (copy/paste)

> **Task:** Update `src/cljs/mateuszmazurczak/ui/components/data_table.cljs` by porting the missing capabilities from `flow/ui/components/data_table.cljs`.
>
> **Must add:**
> 1. Drag-and-drop row reordering using `@dnd-kit/core`, `@dnd-kit/sortable`, `@dnd-kit/utilities`, with vertical axis restriction.
> 2. Support for expandable rows using TanStack `getExpandedRowModel`, and render an optional `render-sub-component` row when expanded.
> 3. Add a drag handle cell UI (GripVertical) and wire it to sortable listeners/attributes.
>
> **API constraints:**
> - Keep the component data-driven: accept data + callbacks, do not own the canonical dataset.
> - Ensure existing non-DnD tables still work unchanged.
> - Preserve sorting/filtering/pagination/selection behavior.
>
> **Also fix:** any corrupted Tailwind overflow classes in this file (`overmateuszmazurczak-*` → `overflow-*`).
>
> **Deliverable:** Updated component + any necessary npm deps and compilation fixes.

---

## 2) Missing component: `app_skeleton.cljs`

### Summary
Flow has `app_skeleton.cljs` which provides an “app initialization loading screen” (sidebar skeleton + header skeleton + page content skeleton). This repo does not have it.

It’s low-risk, mostly presentation-only, and can be useful whenever:
- the app needs time to load configuration/auth/session,
- we want to avoid layout shift,
- we want to prevent rendering real pages before data is ready.

### What it contains (flow)
- Uses existing `skeleton` component.
- Builds:
  - `sidebar-skeleton`
  - `main-content-skeleton`
  - `app-loading` root layout

### Porting considerations
- Namespacing and styling tokens must match this repo (`bg-background`, `border-border`, `bg-sidebar`, etc).
- Keep it dumb/presentational.
- It should live in `ui/components/app_skeleton.cljs` (or `app-skeleton.cljs` depending on naming conventions—this repo uses underscores in filenames but kebab-case in ns segments; follow existing style in this folder).

---

### Implementation prompt (copy/paste)

> **Task:** Port `flow/ui/components/app_skeleton.cljs` into this repo as a new UI component.
>
> **Create file:** `src/cljs/mateuszmazurczak/ui/components/app_skeleton.cljs`
>
> **Expose a single public entry:** e.g. `[app-loading]` that renders the skeleton layout.
>
> **Constraints:**
> - Presentation-only (no subscriptions, no data fetching).
> - Use existing `mateuszmazurczak.ui.components.skeleton/skeleton`.
> - Keep Tailwind classes consistent with this repo’s design tokens (`bg-background`, `bg-sidebar`, `border-border`, etc.).
>
> **Goal:** allow pages/system to render `[app-loading]` during initialization.

---

## 3) Missing component: `drawer.cljs` (Vaul-based)

### Summary
Flow has a Vaul-based drawer wrapper component. This repo currently has `sheet.cljs` (Radix sheet). Drawer is similar but not identical.

Drawer can be valuable if we want:
- “mobile bottom drawer” interaction with nice gestures,
- an alternative to Radix Sheet, especially for mobile-first interactions.

### What flow’s `drawer.cljs` does
- Wraps Vaul `Drawer` primitives:
  - `Root`, `Trigger`, `Portal`, `Close`, `Overlay`, `Content`, `Title`, `Description`
- Implements:
  - `drawer` root function with props:
    - `open`, `on-open-change`, `direction` (default `:bottom`), `should-scale-background`, `modal`
  - `drawer-content` includes overlay + content; adds “handle bar” for bottom drawer
  - uses `styles/merge-classes`
- Uses React-style prop names in places (`:className`, `:onOpenChange`), not always idiomatic hiccup keys. That’s okay but should be consistent.

### Porting considerations
- Evaluate whether adding Vaul is worth it vs continuing to use Radix Sheet.
  - If we already have a `sheet` abstraction, a separate `drawer` might fragment the component system.
- If we port it, do it cleanly:
  - consistent prop naming (`:class` vs `:className`)
  - consistent merge-classes usage
- Ensure SSR/portal behavior is acceptable in this repo.

---

### Implementation prompt (copy/paste)

> **Task:** Port `flow/ui/components/drawer.cljs` into this repo as `mateuszmazurczak.ui.components.drawer`.
>
> **Create file:** `src/cljs/mateuszmazurczak/ui/components/drawer.cljs`
>
> **Add dependency:** `vaul` to NPM deps (and confirm build works).
>
> **API requirements:**
> - Provide wrapper fns: `drawer`, `drawer-trigger`, `drawer-content`, `drawer-close`, `drawer-header`, `drawer-footer`, `drawer-title`, `drawer-description`.
> - Keep default behavior: `direction :bottom`, `modal true`.
> - Use `merge-classes` for styling.
> - Prefer `:class` in public API, translating to `:className` only when required by the underlying component.
>
> **Goal:** enable using drawer in mobile UX patterns, optionally alongside existing `sheet`.

---

## 4) Missing component: `tag_combobox.cljs`

### Summary
Flow has a responsive “tag combobox”:
- desktop: popover
- mobile: sheet (drawer-like)
- supports selecting an existing tag or creating a new one when no exact match exists.

This repo does not have it. It’s potentially reusable if we have any tagging/filtering UX.

### What flow’s component does
- Internal `tag-list` uses `command` component:
  - `command-input` to filter tags (case-insensitive regex)
  - shows `command-empty` when no tags
  - lists tags with check icon indicating selection
  - if user types a non-existing tag, shows “Create `<tag>`”
- `tag-combobox` decides between sheet/popover using `use-is-mobile` hook.

### Known correctness issues to fix while porting
Flow code has suspicious state handlers like:
- `:on-open-change #(set-open not)`
- `:onOpenChange #(set-open not)`
- same pattern for `:set-open #(set-open not)`

That is likely incorrect (it sets state to the function `not`, or toggles incorrectly). Proper implementation should either:
- accept the boolean from callback and set it (`set-open is-open`), or
- toggle via `(set-open (not open?))` using current state value.

**Prompt requirement:** fix this while porting.

### Porting considerations
- This repo already has:
  - `popover`, `sheet`, `command`, `button`
- This repo does **not** appear to have the hook `use-is-mobile` (flow has `flow/ui/hooks/use_is_mobile.cljs`).
  - We must either port that hook too, or make mobile/desktop selection a prop (`:is-mobile?`) to keep UI pure and avoid “environment detection” inside components.

Given the project rules (“UI components: zero business logic, no transformations”), having a `use-is-mobile` hook *inside* a component is borderline. Prefer:
- pass `:is-mobile?` from above (application layer) **or**
- create a very small hook in `ui/hooks` and keep it strictly presentation-supporting.

---

### Implementation prompt (copy/paste)

> **Task:** Port `flow/ui/components/tag_combobox.cljs` into this repo, but fix its state handling and decide how to handle “mobile detection”.
>
> **Create file:** `src/cljs/mateuszmazurczak/ui/components/tag_combobox.cljs`
>
> **Required behavior:**
> - Provide `[tag-combobox {:tags ... :selected-tag ... :on-select ... :on-create ...}]`
> - Desktop variant uses `popover`.
> - Mobile variant uses `sheet` (or the new `drawer`, if we decide that’s the standard).
> - Inside the list:
>   - filter tags by input (case-insensitive)
>   - show selection indicator
>   - allow “Create `<input>`” when no exact match
>
> **Fixes vs flow:**
> - Correct open-state callbacks. Use either:
>   - `:on-open-change (fn [is-open] (set-open is-open))` patterns, or
>   - proper toggling with current boolean state.
> - Do not set state to `not`.
>
> **Architecture decision required:**
> - Option A (preferred for purity): accept `:is-mobile?` as a prop and render sheet/popover accordingly.
> - Option B: port `use-is-mobile` hook into this repo (if we accept it as a UI concern).
>
> **Goal:** reusable tag-selection primitive with predictable controlled/uncontrolled behavior.

---

## Recommended port order (pragmatic)

1) `app_skeleton.cljs` (easy, low risk, no deps)
2) Fix overflow class corruption across components (separate mechanical PR)
3) `tag_combobox.cljs` (moderate, but needs correctness fixes + mobile strategy)
4) `drawer.cljs` (only if we commit to Vaul; otherwise skip)
5) `data_table.cljs` feature port (largest risk/benefit; do only if needed)

---

## Acceptance criteria checklist (for the eventual PR)

- [ ] `app_skeleton` exists and renders without extra deps.
- [ ] `tag_combobox` works on desktop and mobile path, open state behaves correctly.
- [ ] If `drawer` is added, `vaul` is included and bundle compiles.
- [ ] If `data_table` is updated, existing usages still render and the new features are behind optional props (no forced complexity).
- [ ] `data_table` DnD reordering is externally controlled via callback (no hidden internal mutation of canonical data).
- [ ] No remaining `overmateuszmazurczak-*` classes in affected components (or at least fixed in the ported ones).
