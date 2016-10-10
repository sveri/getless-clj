(ns de.sveri.getless.routes.rest
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response status]]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.spec-validation :as validation]
            [clojure.string :as str]))


(defn get-weights [req db]
  (let [user-id (-> req :identity :user-id)
        weights (db-w/get-weights db user-id)
        weights-cleaned (mapv #(dissoc % :id :users_id) weights)]
    (response weights-cleaned)))

(defn add-weight [weight date req db]
  (let [user-id (-> req :identity :user-id)
        validation-result (validation/validate-specs weight ::db-w/weight date ::db-w/weight)]
    (if (str/blank? validation-result)
      (response (first (db-w/save-weight db weight (long date) user-id)))
      (status (response {:error "Something failed writing into database" :validation validation-result}) 500))))

(defn rest-routes [_ db]
  (routes
    (GET "/api/weight" req (get-weights req db))
    (POST "/api/weight" [weight date :as req] (add-weight weight date req db))))
