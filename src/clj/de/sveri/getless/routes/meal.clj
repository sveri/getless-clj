(ns de.sveri.getless.routes.meal
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response]]))

(defn new-page []
  (layout/render "meal/new.html"))

(defn meal-routes [config]
  (routes
    (GET "/meal/new" [search] (new-page))))
