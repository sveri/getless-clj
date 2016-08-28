(ns de.sveri.getless.routes.meal
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            ;[de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response redirect]]))

(defn new-page []
  (layout/render "meal/new.html"))

(defn add-product [productid {:keys [session]}]
  (clojure.pprint/pprint session)
  (redirect "/meal/new"))

(defn meal-routes [config]
  (routes
    (GET "/meal/new" [] (new-page))
    (GET "/meal/add/product/:productid" [productid :as req] (add-product productid req))))
