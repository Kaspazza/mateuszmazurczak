# Portfolio API Reference — Implementation Guide

Audit of all 44 portfolio component files (excluding `carousel`, `checkbox`, `code-block` which are already fixed).

## Grading Legend

- **A** — Props complete, good usage example, good scenes
- **B** — Props mostly complete, minor gaps
- **C** — Significant prop gaps, empty usage example
- **D** — Placeholder/skeleton API reference, phantom entries
- **F** — Phantom components, zero real documentation

---

## Priority Tiers

### 🔴 Tier 1 — Broken / Phantom (Grade D–F) — Fix First

These have phantom components, placeholder entries, or completely missing main components.

| Component | Grade | Key Issues |
|-----------|-------|------------|
| **markdown** | F | Phantom `"hello"` component. Actual `markdown` component undocumented. Zero props. |
| **tag-combobox** | D | Phantom `"component"` placeholder. `tag-combobox` with 6 props undocumented. |
| **scroll-button** | D | Phantom `"component"` placeholder. `scroll-button` with 4 props undocumented. Context dependency on `chat-container-root` not noted. |
| **speech-recognition-button** | D | Phantom `"component"` placeholder. 4 props undocumented. Context dependency on `prompt-input` not noted. |
| **prompt-input** | D+ | 3 of 4 public components missing (`prompt-input`, `prompt-input-textarea`, `prompt-input-action`). Only `prompt-input-actions` documented (with just `:class`). |
| **command** | D | 15+ props missing across 9 sub-components. Only `:class` on most. `:onSelect`, `:heading`, `:value`, `:onValueChange` all missing. |
| **collapsible** | D+ | 7 props missing across 3 components (`:open`, `:default-open`, `:on-open-change`, `:disabled`, `:as-child`, `:force-mount`). |

#### Actions for Tier 1:
1. Remove all phantom entries (`"hello"`, `"component"`, `"Component"`)
2. Add real component cards with all props from source
3. Document context dependencies in Important Notes callout
4. Replace empty usage examples

---

### 🟠 Tier 2 — Severely Incomplete (Grade C–C-) — Fix Next

These exist and document the right components, but are missing most behavioral props.

| Component | Grade | Key Issues |
|-----------|-------|------------|
| **data-table** | C- | Main `data-table` component (10 props including `:columns`, `:data`, `:toolbar-config`, `:dnd-config`) completely missing from API. Only sub-UI components documented. |
| **radio-group** | C- | 7 behavioral props missing on `radio-group` (`:value`, `:default-value`, `:on-value-change`, `:disabled`, `:required`, `:name`, `:orientation`). 3 missing on `radio-group-item`. |
| **switch** | C- | 7 of 8 props missing (`:checked`, `:default-checked`, `:on-checked-change`, `:disabled`, `:required`, `:name`, `:value`). |
| **dropdown-menu** | C- | 20+ props missing across 16 sub-components. Key gaps: `:on-select`, `:checked`, `:on-checked-change`, `:value`, `:on-value-change`, `:disabled`, `:as-child`. |
| **dialog** | C- | 4 root props missing (`:open`, `:defaultOpen`, `:onOpenChange`, `:modal`). `:as-child` missing on trigger/close. |
| **sheet** | C- | 3 critical components missing entirely (`sheet`, `sheet-trigger`, `sheet-close`). Usage example shows `[sheet-overlay {}]` — wrong component. |
| **notification** | C- | Only 1 of 10 functions has props documented (`show-toast`). `show-success/error/info/warning/loading/promise/dismiss/custom` all have empty prop lists. `toaster` Sonner props undocumented. |
| **stepper** | C | 3 of 7 public components undocumented (`stepper-navigation`, `stepper-step`, `stepper-panel`). |
| **header** | C | `header-comp`'s variadic `& menu-items` arg (maps of `{:title :href}`) completely undocumented. `toggle-header-border` miscategorized as component (it's a scroll handler). |
| **button** | C+ | Missing `:disabled`, `:type`, `:on-click`, prop forwarding note. |
| **theme-toggle** | C- | No props is correct, but should document internal behavior (reads `:theme/current`, dispatches `[:theme/toggle]`). **Bogus npm install**: `npm install lucide-react relative sr-only`. |
| **popover** | C | 3 of 4 component cards have empty `props []`. Missing `:open`, `:defaultOpen`, `:onOpenChange`, `:modal`, `:asChild`, `:side`. |

#### Actions for Tier 2:
1. Add all missing props from source docstrings
2. For `data-table`: add the main `data-table` component card with all 10 props
3. For `sheet`: add `sheet`, `sheet-trigger`, `sheet-close` cards; fix usage example
4. For `notification`: document all function signatures (positional args, not just keyword props)
5. Fix bogus npm install commands (`theme-toggle`, `loader`, `footer`)
6. Replace empty usage examples

---

### 🟡 Tier 3 — Mostly Good, Polish Needed (Grade B–B+)

