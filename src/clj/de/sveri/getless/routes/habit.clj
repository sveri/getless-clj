(ns de.sveri.getless.routes.habit
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]))

(defn index-page [db]
  (layout/render "habit/index.html"))

(defn habit-routes [config db]
  (routes
    (GET "/habit" [] (index-page db))))
