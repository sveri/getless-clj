(ns de.sveri.getless.routes.rest
  (:require [compojure.core :refer [routes GET]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response]]
            [de.sveri.getless.db.weight :as db-w]))

(defn get-weights [req]
  (let [user-id (-> req :identity :user-id)
        weights (db-w/get-weights user-id)
        weights-cleaned (mapv #(dissoc % :id :users_id) weights)]
    (response weights-cleaned)))

(defn rest-routes [_]
  (routes
    (GET "/api/weight" req (get-weights req))))
