# Philosophy & Approach

> **Copy-first UI components for ClojureScript (Reagent + Tailwind), inspired by shadcn/ui — you own the code, customize freely, and keep UI concerns isolated.**

## Why This Library Exists

Building UIs in ClojureScript has always had a friction point: the ecosystem is small, and most mature component libraries live in the JavaScript/React world. After 10 years of building web applications and trying every approach, the pattern is always the same: **it works great on day one and fights you on day 300.**

### The approaches that don't work long-term

**Using a full UI library (Material UI, Ant Design, Chakra, etc.)**

These are seductive. You install the package, import a Button, and it looks professional in minutes. But then:

- You need a button that's *almost* like theirs but with a different loading state. You dig into their API — 47 props, none of which do what you want.
- You need to change how a Dialog animates. The animation is buried three layers deep in their CSS-in-JS runtime. You can't reach it without `!important` hacks or forking the library.
- You upgrade to the next major version. Half your overrides break because they renamed internal CSS classes.
- Your designer wants a specific shadow on cards. The library's theme system *almost* supports it, but the token name doesn't match the CSS property you need, so you end up fighting the abstraction.

And the worst part: **you can't just use one component.** These libraries put tentacles into your entire frontend. Material UI needs a `ThemeProvider` wrapping your whole app, a `CssBaseline` resetting your styles, their styling engine (`@emotion` or `styled-components`) as a runtime dependency, and often a specific React version. You didn't want an ecosystem — you wanted a Button. But one component drags in an entire world of opinions about how your app should be structured, styled, and themed. You're not using a library anymore — you're using a framework.

The fundamental problem: **they couple behaviour, styling, and design decisions into one opaque package** and then demand your entire application conform to their way of doing things. When you need to change one thing, you're fighting the other two — plus the framework wrapping it all.

**Building everything from scratch**

The opposite extreme. You control everything, but now you're implementing WAI-ARIA patterns for dropdown menus, focus trapping for modals, scroll locking for overlays, keyboard navigation for comboboxes. That's months of work before you ship a single feature — and you'll still get accessibility wrong in subtle ways that only screen reader users notice.

**Using Clojure-specific UI libraries**

The ecosystem is small. The libraries that exist tend to be one-person projects with opinions baked in. They work until they don't, and when they don't, you're stuck waiting for a maintainer who has a day job. You can't easily change the internals because the library wasn't designed for that — it was designed to be used as-is.

**Using headless libraries alone (Radix, Headless UI)**

Better — you get accessible behaviour without styling opinions. But now you're making every design decision yourself. What padding? What border radius? What hover state? What focus ring? Multiply that by 40+ components and you're spending weeks on decisions that have well-known good answers. And if you're not a designer, the result looks inconsistent.

### The common root cause

Every one of these failures shares the same root: **they complect things that should be separate.** Behaviour, styling methodology, and design decisions are three independent concerns. When a library bundles them together, changing one means fighting the other two. When a library provides none of them, you're building everything from scratch.

