(ns mateuszmazurczak.endpoint.routes
  (:require
   [mateuszmazurczak.env    :as mm-env]
   [mateuszmazurczak.routes :as mm-routes]))

(defn update-kw
  "Update the keywords `kws` in map `m` with function `f`"
  [m kws f]
  (reduce (fn [m k]
            (if (contains? m k) (let [v (get m k)] (assoc m k (f v))) m))
          m
          kws))

(defn- kw-to-registry-value
  [registry val]
  (if (keyword? val) (get registry val) val))

(defn- update-registry
  [handler-registry handler-map]
  (update-kw handler-map
             [:get :head :patch :delete :options :post :put :trace]
             (partial kw-to-registry-value handler-registry)))

(defn parse-routes
  "Parse the routes to return the frontend or backend
  Params:
  * `kw` Is `:fe` or `:be`, to respectively return frontend or backend data
  * `routes` routes to analyze
  * `handler-registry` transform a keyword into a handler"
  ([router-kw routes] (parse-routes router-kw routes {}))
  ([router-kw routes handler-registry]
   (let [selected-route-data (get routes router-kw)]
     (cond
       (string? routes) routes
       (and (map? routes) (map? selected-route-data))
       (merge (select-keys routes [:name])
              (update-registry handler-registry selected-route-data))
       (map? routes) (kw-to-registry-value handler-registry selected-route-data)
       (vector? routes) (mapv #(parse-routes router-kw % handler-registry)
                              routes)))))

(defn web-routes
  "Routes for mateuszmazurczak,
  `mateuszmazurczak.routes` contains both backa and frontend routes,
  Here, the routes are parsed and the specific routes for local environment are added
  Params:
  * `registry` "
  [registry]
  (conj (parse-routes :be mm-routes/routes registry) (mm-env/route)))
