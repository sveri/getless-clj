(ns de.sveri.getless.routes.finance
  (:require [compojure.core :refer [routes GET]]
            [de.sveri.getless.layout :as layout]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]))

;(defn home-page [{:keys [localize]}]
;  (layout/render "home/index.html" {:rules [(localize [:home/we-dont-judge])
;                                            (localize [:home/we-support-each-other])
;                                            (localize [:home/we-use-means])]}))

(defn links-page []
  (layout/render "finance/links.html"))

(defn finance-routes []
  (routes (GET "/finance/links" [] (links-page))))
          ;(GET "/" req (home-page req))))
