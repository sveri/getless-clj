(ns de.sveri.getless.routes.off
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response]]))

(defn search-page [{:keys [off-url off-user off-password]} search only-one-locale]
  (let [only-one-locale? (= "on" only-one-locale)
        products (if-not (s/blank? search) (off/search-products search only-one-locale? off-url off-user off-password) {})]
    (layout/render "off/search.html" {:products (:products products) :search search})))

(defn off-routes [config]
  (routes (GET "/off/search" [search only-one-locale] (search-page config search only-one-locale))))
