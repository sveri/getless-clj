(ns de.sveri.getless.routes.off
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response]]))

(defn ->referer [req]
  (let [referer (-> req :headers (get "referer" ""))]
    (cond
      (.contains referer "food") "food"
      (.contains referer "meal") "meal")))

(defn search-page [{:keys [off-url off-user off-password]} search referer req]
  (let [products (if-not (s/blank? search) (off/search-products search off-url off-user off-password) {})
        referer (or referer (->referer req))]
    (layout/render "off/search.html" {:products (:products products) :search search :referer referer})))

(defn off-routes [config]
  (routes (GET "/off/search" [search referer :as req] (search-page config search referer req))))
