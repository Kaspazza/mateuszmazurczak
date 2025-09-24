# Four Approaches to Ports and Adapters in Clojure

The Hexagonal Architecture (also known as Ports and Adapters) is a powerful pattern for building maintainable, testable applications. In Clojure, there are four main approaches to implementing this pattern, each with its own trade-offs. Let's explore all approaches and when to use each.

## The Core Concepts

Before diving into implementations, let's clarify the terminology:

- **Port**: The interface that defines what your application needs (e.g., "I need to translate text")
- **Adapter**: The concrete implementation that fulfills the port (e.g., "Translation done by Tempura library", "Translation done by clj-i18n")
- **System**: The wiring layer that connects adapters to ports (e.g.,"Now when development system is starting for translation library we are using tempura" )

## Approach 1: Protocol-Based (Pure Separation)

This approach uses Clojure's `defprotocol` and `defrecord` for clean interface segregation.

### Implementation

```clojure
;; Port definition (pure interface)
(defprotocol TranslationPort
  "Port for text translation functionality"
  (translate [this language translation-key]
    "Translate text for given language and key"))

;; Adapter implementation
(defrecord TempuraAdapter [translation-opts]
  TranslationPort
  (translate [_this language translation-key]
    (tempura/tr translation-opts 
                (if (vector? language) language [language]) 
                [translation-key])))

;; System wiring (Integrant)
(defmethod ig/init-key :translator/tempura
  [_ {:keys [debug? dictionaries]}]
  (let [opts (create-tempura-opts debug? dictionaries)]
    (->TempuraAdapter opts)))

;; Usage in components
(defn my-handler [request]
  (let [translator (:translator request)]
    {:body (translate translator :en :welcome-message)}))
```

### Benefits
- **Pure interface definition**: Protocol clearly defines what operations are available
- **Multiple implementations**: Easy to swap between different translation libraries
- **Explicit dependencies**: Components receive translator as explicit parameter
- **Easy testing**: Mock implementations are straightforward to create

### When to Use
- Multiple potential implementations (Redis vs in-memory cache, different databases)
- Complex domains with many operations
- When you need different implementations for different environments
- Team prefers explicit interfaces

## Approach 2: Multimethod-Based (Dynamic Dispatch)

This approach uses Clojure's `defmulti` and `defmethod` for runtime polymorphism based on dispatch values.

### Implementation

```clojure
;; Port definition (multimethod)
(defmulti translate
  "Translate text using different translation strategies"
  (fn [strategy language translation-key] strategy))

;; Adapter implementations
(defmethod translate :tempura
  [_strategy language translation-key]
  (tempura/tr tempura-opts 
              (if (vector? language) language [language]) 
              [translation-key]))

(defmethod translate :google
  [_strategy language translation-key]
  (google-translate/translate language translation-key))

(defmethod translate :static
  [_strategy language translation-key]
  (get-in static-translations [language translation-key] "MISSING"))

;; System wiring
(defmethod ig/init-key :translator/strategy
  [_ {:keys [strategy]}]
  (fn [language key] (translate strategy language key)))

;; Usage
(defn my-handler [request]
  (let [translator (:translator request)]
    {:body (translator :en :welcome-message)}))
```

### Benefits
- **Runtime flexibility**: Can switch strategies without recompilation
- **Open for extension**: New implementations can be added anywhere in codebase
- **Hierarchical dispatch**: Support for inheritance and complex dispatch logic
- **Dynamic reconfiguration**: Can change strategies at runtime

### When to Use
- Need runtime strategy switching
- Plugin architectures
- A/B testing different implementations
- Complex dispatch logic based on data

## Approach 3: Registry-Based (Configuration Driven)

This approach uses a registry (map) to store and lookup different implementations.

### Implementation

