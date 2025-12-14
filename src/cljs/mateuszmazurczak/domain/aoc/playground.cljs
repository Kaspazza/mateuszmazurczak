(ns mateuszmazurczak.domain.aoc.playground
  "Pure data transformations for AoC playground integration.
   
   Handles compression and URL composition for opening code in
   Squint or Cherry playgrounds."
  (:require
   ["pako" :as pako]))

(def ^:private aoc-helper-comment
  ";; Helper functions:
;; (fetch-input year day) - get AOC input
;; (append str) - append str to DOM
;; (spy x) - log x to console and return x

;; Example fetch call.
;;(def input (->> (js-await (fetch-input 2022 1))
;;             #_spy
;;             str/split-lines
;;             (mapv parse-long)))

")

(def ^:private aoc-boilerplate-url
  "URL to AoC helper functions boilerplate."
  "https://gist.githubusercontent.com/borkdude/cf94b492d948f7f418aa81ba54f428ff/raw/3b58a80710fbbbda091966c8eb85323eef4652c1/aoc_ui.cljs")

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
     - :boilerplate - URL to boilerplate code, default aoc-boilerplate-url
     - :repl - Enable REPL mode (default: true)
     - :include-helpers? - Include AoC helper comment (default: true)
   
   Returns: Complete playground URL"
  [code
   {:keys [playground boilerplate repl include-helpers?]
    :or {playground :squint
         boilerplate aoc-boilerplate-url
         repl true
         include-helpers? true}
    :as _opts}]
  (let [base-url (case playground
                   :cherry "https://squint-cljs.github.io/cherry/"
                   :squint "https://squint-cljs.github.io/squint/")
        code-with-helpers (if include-helpers? (str aoc-helper-comment code) code)
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
