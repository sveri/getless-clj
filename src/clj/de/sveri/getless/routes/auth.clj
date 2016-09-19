(ns de.sveri.getless.routes.auth
  (:require [compojure.core :refer [routes GET]]
            [buddy.sign.jwt :as jwt]
            [de.sveri.getless.db.user :as db-u]
            [ring.util.response :refer [response status]]
            [buddy.hashers :as hashers]
            [de.sveri.getless.db.user :as db]))

(defn login-handler
  [config username password]
  ;(clojure.pprint/pprint req)
  (if-let [user (db/get-user-by-email username)]
    (cond
      (or (= 0 (:is_active user)) (= false (:is_active user))) (status (response {:error "Unauthorized"}) 401)
      (= false (hashers/check password (get user :pass ""))) (status (response {:error "Unauthorized"}) 401)
      :else (response {:token (jwt/sign {:user (:id user)} (:jwt-secret config))}))
    (status (response {:error "Unauthorized"}) 401)))

(defn auth-routes [config]
      (routes
        (GET "/api/login" req (login-handler config (-> req :headers (get "username")) (-> req :headers (get "password"))))))