```clojure
;; Registry definition
(def translation-strategies-registry
  "Registry of available translation strategies"
  {:tempura {:description "Tempura-based translation"
             :create-fn tempura-translator/create}
   :google  {:description "Google Translate API"
             :create-fn google-translator/create}
   :static  {:description "Static file-based translation"
             :create-fn static-translator/create}})

;; Port implementation
(defn create-translator
  "Creates translator based on strategy from registry"
  [strategy config]
  (let [strategy-def (get translation-strategies-registry strategy)]
    (when-not strategy-def
      (throw (ex-info "Unknown translation strategy" {:strategy strategy
                                                      :available (keys translation-strategies-registry)})))
    ((:create-fn strategy-def) config)))

;; System wiring
(defmethod ig/init-key :translator/registry
  [_ {:keys [strategy config]}]
  (create-translator strategy config))

;; Usage with introspection
(defn list-available-strategies []
  (map (fn [[k v]] {:key k :description (:description v)})
       translation-strategies-registry))
```

### Benefits
- **Discoverability**: Easy to see all available implementations
- **Metadata support**: Can store descriptions, validation, etc.
- **Configuration-driven**: Strategy selection through config files
- **Runtime introspection**: Can list and validate available strategies

### When to Use
- Need to list/document available implementations
- Configuration-driven architecture
- Plugin discovery mechanisms
- When implementations come from different modules

## Approach 4: Function Namespace Hybrid (Pragmatic)

This approach combines the port definition and adapter choice in a single namespace.

### Implementation

```clojure
;; i18n.cljc - Hybrid Port/Application Service
(ns app.i18n
  "Translation port - public API for translation functionality"
  (:require [app.adapters.tempura :as tempura-adapter]))

(defn create-translator
  "Creates a translator function configured for the environment"
  [debug?]
  (let [tempura-opts (tempura-adapter/create-opts debug?)]
    (fn translate [language translation-key]
      (tempura-adapter/translate tempura-opts language translation-key))))

(defn tr
  "Helper function for common translation pattern"
  [translator language translation-key]
  (translator language translation-key))

;; System wiring (Integrant)
(defmethod ig/init-key :app/translator
  [_ {:keys [debug?]}]
  (i18n/create-translator debug?))

;; Usage remains the same
(defn my-handler [request]
  (let [translator (:translator request)]
    {:body (translator :en :welcome-message)}))
```

### Benefits
- **Less ceremony**: No protocols or records needed
- **Single implementation focus**: Perfect when you have one clear adapter choice
- **Application Service pattern**: Namespace acts as a configured service layer
- **Functional composition**: Easy to compose and configure

### When to Use
- Single, stable implementation (unlikely to change)
- Simple domains with few operations
- Rapid prototyping or smaller codebases
- When protocols feel like over-engineering

## Architectural Analysis

### The Spectrum of Abstraction

```
Low Abstraction                                           High Abstraction
      |                                                         |
Direct Usage → Function Namespace → Registry → Multimethod → Protocols
  (No ports)      (Hybrid)        (Config)   (Dynamic)      (Pure)
```

### Decision Matrix

| Factor | Protocol | Multimethod | Registry | Function Namespace |
|--------|----------|-------------|----------|-------------------|
| **Interface Clarity** | ✅ Explicit | ⚠️ Implicit | ⚠️ Registry-dependent | ⚠️ Implicit |
| **Runtime Flexibility** | ❌ Compile-time | ✅ Full runtime | ⚠️ Config-driven | ❌ Compile-time |
| **Implementation Discovery** | ❌ Code inspection | ❌ Code inspection | ✅ Registry listing | ❌ Code inspection |
| **Development Speed** | ❌ More setup | ⚠️ Medium setup | ⚠️ Medium setup | ✅ Fastest |
| **Testing** | ✅ Easy mocking | ⚠️ Dispatch complexity | ⚠️ Registry setup | ⚠️ More coupling |
| **Performance** | ✅ Direct dispatch | ⚠️ Dynamic dispatch | ⚠️ Map lookup | ✅ Direct calls |
| **Extensibility** | ⚠️ Recompilation needed | ✅ Runtime extension | ✅ Registry updates | ❌ Namespace changes |

