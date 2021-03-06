(ns de.sveri.getless.routes.auth
  (:require [compojure.core :refer [routes POST]]
            [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response status]]
            [buddy.hashers :as hashers]
            [clojure.tools.logging :as log]
            [de.sveri.getless.db.user :as db]))


(defn login-handler
  [config username password db]
  (if-let [user (db/get-user-by-email db username)]
    (cond
      (or (= 0 (:is_active user)) (= false (:is_active user)))
      (do (log/info "Inactive user tried to access /api/login: " username)
          (status (response {:error "Unauthorized"}) 401))

      (= false (hashers/check password (get user :pass "")))
      (do (log/info "Wrong password tried to access /api/login: " username)
          (status (response {:error "Unauthorized"}) 401))

      :else (response {:token (jwt/sign {:user-id (:id user)} (:jwt-secret config))}))

    (do (log/info "Non existent user tried to access /api/login: " username)
        (status (response {:error "Unauthorized"}) 401))))

(defn auth-routes [config db]
  (routes
    (POST "/api/login" [username password] (login-handler config username password db))))
