(ns mateuszmazurczak.tasks.css)

(defn tailwind-compile-cmd
  [css-file compiled-dir]
  ["npx" "@tailwindcss/cli" "-i" css-file "-o" compiled-dir])

(defn tailwind-watch-cmd
  [css-file compiled-dir]
  (conj (tailwind-compile-cmd css-file compiled-dir) "--watch"))

(defn tailwind-release-cmd
  [css-file compiled-dir]
  (conj (tailwind-compile-cmd css-file compiled-dir) "--minify"))