## Real-World Example: Translation System

Let's see all approaches applied to a translation system:

### Protocol Approach
```clojure
;; Clear interface, multiple potential implementations
(defprotocol I18nPort
  (tr [this lang key])
  (available-languages [this])
  (reload-dictionaries! [this]))

;; Easy to create test doubles
(defrecord MockTranslator [responses]
  I18nPort
  (tr [_this lang key] (get-in responses [lang key] "MISSING"))
  (available-languages [_this] [:en :pl])
  (reload-dictionaries! [_this] :ok))
```

### Multimethod Approach
```clojure
;; Runtime strategy switching
(defmulti translate :strategy)
(defmethod translate :tempura [opts lang key] ...)
(defmethod translate :google [opts lang key] ...)

;; Easy to test different strategies
(deftest translation-strategies-test
  (is (= "tempura-result" (translate {:strategy :tempura} :en :key)))
  (is (= "google-result" (translate {:strategy :google} :en :key))))
```

### Registry Approach
```clojure
;; Discoverable implementations
(def strategies
  {:tempura {:create-fn create-tempura-translator}
   :google  {:create-fn create-google-translator}})

;; Configuration-driven selection
(defn create-from-config [config]
  (let [strategy (get-in config [:translation :strategy])]
    (create-translator strategy config)))
```

### Function Namespace Approach
```clojure
;; Simple, focused API
(defn create-translator [config]
  {:translate (fn [lang key] ...)
   :available-languages (fn [] ...)
   :reload! (fn [] ...)})

;; Testing requires more setup
(deftest translation-test
  (with-redefs [tempura/tr (fn [_ _ _] "mocked")]
    (is (= "mocked" (translate test-translator :en :key)))))
```

## System Integration Patterns

All approaches work well with Integrant for dependency injection:

```clojure
;; Protocol-based
{:translator/tempura {:debug? true}
 :app/handlers {:translator (ig/ref :translator/tempura)}}

;; Multimethod-based  
{:translator/multimethod {:strategy :tempura :debug? true}
 :app/handlers {:translator (ig/ref :translator/multimethod)}}

;; Registry-based
{:translator/registry {:strategy :tempura :config {...}}
 :app/handlers {:translator (ig/ref :translator/registry)}}

;; Function namespace
{:translator/function {:debug? true}
 :app/handlers {:translator (ig/ref :translator/function)}}
```

## Recommendations

### Choose Protocol-Based When:
- Building libraries or frameworks
- Need explicit interface contracts
- Multiple implementations are likely
- Team values compile-time safety
- Testing with mocks is critical

### Choose Multimethod-Based When:
- Need runtime strategy switching
- Building plugin architectures
- A/B testing implementations
- Complex dispatch logic required
- Open/closed principle is important

### Choose Registry-Based When:
- Need implementation discovery
- Configuration-driven architecture
- Documentation of available strategies is important
- Implementations come from different modules
- Runtime introspection is valuable

### Choose Function Namespace When:
- Building applications (not libraries)
- Single, stable implementation exists
- Rapid development is prioritized
- Team prefers simplicity over flexibility
- Domain is simple and unlikely to change

## Conclusion

All four approaches are valid implementations of the Ports and Adapters pattern, each serving different needs:

- **Protocols** provide the purest separation and compile-time safety
- **Multimethods** offer maximum runtime flexibility and extensibility  
- **Registry** enables discovery and configuration-driven architecture
- **Function Namespaces** deliver pragmatic simplicity with architectural boundaries

The key insight is understanding that these approaches exist on a spectrum from simple (Function Namespace) to flexible (Multimethod) to explicit (Protocol). The Registry approach is unique in providing discoverability and metadata capabilities.

Choose based on your specific context: runtime requirements, team preferences, complexity tolerance, and long-term maintenance needs. Each approach will serve you well when matched to the right use case.

---

*Have you used either of these patterns in your Clojure projects? What was your experience? Share your thoughts in the comments below.*
