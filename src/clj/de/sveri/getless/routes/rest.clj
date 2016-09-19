(ns de.sveri.getless.routes.rest
  (:require [compojure.core :refer [routes GET]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response]]
            [de.sveri.getless.db.weight :as db-w]))

(defn login-handler [req]
  (let [user-id (-> req :identity :user-id)
        weights (db-w/get-weights user-id)]
    (response weights)))

(defn rest-routes [_]
  (routes
    (GET "/api/weight" req (login-handler req))))
