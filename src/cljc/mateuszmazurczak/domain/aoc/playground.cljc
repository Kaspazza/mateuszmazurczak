(ns mateuszmazurczak.domain.aoc.playground
  "Domain knowledge about AoC playground integrations.
   
   Pure data: service URLs, helper code, parameter structure.
   No platform dependencies.")

;; =============================================================================
;; Playground URLs
;; =============================================================================

(def squint-url
  "Base URL for Squint playground."
  "https://squint-cljs.github.io/squint/")

(def cherry-url
  "Base URL for Cherry playground."
  "https://squint-cljs.github.io/cherry/")

;; =============================================================================
;; AoC Helper Code
;; =============================================================================

(def helper-comment
  "Helper comment/boilerplate for AoC solutions in playground."
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

(def boilerplate-url
  "URL to AoC helper functions boilerplate."
  "https://gist.githubusercontent.com/borkdude/cf94b492d948f7f418aa81ba54f428ff/raw/3b58a80710fbbbda091966c8eb85323eef4652c1/aoc_ui.cljs")

;; =============================================================================
;; Display Logic
;; =============================================================================

(defn should-show-playground?
  "Determine if playground links should be shown for a solution.
   
   Only code snippets can be opened in playground (not repository links)."
  [content-type]
  (= content-type :code-snippet))
