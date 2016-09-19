(ns de.sveri.getless.routes.rest
  (:require [compojure.core :refer [routes GET]]
            [buddy.sign.jwt :as jwt]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [response]]))

(defn login-handler
  [config req]
  (response {:success :success}))
;(if-not (authenticated? req)
;  (throw-unauthorized)
;  (response {:success :success}))
    ;{:status  200
    ; :body    {:token token}
    ; :headers {:content-type "application/json"}}))

(defn rest-routes [config]
      (routes
        (GET "/api/weight" req (login-handler config req))))
