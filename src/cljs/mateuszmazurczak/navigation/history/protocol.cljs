(ns mateuszmazurczak.navigation.history.protocol
  "Port for history management of the browser
  This is based on [push-state](https://developer.mozilla.org/en-US/docs/Web/API/History/pushState) browser feature.
  This feature is useful to add links in the browsers history")

(defprotocol History
  (init! [this]
   "After this method is called, all clicks are listened at and tracked\nParams:\n * none")
  (stop! [this]
   "Stop the tracking of click to include history\nParams:\n* none")
  (navigate! [this page]
   "Navigate to the page")
  (href-delta [this match name path-params query-params]
   "Hypertext reference to a path built from the current match, all others attributes my be nil, all other values are appended\nParams:\n* `match` is the starting point\n* `name` if the name is supplied, it will replace the current panel\n* `path-params` that map is merged to the current parameters\n* `query-params` that map is merged to the current parameters")
  (href [this name path-params query-params]
   "Hypertext reference to the path given as a parameter\nParams:\n* `path-params` parameters to fill the path\n* `query-params` are the parameters to set in the query"))
