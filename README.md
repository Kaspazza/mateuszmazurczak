# Mateusz Mazurczak Website

Personal website project for www.mateuszmazurczak.com (and .pl)

Full-stack Clojure/ClojureScript web application showcasing hexagonal architecture, modern tooling, and production-ready patterns.

## Quick Start

```bash
bb dev-launch    # Starts CLJ REPL (nrepl:8000), Shadow-CLJS watch, Tailwind
```

- **Tests UI**: http://localhost:8081
- **Shadow-CLJS**: http://localhost:9630  
- **App**: http://localhost:3000

See [AGENTS.md](AGENTS.md) for comprehensive build/dev commands.

## Architecture

**Hexagonal architecture (ports/adapters/application/ui)** - explicit separation of concerns:

### Frontend Structure (`src/cljs/mateuszmazurczak/`)

- **`ports/`** - Public APIs (analytics, events, navigation, state). Pure contracts, no adapter imports.
- **`adapters/`** - Infrastructure implementations organized by port:
  - `adapters/analytics/posthog.cljs`
  - `adapters/events/reframe/*` (core, i18n, navigation, pages)
  - `adapters/navigation/*` (reitit_history, reitit_router, routes)
  - `adapters/state/reframe.cljs`
  - `adapters/error_tracking/*` (logging, sentry)