This library takes a different path, inspired by [shadcn/ui](https://ui.shadcn.com/) but built for and by the Clojure community: **keep the three concerns separate, use the best tool for each, and give you ownership of the result.**

## The Core Idea: Three Problems, Three Layers, One Stack

Each UI concern is solved by a dedicated tool that does one thing well:

1. **Behaviour** → **Radix UI** — How does a dropdown open? How does focus get trapped in a modal? How does a screen reader announce a toast? This is accessibility and interaction logic — hard to get right, easy to get wrong, and identical regardless of what your app does. Radix solves this once, correctly, for thousands of applications.
2. **Styling methodology** → **Tailwind CSS** — What CSS approach won't turn into a specificity nightmare at scale? How do you keep styles maintainable without digging into semantic classes like `.hero` or `.btn-primary` that become meaningless the moment you need to change them? Tailwind solves this by mapping utilities directly to CSS properties — no abstraction gap, no specificity wars, no dead styles.
3. **Design decisions** → **This library** — What border radius should a card have? How much padding does a button need? What's the hover state? These decisions take weeks to make well, and inconsistency here makes your app feel amateur. This library provides thoughtful, pre-made answers that you can change at any time because they're just Tailwind classes in files you own.

The insight: **each of these problems is already solved — by a different tool.** What was missing is someone wiring them together in ClojureScript with Clojure idioms.

That's what this library is. It's the **glue layer** — pre-made, thoughtful styling decisions on top of battle-tested behaviour primitives, expressed as Reagent components with keyword-driven APIs. You get a production-ready starting point without giving up control, because you own every line and every concern stays separate.

Here's how the three layers work in detail:

### 1. Unstyled, Accessible Primitives (Radix UI)

[Radix UI](https://www.radix-ui.com/) provides the **behaviour layer** — headless, fully accessible UI primitives. "Headless" means Radix has zero opinions about how your components look. It solves *only* the hard interaction problems: accessibility, keyboard navigation, focus management, scroll locking. This means:

- **WAI-ARIA compliant** out of the box — screen readers, keyboard navigation, focus trapping, scroll locking.
- **No opinions on styling** — Radix gives you the *what* (a dialog that opens, traps focus, closes on ESC) without dictating the *how* (what it looks like).
- **Battle-tested** — Used by thousands of React applications. The hard accessibility work is done once and done right.
- **Composable** — Each primitive is a building block. A `Dialog` is composed of `Trigger`, `Overlay`, `Content`, `Title`, `Description`, `Close`. You assemble what you need.

This is a deliberate architectural choice: **separating behaviour from presentation**. The same principle that makes Clojure's pure functions powerful — separate the "what" from the "how" — applies here at the component level.

### 2. Utility-First Styling (Tailwind CSS v4)

[Tailwind CSS](https://tailwindcss.com/) provides the **styling layer**. It's not a replacement for CSS — it *is* CSS, with the common pain points solved: no specificity wars, no naming conventions to argue about, no dead styles accumulating in your bundle.

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

The design token architecture (CSS custom properties → Tailwind theme → component classes) means this library works naturally with design systems — and crucially, **you don't need a designer to start**.

The default theme gives you a coherent, professional look on day one. The token architecture means that when a designer *does* join, they don't need to touch your components — they adjust CSS variables in one file, and everything updates.

The flow works like this:

1. A designer defines tokens in Figma (colors, spacing, typography, radii, shadows) — *or you use the sensible defaults*.
2. Those tokens map 1:1 to CSS variables in `main.css` (e.g., `--primary`, `--radius`, `--shadow-md`).
3. Tailwind reads those variables via `@theme inline` and generates utility classes (`bg-primary`, `rounded-lg`, `shadow-md`).
4. Components use those utility classes. Change the token, change every component.

This is a **single source of truth** for your visual language, flowing from design → CSS → utilities → components. The handoff between developer and designer is a file of CSS custom properties — not a redesign.

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

## The Contract: What We Promise, What We Don't

Clear boundaries prevent scope creep and set honest expectations.

### We promise

- **Reagent/Hiccup components + Tailwind classes** — Every component is a plain Reagent function returning Hiccup with Tailwind utility classes. No proprietary abstractions.
- **Copy/paste workflow** — Not "add a dependency and forget." You take the source, it lives in your project, you own it forever.
- **UI-only** — Components handle presentation and user interaction. Zero business logic, zero domain leakage, zero state management opinions. They sit cleanly in the UI layer of any architecture.
- **Consistent API conventions** — Every component follows the same patterns: props map first, children after, keywords for variants, `:class` for overrides. Learn one, know them all.
- **Accessible by default** — Radix primitives handle WAI-ARIA, keyboard navigation, focus management. You get accessibility without thinking about it.

### We don't promise

- **Backwards compatibility across copies** — Once you copy a component, it's yours. We may improve the source, but we won't version-manage your copy. That's the point.
- **A complete design system** — We provide sensible defaults and a token architecture. Your designer (or you) fills in the brand-specific details.
- **Every component you'll ever need** — We cover the common 40+. Domain-specific components (a mortgage calculator, a Kanban board) are your job — but ours make good building blocks.
- **Framework integration** — We don't ship re-frame subscriptions, Integrant components, or routing helpers. Components are pure UI; how you wire them to your app is your decision.

## Flexibility Spectrum: Use It Your Way

This library is not all-or-nothing. You can adopt it at whatever level fits your project:

### Take one component, ignore the rest

Need just a Dialog and a Button? Copy those two files. There are no hidden dependencies between components (beyond shared utilities like `cn`). The Dropdown doesn't know the Table exists.

### Tweak the styling, keep the behaviour

Every visual decision is a Tailwind class. Want a bigger shadow on your Card? Change `shadow-sm` to `shadow-lg` — it's CSS you own. Want rounder buttons? Change one `rounded-md` to `rounded-full`. The Radix behaviour underneath doesn't care what it looks like.

### Replace a component entirely

If your app outgrows the default Select, write your own. The rest of the library keeps working. Components don't depend on each other's internals — they're functions, not a framework.

### Start without a designer, hand off later

The default theme gives you a coherent, professional look out of the box. Ship with it. When a designer joins, they adjust the CSS variables in one file (`main.css`), and every component updates. The token architecture means the handoff is a file of CSS custom properties, not a redesign.

### Use it as a design system foundation

The flow is: **CSS variables → Tailwind theme → component classes**. This means you already have a design system — colors, spacing, typography, radii, shadows — all defined in one place. A designer can map Figma tokens 1:1 to your CSS variables. The components are living documentation of how those tokens are used.
