(ns de.sveri.getless.routes.food
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.food :as s-food]
            [de.sveri.getless.service.off :as s-off]))


(defn add-food-page [req]
  (layout/render "food/add-food.html"))

(defn add-product [productid {:keys [session]} {:keys [off-url off-user off-password]}]
  (let [session (s-food/add-food-to-session session (s-off/get-by-id productid off-url off-user off-password))]
    (assoc (redirect "/meal/add") :session session)))

(defn food-routes [config]
  (routes
    (GET "/food/add" req (add-food-page req))
    (GET "/food/add/product/:productid" [productid :as req] (add-product productid req config))))
