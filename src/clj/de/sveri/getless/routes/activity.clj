(ns de.sveri.getless.routes.activity
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]))

(defn index-page [db]
  (layout/render "activity/index.html"))

(defn activity-routes [config db]
  (routes
    (GET "/activity" [] (index-page db))))
