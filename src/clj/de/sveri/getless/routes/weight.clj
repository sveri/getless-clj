(ns de.sveri.getless.routes.weight
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.user :as s-u]
            [de.sveri.getless.service.weight :as s-w]
            [clojure.instant :as inst]
            [de.sveri.getless.service.spec-validation :as validation]
            [clojure.string :as str]
            [de.sveri.getless.service.food :as s-food]))

(def default-last-x-days 2)

(defn weight-page [_ db {:keys [off-url off-user off-password]}]
  (let [
        weights-and-nutriments (s-w/merge-weights-and-nutriments
                                 (db-w/get-weights db (s-u/get-logged-in-user-id db) default-last-x-days)
                                 (->> (s-food/->foods-with-product-grouped-by-date db off-url off-user off-password default-last-x-days)
                                      s-food/->nutriments-grouped-by-date))


        weights-map (s-w/format-weighted-at (db-w/get-weights db (s-u/get-logged-in-user-id db) default-last-x-days)
                                            s-w/weight-date-format)
        data-map (s-w/format-weighted-at weights-and-nutriments s-w/weight-date-format)]
    (layout/render "weight/index.html"
                   {:weights    (s-w/weight->js-string :weight weights-map)
                    :dates      (s-w/weight->js-string :weighted_at weights-map)
                    :sugars (->> (s-food/->foods-with-product-grouped-by-date db off-url off-user off-password default-last-x-days)
                                 s-food/->nutriments-grouped-by-date)})))
                    ;:sugars     (-> (s-w/match-weights-dates-with-sugars-nutriments weights-map
                    ;                  (->> (s-food/->foods-with-product-grouped-by-date db off-url off-user off-password)
                    ;                       s-food/->nutriments-grouped-by-date)))})))

                                     ;(mapv :sugars_100g)
                                     ;(mapv str)))})))



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


(defn weight-routes [config db]
  (routes
    (GET "/weight" req (weight-page req db config))
    (POST "/weight/add" [date weight] (add date weight db))))

