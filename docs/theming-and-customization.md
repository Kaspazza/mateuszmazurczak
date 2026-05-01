# Theming & Customization

This document describes the global systems that make the component library work — the design token pipeline, dark mode, class merging, and how to customize everything to match your design system.

## Architecture Overview

```
CSS Custom Properties (main.css)
        ↓
Tailwind v4 @theme inline
        ↓
Utility classes (bg-primary, rounded-lg, etc.)
        ↓
Component code (merge-classes for overrides)
        ↓
Your UI
```

Every customization flows through this pipeline. There is **one place** to define your brand colors, radii, shadows, and fonts — the CSS file. Everything downstream reacts automatically.

---

## 1. CSS Custom Properties (Design Tokens)

All design tokens live in `resources/css/main.css` as CSS custom properties on `:root` (light mode) and `.dark` (dark mode).

### Color Tokens

The color system uses **semantic naming**, not raw color values. Components reference roles, not specific colors:

| Token | Purpose | Used by |
|-------|---------|---------|
| `--background` | Page background | `body`, cards, inputs |
| `--foreground` | Primary text | Default text color |
| `--primary` | Brand / accent color | Buttons, links, active states |
| `--primary-foreground` | Text on primary | Button labels on primary bg |
| `--secondary` | Secondary actions | Secondary buttons, badges |
| `--secondary-foreground` | Text on secondary | Labels on secondary bg |
| `--muted` | Subdued backgrounds | Disabled states, code blocks |
| `--muted-foreground` | Subdued text | Descriptions, placeholders |
| `--accent` | Hover / focus backgrounds | Menu highlights, hover states |
| `--accent-foreground` | Text on accent | Menu item text on hover |
| `--destructive` | Error / danger | Delete buttons, error messages |
| `--destructive-foreground` | Text on destructive | Labels on destructive bg |
| `--border` | Border color | Input borders, separators, cards |
| `--input` | Input borders/backgrounds | Form input styling |
| `--ring` | Focus ring | Focus-visible outlines |
| `--popover` | Popover/dropdown backgrounds | Tooltips, selects, dropdowns |
| `--popover-foreground` | Text in popovers | Dropdown item text |
| `--card` | Card backgrounds | Card components |
| `--card-foreground` | Text in cards | Card content text |
| `--chart-1` through `--chart-5` | Chart colors | Data visualization |
| `--sidebar` | Sidebar background | Sidebar component |
| `--sidebar-*` | Sidebar variants | Various sidebar states |

All colors use the **oklch** color space for perceptual uniformity. This means adjusting lightness, chroma, or hue produces predictable visual results.

### Changing Your Brand Colors

To change the primary color from purple to blue, edit only these variables:

```css
:root {
  --primary: oklch(0.6270 0.2650 260.0000); /* was 303.9 (purple), now 260 (blue) */
  --primary-foreground: oklch(0.9889 0.0053 17.2475);
}

.dark {
  --primary: oklch(0.5136 0.2031 262.0000); /* adjust dark variant too */
  --primary-foreground: oklch(0.9851 0 0);
}
```

Every button, badge, link, focus ring, and active state in every component updates automatically. **Zero component code changes.**

### Non-Color Tokens

