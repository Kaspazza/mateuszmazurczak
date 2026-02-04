(ns mateuszmazurczak.utils.text
  (:require
   [clojure.string :as str]))

(defn- split-long-word
  "Split a word that exceeds max length into chunks.
   Used for URLs and other long strings without spaces."
  [word max-len]
  (if (<= (count word) max-len)
    [word]
    (loop [remaining word
           result []]
      (if (empty? remaining)
        result
        (let [chunk (subs remaining 0 (min max-len (count remaining)))
              rest-str (subs remaining (min max-len (count remaining)))]
          (recur rest-str (conj result chunk)))))))

(defn wrap-text
  "Wrap text into multiple lines based on character limit.
   Handles both spaced text and long strings without spaces (like URLs).
   Returns vector of text lines."
  [text max-chars-per-line]
  (if (<= (count text) max-chars-per-line)
    [text]
    (let [;; Split by whitespace, keeping track of segments
          segments (str/split text #"\s+")
          ;; Process each segment, splitting long ones
          process-segment (fn [segment]
                            (if (<= (count segment) max-chars-per-line)
                              [segment]
                              (split-long-word segment max-chars-per-line)))
          ;; Flatten all segments into words/chunks
          all-chunks (mapcat process-segment segments)
          ;; Build lines by combining chunks
          lines (reduce (fn [acc chunk]
                          (let [current-line (or (last acc) "")
                                separator (if (empty? current-line) "" " ")
                                new-line (str current-line separator chunk)]
                            (if (<= (count new-line) max-chars-per-line)
                              (if (empty? acc) [new-line] (conj (vec (butlast acc)) new-line))
                              (conj acc chunk))))
                        []
                        all-chunks)]
      (vec (remove str/blank? lines)))))
