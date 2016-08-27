(ns de.sveri.getless.routes.off
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response]]))

(defn search-page [{:keys [off-url off-user off-password]} search]
  (let [products (if-not (s/blank? search) (off/search-products search off-url off-user off-password) {})]
    (layout/render "off/search.html" {:products (:products products) :search search})))

(defn off-routes [config]
  (routes (GET "/off/search" [search] (search-page config search))))
