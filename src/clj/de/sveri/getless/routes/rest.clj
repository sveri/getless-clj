(ns de.sveri.getless.routes.rest
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response]]
            [de.sveri.getless.db.weight :as db-w]))

(defn get-weights [req db]
  (let [user-id (-> req :identity :user-id)
        weights (db-w/get-weights db user-id)
        weights-cleaned (mapv #(dissoc % :id :users_id) weights)]
    (response weights-cleaned)))

(defn add-weight [weight date req]
  (let [user-id (-> req :identity :user-id)]))
    ;(response weights-cleaned)))

(defn rest-routes [_ db]
  (routes
    (GET "/api/weight" req (get-weights req db))
    (POST "/api/weight" [weight date :as req] (add-weight weight date req))))
