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

(defn weight-page [_ db]
  (let [weights-map (s-w/format-weighted-at (db-w/get-weights db (s-u/get-logged-in-user-id db))
                                            s-w/weight-date-format)]
    (layout/render "weight/index.html" {:weights (s-w/weight->js-string :weight weights-map)
                                        :dates   (s-w/weight->js-string :weighted_at weights-map)})))


(defn add [date weight db]
  (let [date-date (.getTime (inst/read-instant-date date))
        weight-weight (read-string weight)
        validation-result (validation/validate-specs date-date ::db-w/weight
                                                     weight-weight ::db-w/weight)]
    (if (str/blank? validation-result)
      (do (db-w/save-weight db weight-weight date-date (s-u/get-logged-in-user-id db))
          (redirect "/weight"))
      (do (layout/flash-result "error" "alert-error")
          (assoc (redirect "/weight") :flash validation-result)))))


(defn weight-routes [_ db]
  (routes
    (GET "/weight" req (weight-page req db))
    (POST "/weight/add" [date weight] (add date weight db))))

