# Mateusz Mazurczak website 
This is a private project representing www.mateuszmazurczak.com (and .pl) 

## Architecture

**Hexagonal architecture (ports/adapters)** - infrastructure is split into:
- **Port** - API namespace defining interface (e.g., `mateuszmazurczak.database`, `mateuszmazurczak.logging`)
- **Adapters** - Concrete implementations in subdirectories (e.g., `database/adapters/datomic.clj`, `logging/telemere.cljc`)

### Port/Adapter Implementation Patterns

Three patterns based on how well requirements are known:

**1. Protocol-based (Full Implementation)**
- Port defines `defprotocol` interface, adapters implement via `defrecord`
- Use when: **Requirements are well-known and stable**
- Clean separation, easy to swap implementations
- Example: `logging/protocol.cljc` + `logging/telemere.cljc`

**2. Hybrid (Function Namespace)**
- Port namespace provides API functions + directly uses one adapter implementation
- Use when: **Requirements are unclear or still evolving**
- Less ceremony, easier to change as requirements become clear
- Namespace acts as Application Service layer

**3. Minimal (No Port)**
- Single implementation, no abstraction layer
- Use when: No variation expected or needed

### System Composition

Infrastructure is wired together using **Integrant** with configuration from `env/*/config.edn`. 

**System files** (`system.clj` for backend, `frontend_system.cljs` for frontend):
- Define Integrant lifecycle methods (`ig/init-key`, `ig/halt-key!`) for each component
- Import and wire specific adapters to the system
- Choose which adapter to use based on config values

**Key principle:** 
- Port namespaces **never import adapters** - only define interfaces
- System files **do import adapters** - that's their job as the composition root
- Adapters stay pure - no Integrant dependency, just constructors and protocol implementation
- Adding new adapter = edit system.clj (add defmethod wrapper)
- Switching between adapters = config change only

### Event System (Frontend)

The frontend uses **registry-based event architecture** to decouple UI from state management implementation.

**Core Components:**

1. **Event Registry** (`events/registry.cljs`)
   - Single source of truth defining ALL application events
   - Each event specifies:
     - `:category` - Logical grouping (`:navigation`, `:page`, `:i18n`)
     - `:description` - What the event does (behavior, not implementation)
     - `:schema` - Malli schema for validation
     - `:handler-type` - `:fx` (side-effects) or `:db` (pure state update)

2. **Event Port** (`events.cljs`)
   - Public API for event dispatching
   - Validates adapter completeness (all registry events implemented)
   - Validates adapter correctness (no unknown events)
   - **UI code only imports this namespace, never adapters**

3. **Event Adapter** (`events/adapters/reframe/`)
   - Re-frame implementation of registry contract
   - Organized by domain: `navigation.cljs`, `i18n.cljs`, `pages/*.cljs`
   - Exports three things:
     - `handlers` - Map of `{event-id handler-fn}`
     - `register-fns` - Map of `{:db reg-event-db, :fx reg-event-fx}`
     - `get-dispatch-fn` - Returns `re-frame.core/dispatch`

**System Wiring** (`frontend_system.cljs`):

```clojure
;; 1. Initialize adapter (effects, subscriptions, etc.)
(defmethod ig/init-key :events.adapter/reframe [_]
  (events-reframe/init!)  ; Adapter-specific setup
  {:handlers (events-reframe/handlers)
   :register-fn (events-reframe/register-fns)
   :dispatch-fn (events-reframe/get-dispatch-fn)})

;; 2. Wire adapter to event port
(defmethod ig/init-key :frontend/events [_ {:keys [adapter]}]
  (events/wire! (:handlers adapter) (:register-fn adapter))
  (events/set-dispatch! (:dispatch-fn adapter)))
```

**For UI Developers:**

```clojure
(require '[mateuszmazurczak.events :as events])

;; Direct dispatch
(events/dispatch! [:nav/navigate ::routes/home])

;; Or dispatch markers for data-driven UI
(def data {:on-click [:dispatch [:nav/navigate ::routes/home]]})

;; Convert markers to functions
(events/dispatch-tree data)
;; => {:on-click #(events/dispatch! [:nav/navigate ::routes/home])}
```

**Route Controllers:**

Events integrate with routing via controller pattern (`:controllers` in `routes.cljs`):

```clojure
["/home" 
 {:name ::home
  :panel-id :panels/home
  :controllers [{:start (fn [_] (events/dispatch! [:home/on-route-enter]))}]}]
```

When route is entered, controller dispatches page event to load data.

**For Adapter Implementers:**

To replace re-frame with another state management library:

1. **Check registry** - All events in `events/registry.cljs` must be implemented
2. **Create adapter namespace** - e.g., `events/adapters/xyz/core.cljs`
3. **Export three things:**
   - `handlers` - Map of `{event-id handler-fn}` for all registry events
   - `register-fns` - Map of `{:db your-reg-fn, :fx your-reg-fn}` matching handler types
   - `get-dispatch-fn` - Returns your dispatch function
4. **Implement `init!`** - Setup if needed for starting the library  
5. **Wire in `frontend_system.cljs`** - Create new component for your adapter
6. **Update config** - Point `:events.adapter` to your new adapter

The port validates everything at system startup (fail-fast), so missing events are caught immediately. 

### Configuration System

The application uses **Integrant** with **Aero**. 

#### Configuration Hierarchy

