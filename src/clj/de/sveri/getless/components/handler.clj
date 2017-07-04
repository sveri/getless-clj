(ns de.sveri.getless.components.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.route :as route]
            [com.stuartsierra.component :as comp]
            [de.sveri.getless.routes.home :refer [home-routes]]
            [de.sveri.getless.routes.finance :refer [finance-routes]]
            [de.sveri.getless.routes.off :refer [off-routes]]
            [de.sveri.getless.routes.weight :refer [weight-routes]]
            [de.sveri.getless.routes.auth :refer [auth-routes]]
            [de.sveri.getless.routes.rest :refer [rest-routes]]
            [de.sveri.getless.routes.food :refer [food-routes]]
            [de.sveri.getless.routes.habit :refer [habit-routes]]
            [de.sveri.getless.routes.activity :refer [activity-routes]]
            [de.sveri.getless.routes.cc :refer [cc-routes]]
            [de.sveri.getless.routes.user :refer [user-routes registration-routes]]
            [de.sveri.getless.middleware :refer [load-middleware]]
            [de.sveri.getless.service.config :as s-c]
            [de.sveri.getless.service.auth :as s-auth]
            [buddy.auth.backends :as backends]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 15 60 30)
   :timeout-response (redirect "/")})

(defn- mk-defaults
       "set to true to enable XSS protection"
       [xss-protection?]
       (-> site-defaults
           (update-in [:session] merge session-defaults)
           (assoc-in [:security :anti-forgery] xss-protection?)))


(def secret (:jwt-secret (s-c/prod-conf-or-dev)))
(def jws-backend (backends/jws {:secret secret}))

(defn get-handler [config db]
  (routes
    (-> (auth-routes config db)
        (wrap-routes wrap-restful-format :formats [:json-kw]))
    (-> (rest-routes config db)
        (wrap-routes wrap-restful-format :formats [:json-kw])
        (wrap-routes wrap-authentication jws-backend)
        (wrap-routes wrap-authorization jws-backend)
        (wrap-routes wrap-access-rules {:rules s-auth/rest-rules}))
    (-> (app-handler
          (into [] (concat (when (:registration-allowed? config) [(registration-routes config db)])
                         ;; add your application routes here
                         [(cc-routes config) (weight-routes config db) (off-routes config) home-routes
                          (user-routes config db) (food-routes config db) (habit-routes config db)
                          (finance-routes)
                          (activity-routes config db) base-routes]))
          ;; add custom middleware here
          :middleware (load-middleware config)
          :ring-defaults (mk-defaults false)
          ;; add access rules here
          :access-rules []
          ;; serialize/deserialize the following data formats
          ;; available formats:
          ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
          :formats [:json-kw :edn :transit-json])
        ; Makes static assets in $PROJECT_DIR/resources/public/ available.
        (wrap-file "resources")
        ; Content-Type, Content-Length, and Last Modified headers for files in body
        (wrap-file-info))))

(defrecord Handler [config db]
  comp/Lifecycle
  (start [comp]
    (assoc comp :handler (get-handler (:config config) (:db db))))
  (stop [comp]
    (assoc comp :handler nil)))

(defn new-handler []
  (map->Handler {}))
