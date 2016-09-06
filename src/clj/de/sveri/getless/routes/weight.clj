(ns de.sveri.getless.routes.weight
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [clojure.spec :as s]))

(defn weight-page [{:keys [session]}]
  (layout/render "weight/index.html"))


(defn add [date weight])


(defn weight-routes [config]
  (routes
    (GET "/weight" req (weight-page req))
    (POST "/weight/add" [date weight] (add date weight))))

