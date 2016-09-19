(ns de.sveri.getless.routes.auth
  (:require [compojure.core :refer [routes POST]]
            [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response status]]
            [buddy.hashers :as hashers]
            [de.sveri.getless.db.user :as db]))

(defn login-handler
  [config username password]
  (if-let [user (db/get-user-by-email username)]
    (cond
      (or (= 0 (:is_active user)) (= false (:is_active user))) (status (response {:error "Unauthorized"}) 401)
      (= false (hashers/check password (get user :pass ""))) (status (response {:error "Unauthorized"}) 401)
      :else (response {:token (jwt/sign {:user-id (:id user)} (:jwt-secret config))}))
    (status (response {:error "Unauthorized"}) 401)))

(defn auth-routes [config]
  (routes
    (POST "/api/login" [username password] (login-handler config username password))))
