(ns mateuszmazurczak.application.aoc.playground
  "Application-layer playground integration for AoC.
   
   Handles compression and URL composition for opening code in
   Squint or Cherry playgrounds. Uses pako for gzip compression (JS runtime dependent)."
  (:require
   ["pako" :as pako]
   [mateuszmazurczak.domain.aoc.playground :as playground-domain]))



(defn- uint8array->binary-string
  "Convert Uint8Array to binary string for btoa encoding.
   
   This is necessary because pako.gzip returns a Uint8Array and btoa expects
   a binary string (each character represents a byte)."
  [uint8array]
  (let [len (.-length uint8array)
        chars (js/Array. len)]
    (dotimes [i len] (aset chars i (.fromCharCode js/String (aget uint8array i))))
    (.join chars "")))

(defn compose-playground-url
  "Compose a playground URL (Squint or Cherry) for code content.
   
   Uses gzip compression for Squint (supported), plain base64 for Cherry (not supported).
   
   Args:
   - code: The code string to open in playground
   - opts: Optional map with:
     - :playground - Playground type (:squint | :cherry), default :squint
     - :boilerplate - URL to boilerplate code, default from domain
     - :repl - Enable REPL mode (default: true)
     - :include-helpers? - Include AoC helper comment (default: true)
   
   Returns: Complete playground URL"
  [code
   {:keys [playground boilerplate repl include-helpers?]
    :or {playground :squint
         boilerplate playground-domain/boilerplate-url
         repl true
         include-helpers? true}
    :as _opts}]
  (let [base-url (case playground
                   :cherry playground-domain/cherry-url
                   :squint playground-domain/squint-url)
        code-with-helpers (if include-helpers?
                            (str playground-domain/helper-comment code)
                            code)
        encoded-code (if (= playground :squint)
                       (let [compressed (pako/gzip code-with-helpers)
                             binary-string (uint8array->binary-string compressed)]
                         (str "gzip:" (js/btoa binary-string)))
                       (js/btoa code-with-helpers))
        url (js/URL. base-url)]
    (.. url -searchParams (set "src" encoded-code))
    (when boilerplate (.. url -searchParams (set "boilerplate" boilerplate)))
    (.. url -searchParams (set "repl" (str repl)))
    (.toString url)))

(defn squint-url
  "Compose Squint playground URL with AoC helpers."
  [code]
  (compose-playground-url code
                          {:playground :squint
                           :include-helpers? true}))

(defn cherry-url
  "Compose Cherry playground URL with AoC helpers."
  [code]
  (compose-playground-url code
                          {:playground :cherry
                           :include-helpers? true}))