The system follows this precedence order:
1. **Environment Variables** (highest priority - #env)
2. **Secrets File** (`.secrets.edn` file at root #secrets)

#### Backend Configuration Files
Main point of any used env variables or externally driven config is read from config.edn files and passed to integrant components defined under :system.

**Environment-specific config files:**
- `env/development/config.edn` - Development environment 
- `env/production/config.edn` - Production environment

**Secrets file:**
- `.secrets.edn` - Contains sensitive configuration for all environments (not in version control)

#### Configuration Readers

**Aero edn readers:**
- `#env VAR_NAME` - Read from environment variable
- `#secrets [:sentry :backend :dsn]` - Reads from current environment profile in .secrets.edn
- `#or [#env VAR #secrets [:path]]` - Try env var first, fall back to secrets
- `#ref [:config :path]` - Reference other config values
- `#ig/ref :component` - Integrant component references

#### Frontend Configuration (Shadow-CLJS)

Frontend configuration uses `goog-define` values in `src/cljs/mateuszmazurczak/config.cljs`:

```clojure
(goog-define ENV "")
(goog-define LOG_SENTRY_DNS "")
```

These are set via closure-defines with environment-specific overrides:

**Development:**
- Uses default values from `shadow-cljs.edn` (under :closure-defines key)
- ENV: `"development"`

**Production:**
- Uses `--config-merge` during release build to override closure-defines
- Reads variables from `.secrets.edn` in build-jar task
- ENV: `"production"`
- Values are populated from the same Integrant configuration that drives the backend

#### Secrets File Structure

```edn
{:development
 {:db {:uri "./storage/datalevin/dev-db"}
  :sentry 
  {:backend {:dsn "https://..."}
   :frontend {:dsn "https://..."}}
  :posthog {:api-key "phc_..."}}
   
 :production  
 {:db {:uri "/app/data/db"}
  :sentry
  {:backend {:dsn "https://..."}
   :frontend {:dsn "https://..."}}
  :posthog {:api-key "phc_..."}}}
```

#### Example Configuration Usage

```edn
{:db {:uri #or [#env DB_URI #secrets [:db :uri]]}
 :log {:sentry {:backend {:dsn #or [#env SENTRY_BACKEND_DSN #secrets [:sentry :backend :dsn]]}}}}
```

## Set up
Updating system variables in Mac: 
https://phoenixnap.com/kb/set-environment-variable-mac


## Running the App
From this directory run:
- bb dev-launch

Tests: localhost:8081
Shadow: localhost:9630
App: localhost:3000

After that you can connect to running repl (8000).

## Docker Deployment

The application supports Docker-based deployment with babashka tasks for building, pushing, and running containerized versions.

### Prerequisites
- Docker installed and running
- Docker Hub account (or other registry) configured with `docker login`
- `.secrets.edn` file for running with production configuration locally (on actual prod env variables are used)

### Available Commands

#### Build Docker Image
Build a Docker image with a specific version tag:
```bash
bb docker-build <version>
```

Example:
```bash
bb docker-build 1.0.0
```

This creates an image: `mateuszmazurczak/personal:1.0.0` (platform: linux/amd64)

#### Push Existing Image
Push an already-built image to docker hub:
```bash
bb docker-push <version>
```

#### Build and Push to Registry
Build and push in one command:
```bash
bb docker-build-push <version>
```

#### Run Docker Image Locally
Run a Docker image with automatic secrets injection:
```bash
bb docker-run <version>                                # by default runs with "production profile"
bb docker-run --profile development <version>          # development secrets
```


The `docker-run` task:
- Runs with `--rm` flag (auto-cleanup after stop)
- Exposes port 8080 → 8080
- Automatically convers secrets from `.secrets.edn` based on profile to env variables

### Configuration in Docker

The Docker container expects configuration via:
1. **Environment variables** (highest priority):
   - `DB_URI` - Database path/URI
   - `SENTRY_BACKEND_DSN` - Sentry DSN for backend errors
   - `SENTRY_FRONTEND_DSN` - Sentry DSN for frontend errors

2. **Built-in defaults**: Production config from `env/production/config.edn`

The `bb docker-run` command automatically reads `.secrets.edn` and injects ENV vars, but in production you should use your orchestration tool (docker-compose, K8s secrets, etc.) to manage environment variables.

### Docker Build Details
- **Dockerfile**: `docker/build.dockerfile`
- **Platform**: linux/amd64 (for compatibility with most cloud providers)
- **Port**: 8080
- **Data volume**: `/app/data` (for database persistence)

## General
src/bb/ <- only for bb.edn, contains all useful scripts for working with this app

Code in this repo is for my personal website, and it's built in a way to cover all the web-app usefull functionalities and code separation for quick starting web projects in clojure.

Current state of code contains:
- Scripting for simplifying work with the project
- Configuration
- Environments separation 
- Translation i18n (with taoensso tempura)
- Full-stack routing and setup (ring, reitit, shadow-cljs)
- Basic UI with integrated hiccup, Tailwind, DaisyUI
- Frontend logic and data manged with reagent/re-frame
- Portfolio setup for frontend development
- User error monitoring (with sentry)
- Logs
- Frontend analysis tooling, heatmaps, users on the page etc. 

TODO as features:
- Chat to speak to - so instead of saying contact me at *this-email*, just open chat option that sends email or smth
- Realtime module with information that the page has been updated, so user can click and hard-refresh
- UI theme
- describe testing FE/BE/E2E/ab
- Auth (and feature-flags)
- Versioning (low priority)

## Adding articles
- [ ]  Write article in .md (or in Notion and convert to .md)
- [ ]  Put it in resources/public/article/content
- [ ]  If any imgs put them in resources/public/article/img
- [ ]  Add article in articles.edn
- [ ]  run bb article 
- [ ]  add :content to your article in articles inside articles.core
------- In future it will be moved to only html and static rendering, removing the last point ----

License information can be found in [LICENSE file](LICENSE.md)
Copyright © 2024 Mateusz Mazurczak

