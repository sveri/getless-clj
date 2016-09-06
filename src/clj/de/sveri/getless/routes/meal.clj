(ns de.sveri.getless.routes.meal
  (:require [compojure.core :refer [routes GET POST]]
    ;[clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.spec-validation :as validation]
            [de.sveri.getless.service.spec-common :as spec-comm]
            [de.sveri.getless.service.meal :as s-meal]
            [de.sveri.getless.service.off :as s-off]
    ;[de.sveri.getless.service.off :as off]
            [compojure.response]
            [ring.util.response :refer [response redirect]]
            [clojure.spec :as s]))

(defn new-page [{:keys [session]}]
  (let [meal (s-meal/get-meal session)]
    (layout/render "meal/index.html" {:meal meal})))

(defn add-product [productid {:keys [session]} {:keys [off-url off-user off-password]}]
  (let [session (s-meal/add-product-to-meal session (s-off/get-by-id productid off-url off-user off-password))]
    (assoc (redirect "/meal/new") :session session)))


(s/fdef save-meal :args (s/cat :name string? :type ::s-meal/meal-type))
(defn save-meal [name type {:keys [session]}]
  (if (validation/validate-specs name ::spec-comm/required-name type ::s-meal/meal-type)
    (do
      (redirect "/meal/new"))))


(defn meal-routes [config]
  (routes
    (GET "/meal/new" req (new-page req))
    (POST "/meal/new" [name type :as req] (save-meal name type req))
    (GET "/meal/add/product/:productid" [productid :as req] (add-product productid req config))))
