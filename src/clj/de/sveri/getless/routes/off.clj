(ns de.sveri.getless.routes.off
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response]]))

(defn search-page []
  (layout/render "off/search.html"))

(defroutes off-routes
           (GET "/off/search" [] (search-page)))