| Token | Purpose | Default |
|-------|---------|---------|
| `--radius` | Base border radius | `0.625rem` |
| `--font-sans` | Sans-serif font stack | System fonts |
| `--font-serif` | Serif font stack | System fonts |
| `--font-mono` | Monospace font stack | System fonts |
| `--shadow-*` | Shadow scale (2xs → 2xl) | Subtle elevation shadows |
| `--spacing` | Base spacing unit | `0.25rem` (used by Tailwind's spacing scale) |
| `--tracking-normal` | Default letter spacing | `0em` |

### Radius System

The radius uses a **derived scale** — one base value, computed variants:

```css
@theme inline {
  --radius-sm: calc(var(--radius) - 4px);
  --radius-md: calc(var(--radius) - 2px);
  --radius-lg: var(--radius);
  --radius-xl: calc(var(--radius) + 4px);
}
```

Change `--radius: 0.625rem` to `--radius: 0` for sharp corners, or `--radius: 1rem` for very rounded. Every component adjusts proportionally.

---

## 2. Tailwind v4 Theme Integration

The `@theme inline` block in `main.css` bridges CSS variables to Tailwind's utility class system:

```css
@theme inline {
  --color-primary: var(--primary);
  --color-primary-foreground: var(--primary-foreground);
  /* ... all semantic colors ... */

  --font-sans: var(--font-sans);
  --font-mono: var(--font-mono);

  --radius-sm: calc(var(--radius) - 4px);
  --radius-lg: var(--radius);
  /* ... */

  --shadow-sm: var(--shadow-sm);
  --shadow-md: var(--shadow-md);
  /* ... */
}
```

This means writing `bg-primary` in a component generates CSS that resolves to your `--primary` variable at runtime. The connection is:

```
Tailwind class: bg-primary
    → CSS: background-color: var(--color-primary)
        → resolves to: var(--primary)
            → resolves to: oklch(0.6270 0.2650 303.9000)
```

### Tailwind v4 Specifics

This library uses **Tailwind CSS v4**, which has notable differences from v3:

- **CSS-first configuration** — No `tailwind.config.js`. Everything is configured in CSS via `@theme inline`.
- **`@import "tailwindcss"`** — Single import replaces the old `@tailwind base/components/utilities` directives.
- **`@custom-variant`** — Dark mode is configured as `@custom-variant dark (&:is(.dark *))`, enabling class-based dark mode toggling.
- **`@plugin`** — Plugins like `@tailwindcss/typography` are loaded via `@plugin` directive.
- **Native CSS cascade layers** — Base styles use `@layer base {}`.

### tw-animate-css

The library imports [`tw-animate-css`](https://github.com/Wombosvideo/tw-animate-css) for animations:

```css
@import "tw-animate-css";
```

This provides animation utilities used by components for enter/exit transitions:

- `animate-in`, `animate-out` — Base animation triggers
- `fade-in-0`, `fade-out-0` — Opacity transitions
- `zoom-in-95`, `zoom-out-95` — Scale transitions
- `slide-in-from-top-2`, etc. — Directional slide transitions

These are used extensively in dialog overlays, dropdown menus, tooltips, and popovers via Radix's `data-[state=open/closed]` selectors.

---

## 3. Dark Mode

Dark mode is implemented via class-based toggling:

1. The `.dark` class is toggled on the `<html>` or `<body>` element.
2. CSS variables switch to their dark variants.
3. All components automatically adapt — no per-component dark mode logic.

```css
/* Light (default) */
:root {
  --background: oklch(0.9700 0.008 90);
  --foreground: oklch(0.1448 0 0);
}

/* Dark */
.dark {
  --background: oklch(0.205 0 0);
  --foreground: oklch(0.9851 0 0);
}
```

Some components add dark-specific refinements using Tailwind's `dark:` variant:

```clojure
;; Example from switch component
"dark:data-[state=unchecked]:bg-input/80"
"dark:data-[state=checked]:bg-primary-foreground"
```

The custom variant configuration `@custom-variant dark (&:is(.dark *))` means any element inside a `.dark` ancestor will match `dark:` prefixed utilities.

---

## 4. Class Merging with `tailwind-merge`

The `merge-classes` utility is the glue that makes component customization work:

```clojure
(ns your.utils.styles
  (:require ["tailwind-merge" :refer [twMerge]]))

(defn merge-classes
  [& classes]
  (twMerge (clj->js (remove nil? classes))))
```

### Why This Matters

Every component accepts a `:class` prop. This prop is merged with the component's default classes using `tailwind-merge`, which understands Tailwind's class semantics:

```clojure
;; Button default: "h-10 px-4 py-2"
;; You pass:       "px-8"
;; Result:         "h-10 py-2 px-8"  ← px-4 is overridden, not duplicated
```

Without `tailwind-merge`, you'd get `h-10 px-4 py-2 px-8` — both padding classes applied, with unpredictable results.

### The Override Pattern

Every component follows this pattern:

```clojure
(defn my-component [{:keys [class] :as props} & children]
  (let [base-classes "..." ;; component defaults
        combined (merge-classes base-classes class)]
    [:div {:class combined} ...]))
```

This means **any Tailwind class on any component can be overridden** by the consumer:

```clojure
;; Override border radius
[button {:class "rounded-full"} "Round"]

;; Override colors
[badge {:variant :default :class "bg-green-500 text-white"} "Custom"]

;; Override size
[input {:class "h-14 text-lg"}]
```

### Merging Multiple Sources

`merge-classes` accepts strings, vectors of strings, and `nil` values:

```clojure
(merge-classes
  "base-class"
  (variant-classes variant)   ;; returns a string
  (when large? "text-lg")     ;; may return nil
  class)                      ;; user override, may be nil
```

---

## 5. Base Layer Styles

The `@layer base` block sets global defaults:

```css
@layer base {
  * {
    @apply border-border outline-ring/50;
  }
  body {
    @apply bg-background text-foreground;
  }
  button:not(:disabled),
  [role="button"]:not(:disabled) {
    cursor: pointer;
  }
}
```

This ensures:

- **All borders** use the `--border` token color by default.
- **All outlines** (focus rings) use `--ring` at 50% opacity.
- **Body** uses the semantic background and foreground colors.
- **Buttons** show a pointer cursor (except when disabled).

You don't need to add `border-border` to every component — it's the global default.

---

## 6. Component-Level Customization Patterns

### Variant Pattern

Components use keyword-driven variants resolved via `case`:

```clojure
(defn- variant-classes [variant]
  (case variant
    :default     "bg-primary text-primary-foreground hover:bg-primary/90"
    :destructive "bg-destructive text-destructive-foreground hover:bg-destructive/90"
    :outline     "border border-input bg-background hover:bg-accent"
    :secondary   "bg-secondary text-secondary-foreground hover:bg-secondary/80"
    :ghost       "hover:bg-accent hover:text-accent-foreground"
    :link        "text-primary underline-offset-4 hover:underline"
    ;; fallback
    "bg-primary text-primary-foreground hover:bg-primary/90"))
```

This is Clojure's answer to `cva` (class-variance-authority) from the JS ecosystem. No library needed — `case` on a keyword is simple, fast, and transparent.

### Adding a New Variant

To add a `:success` variant to the button:

1. Open `button.cljs` in your project.
2. Add a case:
   ```clojure
   :success "bg-green-600 text-white hover:bg-green-700"
   ```
3. Done. Use it: `[button {:variant :success} "Save"]`

No PR to a library. No version bump. No waiting.

### The `data-slot` Convention

Every component sets a `data-slot` attribute:

```clojure
(assoc props :data-slot "button")
(assoc props :data-slot "dialog-content")
(assoc props :data-slot "field-label")
```

This serves two purposes:

1. **CSS targeting** — Parent components can style children based on slot: `*:data-[slot=select-value]:line-clamp-1`
2. **Debugging** — Inspect the DOM and immediately see which component rendered each element.

### Prop Forwarding

Components forward unknown props to the underlying element:

```clojure
(-> props
    (assoc :data-slot "button" :class combined-classes)
    (dissoc :variant :size :as-child :class-name))
```

Known component-specific keys (`:variant`, `:size`, `:as-child`) are consumed and removed. Everything else (`:on-click`, `:disabled`, `:aria-label`, `:data-testid`, etc.) passes through to the DOM element.

---

## 7. NPM Dependencies

The styling and behaviour layer depends on these npm packages:

| Package | Purpose | Used by |
|---------|---------|--------|
| `tailwindcss` (v4) | CSS utility framework | All components |
| `@tailwindcss/cli` | Build tool | CSS compilation |
| `@tailwindcss/typography` | Prose styling plugin | Markdown, articles |
| `tw-animate-css` | Animation utilities | Dialogs, dropdowns, tooltips |
| `tailwind-merge` | Intelligent class merging | `merge-classes` utility |
| `@radix-ui/react-*` | Accessible primitives | Dialog, Select, Dropdown, Tooltip, etc. |
| `lucide-react` | Icon library | Icons in buttons, menus, etc. |
| `sonner` | Toast notifications | Notification component |
| `embla-carousel-react` | Carousel behaviour | Carousel component |
| `@tanstack/react-table` | Table state management | Data table component |
| `@dnd-kit/*` | Drag and drop | Data table row reordering |
| `cmdk` | Command palette | Command component |
| `vaul` | Drawer behaviour | Drawer component |
| `shiki` | Syntax highlighting | Code block component |

Each component documents its own dependencies. When you copy a component, you install only what that component needs.

---

## 8. Customization Recipes

### Change the Entire Color Palette

Edit `:root` and `.dark` in `main.css`. All components update.

### Change Border Radius Globally

```css
:root {
  --radius: 0;        /* Sharp corners */
  --radius: 0.25rem;  /* Subtle rounding */
  --radius: 1rem;     /* Very rounded */
}
```

### Add a Font

```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

:root {
  --font-sans: 'Inter', ui-sans-serif, system-ui, /* ... rest of stack */;
}
```

### Override a Component's Default Style

```clojure
;; Make all destructive buttons have a white border
[button {:variant :destructive :class "border border-white"} "Delete"]
```

### Create a Component Variant at Usage Site

```clojure
;; A "success" badge without modifying the badge component
[badge {:class "border-transparent bg-green-600 text-white"} "Active"]
```

### Extend the Shadow Scale

```css
:root {
  --shadow-glow: 0 0 15px 5px oklch(0.627 0.265 304 / 0.3);
}

@theme inline {
  --shadow-glow: var(--shadow-glow);
}
```

Then use: `[button {:class "shadow-glow"} "Glowing"]`
