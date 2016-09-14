(ns de.sveri.getless.routes.weight
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.user :as s-u]
            [de.sveri.getless.service.weight :as s-w]
            [clojure.instant :as inst]
            [de.sveri.getless.service.spec-validation :as validation]
            [clojure.string :as str]))

(defn weight-page [_]
  (let [weights-map (s-w/format-weighted-at (db-w/get-weights (s-u/get-logged-in-user-id))
                                            s-w/weight-date-format)]
    (layout/render "weight/index.html" {:weights (s-w/weight->js-string :weight weights-map)
                                        :dates   (s-w/weight->js-string :weighted_at weights-map)})))


(defn add [date weight req]
  (let [date-date (inst/read-instant-date date)
        weight-weight (read-string weight)
        validation-result (validation/validate-specs date-date ::s-w/weighted_at
                                                     weight-weight ::s-w/weight)]
    (if (str/blank? validation-result)
      (do (db-w/save-weight weight-weight date-date (s-u/get-logged-in-user-id))
          (redirect "/weight"))
      (assoc (redirect "/weight") :flash validation-result))))




(defn weight-routes [config]
  (routes
    (GET "/weight" req (weight-page req))
    (POST "/weight/add" [date weight :as req] (add date weight req))))

