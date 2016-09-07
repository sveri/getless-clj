(ns de.sveri.getless.routes.weight
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.user :as s-u]
            [clojure.spec :as s]))

(defn weight-page [_]
  (layout/render "weight/index.html" {:weights (db-w/get-weights (s-u/get-logged-in-user-id))}))


(defn add [date weight])


(defn weight-routes [config]
  (routes
    (GET "/weight" req (weight-page req))
    (POST "/weight/add" [date weight] (add date weight))))