| Component | Grade | Key Issues |
|-----------|-------|------------|
| **sidebar** | B+ | Props are complete. Missing: real usage example, variant scenes (floating, inset, right-side), skeleton loading scene. |
| **drawer** | B | Props fully documented. Missing: real usage example, left/top direction scenes, non-modal scene. |
| **badge** | B | Props fully documented. Missing: real usage example (only single-variant one-liner). |
| **avatar** | B+ | Props mostly complete. Missing: prop forwarding note, fallback-only scene. |
| **input** | B- | Only 2 of 10+ props documented (`:class`, `:type`). Missing: `:value`, `:default-value`, `:placeholder`, `:disabled`, `:required`, `:on-change`, `:on-blur`, `:on-focus`, prop forwarding note. Missing: invalid state scene. |
| **breadcrumb** | B- | Missing: `:separator` prop on root, accessibility behavior on `breadcrumb-page` (ARIA attrs), children override note on `breadcrumb-separator`. Usage example is empty. |
| **message** | B+ | Props mostly complete. Missing: prop forwarding notes. Usage example empty. Best demo scenes. |
| **table** | B- | Props are `:class` only (correct), but missing: prop forwarding notes, implicit behavior (overflow container, hover states, selected row state, sticky footer). Usage example empty. |
| **textarea** | B- | **Wrong default** for `:auto-size?` — documented as `false`, actually `true`. 10 props missing. Invalid state scene missing. |
| **field** | B- | `:html-for` missing on `field-label`. Children behavior missing on `field-separator`. Prop forwarding note missing on all. Usage example is `[field-set {}]`. |
| **empty** | B- | Props correct but no forwarding notes. Missing: implicit behavior docs (border-dashed, `<a>` styling, SVG sizing). |
| **skeleton** | B | Missing: accessibility props (`:role`, `:aria-label`, `:aria-live`), prop forwarding. |
| **spinner** | B | Missing: implicit `role="status"`, `aria-label="Loading"`, default `size-4`. |
| **tooltip** | B+ | 17 of 18 props documented. Missing: `tooltip-provider-component`. Best prop coverage. |
| **image** | A- | All props match perfectly. Best API reference. Missing: real usage example, error state scene. |

#### Actions for Tier 3:
1. Replace empty usage examples with realistic compositions
2. Add prop forwarding notes
3. Fix `textarea` `:auto-size?` default (false → true)
4. Add missing demo scenes (invalid states, edge cases)
5. Add Important Notes callouts
6. Add external doc links (Radix, etc.)

---

### 🟢 Tier 4 — Application-Specific Components (Lower Priority)

| Component | Grade | Key Issues |
|-----------|-------|------------|
| **app-skeleton** | C | 8 internal building blocks exposed as public API. Only `app-loading` should be documented. Usage example shows internal component. |
| **admin** | B | Props documented, but `:text` map expected keys not listed. Usage example empty. |
| **chat-container** | B | Props mostly accurate. Missing: prop forwarding, implicit `role="log"`, implicit `aria-hidden="true"`. |
| **footer** | C | Zero-arity component. **Bogus npm install**: `npm install footer`. Usage example wrongly passes `{}`. Should note component is not configurable. |
| **navigation** | B- | `:href` incorrectly marked as required (it's either/or with `:on-click`). |
| **system-message** | B | Good prop coverage. Missing: children docs, prop forwarding. No `:fill true` or custom `:icon` scenes despite being documented props. |
| **loader** | B | Props documented well. **Bogus npm install**: `npm install 10px 6px 8px`. |

---

## Systemic Issues (Apply to ALL Components)

### 1. Empty Usage Examples — Every Component
**All 44 components** have `[component {}]` or similar empty shells. Replace every single one.

### 2. Prop Forwarding — ~35 Components
Nearly every component forwards rest-props via `(-> props (assoc ...) (dissoc ...))`. None document: *"All additional props are forwarded to the underlying DOM element."*

### 3. No Important Notes Callout — All Components
Zero components have the amber `⚠️ Important Notes` box. Add where applicable.

### 4. No External Doc Links — All Radix/Third-Party Components
Components wrapping Radix, Embla, Shiki, Sonner, cmdk, vaul, etc. should link to upstream docs.

### 5. Generic Descriptions — ~30 Components
Cards say `"X component"` instead of explaining what it renders, implicit behavior, and sibling relationships.

### 6. Bogus npm Install Commands — 3 Components
- `theme-toggle`: `npm install lucide-react relative sr-only` (relative, sr-only are Tailwind classes)
- `loader`: `npm install 10px 6px 8px` (CSS values, not packages)
- `footer`: `npm install footer` (no such package)

### 7. Missing Invalid/Error State Scenes — Form Components
Components with `aria-invalid` CSS styling but no scene: `input`, `textarea`, `radio-group`, `switch`, `select`.

---

## Recommended Execution Order

1. **Fix bogus npm installs** (5 min) — `theme-toggle`, `loader`, `footer`
2. **Remove phantom entries** (15 min) — `markdown`, `tag-combobox`, `scroll-button`, `speech-recognition-button`
3. **Tier 1 full rewrites** (2–3 hours) — `markdown`, `tag-combobox`, `scroll-button`, `speech-recognition-button`, `prompt-input`, `command`, `collapsible`
4. **Tier 2 prop additions** (3–4 hours) — `data-table`, `radio-group`, `switch`, `dropdown-menu`, `dialog`, `sheet`, `notification`, `stepper`, `header`, `button`, `popover`
5. **Tier 3 polish** (2–3 hours) — All remaining: add prop forwarding notes, fix defaults, add Important Notes, replace usage examples
6. **Missing demo scenes** (2–3 hours) — Invalid states for form components, edge cases, variant showcases
7. **Tier 4 app-specific** (1 hour) — `app-skeleton`, `admin`, `chat-container`, `footer`, `navigation`, `system-message`, `loader`

**Estimated total: 10–15 hours of focused work.**
