(ns mateuszmazurczak.application.qr-codes.input
  "QR codes input parsing for the application layer."
  (:require
   [clojure.string :as str]))

(defn parse-input
  "Parse input text into individual QR code values.
   Splits by newlines, trims whitespace, removes empty lines."
  [input]
  (when (string? input)
    (->> (str/split-lines input)
         (map str/trim)
         (remove str/blank?)
         vec)))
