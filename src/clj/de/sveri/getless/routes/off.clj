(ns de.sveri.getless.routes.off
  (:require [compojure.core :refer [routes GET]]
            [clojure.string :as s]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.off :as off]
            [ring.util.response :refer [response]]))

(defn search-page [{:keys [off-url off-user off-password]} search only-one-locale {:keys [localize]}]
  (let [only-one-locale? (= "on" only-one-locale)
        products (if-not (s/blank? search)
                   (off/search-products search only-one-locale? off-url off-user off-password)
                   {})
        _ (clojure.pprint/pprint (:products products))
        products-with-nutriments (mapv #(off/add-nutriments % localize off/nutriments-to-extract) (:products products))]
    (layout/render "off/search.html" {:products products-with-nutriments :search search})))

(defn off-routes [config]
  (routes (GET "/off/search" [search only-one-locale :as req] (search-page config search only-one-locale req))))
