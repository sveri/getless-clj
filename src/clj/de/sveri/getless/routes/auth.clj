(ns de.sveri.getless.routes.auth
  (:require [compojure.core :refer [routes GET]]
            [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response]]))

(defn login-handler
  [config username password]
  (let [
        ;data (:form-params request)
        ;user (find-user (:username data)   ;; (implementation ommited)
        ;                (:password data))
        ;token (jwt/sign {:user (:id user)} secret)]
        token (jwt/sign {:user 1} (:jwt-secret config))]
    (response {:token token})))
    ;{:status  200
    ; :body    {:token token}
    ; :headers {:content-type "application/json"}}))

(defn auth-routes [config]
      (routes
        (GET "/rest/login" [username password] (login-handler config username password))))
