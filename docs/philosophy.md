# Philosophy & Approach

## Why This Library Exists

Building UIs in ClojureScript has always had a friction point: the ecosystem is small, and most mature component libraries live in the JavaScript/React world. Developers are forced to choose between:

1. **Using JS component libraries directly** — Fighting interop, losing Clojure idioms, dealing with string-based APIs and mutable patterns that feel foreign.
2. **Building everything from scratch** — Reinventing accessibility, keyboard navigation, focus management, and animation. Months of work before shipping a single feature.
3. **Using opinionated Clojure UI libraries** — Getting locked into someone else's abstractions, unable to change styling or behavior when your app inevitably outgrows the defaults.

This library takes a fourth path, inspired by [shadcn/ui](https://ui.shadcn.com/) but built for and by the Clojure community.

## The Core Idea

We combine three layers, each doing what it does best:

### 1. Unstyled, Accessible Primitives (Radix UI)

[Radix UI](https://www.radix-ui.com/) provides the **behaviour layer** — unstyled, fully accessible UI primitives. This means:

- **WAI-ARIA compliant** out of the box — screen readers, keyboard navigation, focus trapping, scroll locking.
- **No opinions on styling** — Radix gives you the *what* (a dialog that opens, traps focus, closes on ESC) without dictating the *how* (what it looks like).
- **Battle-tested** — Used by thousands of React applications. The hard accessibility work is done once and done right.
- **Composable** — Each primitive is a building block. A `Dialog` is composed of `Trigger`, `Overlay`, `Content`, `Title`, `Description`, `Close`. You assemble what you need.

This is a deliberate architectural choice: **separating behaviour from presentation**. The same principle that makes Clojure's pure functions powerful — separate the "what" from the "how" — applies here at the component level.

### 2. Utility-First Styling (Tailwind CSS v4)

[Tailwind CSS](https://tailwindcss.com/) provides the **styling layer** — a utility-first CSS framework that maps directly to CSS properties:

- **No abstraction gap** — `bg-primary`, `text-sm`, `rounded-md` map directly to CSS. You're writing CSS, just with a better syntax.
- **Design tokens via CSS custom properties** — Colors, spacing, radii, shadows are all defined as CSS variables. Change `--primary` and every component updates.
- **Dark mode built-in** — The `.dark` class toggles an entire parallel set of design tokens. Every component respects both themes automatically.
- **No specificity wars** — Utility classes are flat. No cascading nightmares, no `!important` hacks.
- **Purged in production** — Only the classes you actually use end up in your CSS bundle.

Combined with [`tailwind-merge`](https://github.com/dcastil/tailwind-merge), class conflicts are resolved intelligently. When you pass `:class "px-8"` to a button that defaults to `px-4`, the override wins correctly.

### 3. Clojure-Native Component Wrappers

The third layer — and what this library actually ships — is **Clojure/ClojureScript wrappers** that tie the first two layers together using Clojure idioms:

- **Keywords, not strings** — Variants are `:destructive`, `:outline`, `:ghost` — not `"destructive"`. Sizes are `:sm`, `:lg`, `:icon`. This is data, and it composes the way Clojure data composes.
- **Reagent-native** — Components are plain Reagent functions. Props are Clojure maps. Children are Hiccup. No JSX translation layer.
- **Maps, not positional arguments** — Props are always a map as the first argument, followed by children. Consistent, predictable, readable.
- **Idiomatic interop** — Where we use JS libraries (Radix, Embla, TanStack, Shiki), the interop is handled *inside* the component. Consumers see pure Clojure.

## The Copy/Paste Ownership Model

This is perhaps the most important design decision, and the one most aligned with Clojure's values.

**You don't install these components as a dependency. You copy them into your project.**

This means:

### You Own the Code

Every component lives in your codebase. When you need to change something — a different animation, an extra variant, a modified layout — you open the file and change it. No forking a library. No waiting for a PR to be merged. No version conflicts.

### Simplicity Over Indirection

Rich Hickey's [Simple Made Easy](https://www.infoq.com/presentations/Simple-Made-Easy/) resonates here. A component library as a dependency is *easy* (just add it to `deps.edn`) but not *simple* — you're complecting your UI with someone else's release cycle, their API decisions, their breaking changes.

Copying the code is slightly less *easy* but far more *simple*. The component is right there. You can read every line. There are no hidden layers.

### Pure Functions at Every Level

Each component is a pure function of its props. No internal state management magic, no global registries, no implicit dependencies between components. This means:

- **Easy to test** — Pass props in, get Hiccup out.
- **Easy to replace** — Swap one component for another. The rest of the system doesn't care.
- **Easy to understand** — Open the file, read the function, see exactly what HTML and CSS it produces.

### Composability Over Configuration

Rather than one mega-component with 50 props, you get small composable pieces:

```clojure
;; A dialog is composed from parts you control
[dialog {:open @open? :onOpenChange #(reset! open? %)}
 [dialog-trigger {}
  [button {} "Open"]]
 [dialog-content {}
  [dialog-header {}
   [dialog-title {} "Confirm"]
   [dialog-description {} "Are you sure?"]]
  [dialog-footer {}
   [button {:variant :outline} "Cancel"]
   [button {} "Confirm"]]]]
```

Don't need a footer? Don't include it. Need a custom close button? Replace `dialog-close`. Need the overlay to be a different color? Change one Tailwind class.

## How It Plays With Design Systems

The design token architecture (CSS custom properties → Tailwind theme → component classes) means this library works naturally with design systems:

1. A designer defines tokens in Figma (colors, spacing, typography, radii, shadows).
2. Those tokens map 1:1 to CSS variables in `main.css` (e.g., `--primary`, `--radius`, `--shadow-md`).
3. Tailwind reads those variables via `@theme inline` and generates utility classes (`bg-primary`, `rounded-lg`, `shadow-md`).
4. Components use those utility classes. Change the token, change every component.

This is a **single source of truth** for your visual language, flowing from design → CSS → utilities → components.

## What You Get

When you adopt this library, you're getting:

- **A starting point, not a ceiling** — 40+ production-ready components to ship fast, with full freedom to modify as your app grows.
- **Accessibility for free** — Radix handles the hard WAI-ARIA work. Your components are screen-reader friendly, keyboard-navigable, and focus-managed out of the box.
- **Theme-ability** — Light/dark mode, custom color palettes, consistent design tokens — all through CSS variables.
- **ClojureScript-native** — No fighting JS interop. Keywords, maps, Hiccup, Reagent. The components feel like Clojure because they are Clojure.
- **No lock-in** — Every component is a file in your project. Replace it, modify it, delete it. The library serves you, not the other way around.
- **Portfolio integration** — Every component has a visual showcase using [Portfolio](https://github.com/cjohansen/portfolio) (Clojure's answer to Storybook), so you can develop and document in isolation.

## How It Compares

| Aspect | Traditional UI Library | shadcn/ui (JS) | This Library |
|--------|----------------------|----------------|--------------|
| Language | JS/TS | JS/TS | ClojureScript |
| Ownership | npm dependency | Copy/paste | Copy/paste |
| Styling | CSS-in-JS / CSS Modules | Tailwind CSS | Tailwind CSS |
| Behaviour | Custom or Headless UI | Radix UI | Radix UI |
| Props API | Strings / Objects | Strings / TypeScript | Keywords / Maps |
| Component model | React | React / RSC | Reagent |
| Showcase | Storybook | Storybook | Portfolio |
| Variants | String enums | cva (class-variance-authority) | `case` on keywords |
| Design tokens | Varies | CSS variables | CSS variables |

## The Clojure Alignment

This approach resonates with values the Clojure community holds:

- **Data > Code** — Components are configured with data (maps of keywords), not imperative APIs.
- **Simplicity** — Each layer does one thing. Radix = behaviour. Tailwind = styling. Wrappers = Clojure idioms.
- **Transparency** — No hidden magic. The source is right there, in your project.
- **Stability** — You own the code. No upstream breaking changes. Upgrade Radix or Tailwind on your schedule.
- **Pragmatism** — We use the best JS tools (Radix, Tailwind, TanStack) but wrap them in Clojure. Best of both worlds.

This is not about reinventing the wheel. It's about putting good wheels on a Clojure car.