- **`application/`** - Use cases & orchestration (pages/home/data+schema, router, panels). Imports: domain/, ports/ only.
- **`ui/`** - Presentation components (components/*, pages/*). Imports: ports/, application/. **NEVER imports adapters directly.**
- **`system/`** - Composition root (core.cljs, config.cljs, integrant_utils.cljs). Wires everything via Integrant. **ONLY place that imports adapters.**
- **`utils/`** - Pure utilities (cookies, dom, url). No business logic.

**Import Rules** (enforced by structure):
- `domain/` → imports nothing (pure business logic)
- `application/` → imports `domain/`, `ports/` only
- `ports/` → imports nothing (just contracts/protocols)
- `adapters/` → imports `ports/`, `domain/` (implements interfaces)
- `ui/` → imports `ports/`, `application/`, `domain/` (never adapters)
- `system/` → imports everything (composition root wires all layers)

### Backend Structure (`src/clj/mateuszmazurczak/`)

Currently mixed, will follow same hexagonal pattern:
- Database: `adapters/database/*` (datomic, datalevin); utils in `database/utils.clj`; port in `database.clj`
- Error tracking: `adapters/error-tracking/*`; port in `error_tracking.cljc` (hybrid)

### Port/Adapter Patterns

Choose based on requirements clarity:

**1. Protocol-based** - Well-known requirements
- Port = `defprotocol` (e.g., `logging/protocol.cljc`)
- Adapter = `defrecord` (e.g., `logging/telemere.cljc`)
- Port NEVER imports adapters

**2. Hybrid** - Evolving requirements  
- Port provides API + uses one adapter directly (e.g., `error-tracking.cljc`)
- Less ceremony, easier iteration

**3. Minimal** - No variation expected
- Single implementation, no port abstraction

### System Composition

Infrastructure wired via **Integrant** with config from `env/*/config.edn`:

**Backend (`system.clj`)**:
- Fail fast on init errors - broken deployment fails immediately
- No graceful degradation

**Frontend (`system/core.cljs`)**:  
- Use `integrant-utils/optional-component` ONLY for non-critical components (analytics, error-tracking)
- Critical components (router, state, logging) must fail
- On system init failure, show `:panels/system-error`

**Key principles:**
- Port namespaces NEVER import adapters - only define interfaces
- System files DO import adapters - composition root responsibility
- Adapters stay pure - no Integrant dependency
- Adding adapter = edit system file (add defmethod wrapper)
- Switching adapters = config change only

### Event System (Frontend)

**Registry-based event architecture** - decouples UI from state management:

**1. Event Registry** (`adapters/events/registry.cljs`)
- Single source of truth for ALL events
- Each event defines: `:category`, `:description`, `:schema` (Malli), `:handler-type` (`:fx` or `:db`)
- NO event exists outside registry

**2. Event Port** (`ports/events.cljs`)
- Public API for event dispatching  
- Validates adapter completeness on startup (fail-fast)
- **UI code ONLY imports this, never adapters**

**3. Event Adapter** (`adapters/events/reframe/`)
- Re-frame implementation organized by domain (navigation, i18n, pages/*)
- Exports: `handlers`, `register-fns`, `get-dispatch-fn`

**Usage (UI code):**

```clojure
(require '[mateuszmazurczak.ports.events :as events])

;; Direct dispatch
(events/dispatch! [:nav/navigate ::routes/home])

;; Data-driven UI with dispatch markers
(events/dispatch-tree {:on-click [:dispatch [:nav/navigate ::routes/home]]})
;; => {:on-click #(events/dispatch! [...])}
```

**Route Controllers** (`adapters/navigation/routes.cljs`):

```clojure
["/home" {:name ::home
          :panel-id :panels/home
          :controllers [{:start (fn [_] (events/dispatch! [:home/on-route-enter]))}]}]
```

Controllers dispatch page events to load data on route enter. Static data lives in `adapters/state/initial.cljs`, dynamic data loaded via events. NO dispatch in system init. 

### Configuration System

**Integrant** + **Aero** (env/*/config.edn)

**Precedence:**
1. Environment Variables (`#env`)
2. Secrets File (`.secrets.edn`)

**Aero Readers:**
- `#env VAR_NAME` - Environment variable
- `#secrets [:path :to :value]` - From `.secrets.edn` (profile-based)
- `#or [#env VAR #secrets [:path]]` - Fallback chain
- `#ig/ref :component` - Integrant component reference

**Backend:**
- `env/development/config.edn` / `env/production/config.edn`
- Read via Integrant system

**Frontend:**
- `goog-define` values in `system/config.cljs`
- Set via `closure-defines` in `shadow-cljs.edn` (dev) or `--config-merge` (prod)
- Build secrets (POSTHOG_API_KEY, SENTRY_FRONTEND_DSN) from `.secrets.edn` (local) or ENV (Docker/CI)

**`.secrets.edn` structure:**

```edn
{:development {:db {:uri "./storage/datalevin/dev-db"}
               :sentry {:backend {:dsn "..."} :frontend {:dsn "..."}}
               :posthog {:api-key "phc_..."}}
 :production  {:db {:uri "/app/data/db"}
               :sentry {:backend {:dsn "..."} :frontend {:dsn "..."}}
               :posthog {:api-key "phc_..."}}}
```

## Development

### Common Tasks

```bash
bb dev-launch           # Start dev environment (REPL, Shadow, Tailwind)
bb test                 # Run all tests (CLJ + CLJS)
bb test -f              # Disable frontend tests
bb test -b              # Disable backend tests
bb lint                 # Run clj-kondo
bb format               # Format code with zprint
bb article              # Generate HTML from markdown articles
bb update-deps          # Update outdated dependencies
```

### Testing

**CLJ:**
```bash
clojure -M:common-test -n mateuszmazurczak.adapters.http.handler-test  # Single namespace
clojure -M:common-test test/clj/path/to/file_test.clj             # Single file
```

**CLJS (browser):**
```bash
bb dev-launch           # Then open http://localhost:8081 and run tests in Shadow UI
```

**CLJS (Karma/CI):**
```bash
npx karma start --single-run --client.args '["shadow.test.karma.init","^mateuszmazurczak.namespace-test$"]'
```

### Build & Release

```bash
npx shadow-cljs release mateuszmazurczak-app  # Frontend production build
bb build-jar                                   # Backend JAR (add -v for verbose)
bb clean                                       # Clean build artifacts
```

## Docker Deployment

**Prerequisites:**
- Docker installed
- Docker Hub account (`docker login`)
- `.secrets.edn` for local testing (prod uses ENV vars)

**Commands:**

```bash
bb docker-build <version>               # Build image (linux/amd64)
bb docker-push <version>                # Push to registry
bb docker-build-push <version>          # Build + push
bb docker-run <version>                 # Run locally (production profile)
bb docker-run --profile development <v> # Run with dev secrets
```

**Container details:**
- **Port**: 8080
- **Data volume**: `/app/data` (database persistence)
- **Config**: ENV vars (DB_URI, SENTRY_BACKEND_DSN, SENTRY_FRONTEND_DSN) or `env/production/config.edn` defaults

`bb docker-run` auto-injects secrets from `.secrets.edn`. In production, use orchestration tool (docker-compose, K8s) for ENV vars.

## Features

**Current:**
- ✅ Hexagonal architecture (ports/adapters/application/ui)
- ✅ Integrant system composition (backend + frontend)
- ✅ Registry-based event system (re-frame adapter)
- ✅ Full-stack routing (Ring, Reitit, Shadow-CLJS)
- ✅ i18n (taoensso/tempura)
- ✅ UI: Reagent, Tailwind CSS, DaisyUI
- ✅ Component development (Portfolio)
- ✅ Error monitoring (Sentry)
- ✅ Logging (taoensso/telemere)
- ✅ Analytics (PostHog)
- ✅ Database (Datalevin)
- ✅ Docker deployment
- ✅ Babashka scripting (build, test, lint, format)

**Planned:**
- [ ] Contact chat (email integration)
- [ ] Realtime page update notifications
- [ ] UI theming
- [ ] E2E testing documentation
- [ ] Auth + feature flags
- [ ] Versioning

## Code Style & Conventions

- **Architecture**: Explicit hexagonal (see AGENTS.md for detailed rules)
- **Formatting**: zprint (`.zprintrc`: community, how-to-ns, sort-require, hiccup). Run: `bb format`
- **Naming**: kebab-case (fns/vars), CamelCase (records/types), predicates end `?`, side-effects end `!`
- **Namespaces**: Dashes in names (`error-tracking`), map to underscores in paths (`error_tracking/`)
- **Validation**: Malli schemas colocated (`schema.cljc`). CLJS: NEVER `:pre`/`:post` (removed in `:advanced`), use explicit `when-not` + `throw ex-info`
- **Errors**: `ex-info` with `ex-data {:type kw :cause ...}`. Log via `taoensso.telemere` (NO `println`)
- **Tools**: Use eca tools (`eca_read_file`, `eca_grep`, `eca_directory_tree`). NEVER `grep`/`cat`/`ls`/`head`/`tail` directly

See [AGENTS.md](AGENTS.md) for comprehensive architecture guide.

## Adding Articles

1. Write article in `.md` (or convert from Notion)
2. Put in `resources/public/article/content/`
3. Add images to `resources/public/article/img/`
4. Add entry to `articles.edn`
5. Run `bb article` to generate HTML
6. *(Future: static rendering only, removing manual content addition)*

---

**License**: [LICENSE.md](LICENSE.md)  
**Copyright © 2024 Mateusz Mazurczak**

