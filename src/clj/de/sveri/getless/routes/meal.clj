(ns de.sveri.getless.routes.meal
  (:require [compojure.core :refer [routes GET]]
            ;[clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.meal :as s-meal]
            [de.sveri.getless.service.off :as s-off]
            ;[de.sveri.getless.service.off :as off]
            [compojure.response]
            [ring.util.response :refer [response redirect]]))

(defn new-page [{:keys [session]}]
  (let [meal (s-meal/get-meal session)]
    (layout/render "meal/new.html" {:meal meal})))

(defn add-product [productid {:keys [session]} {:keys [off-url off-user off-password]}]
  (let [session (s-meal/add-product-to-meal session (s-off/get-by-id productid off-url off-user off-password))]
    (assoc (redirect "/meal/new") :session session)))

(defn meal-routes [config]
  (routes
    (GET "/meal/new" req (new-page req))
    (GET "/meal/add/product/:productid" [productid :as req] (add-product productid req config))))
