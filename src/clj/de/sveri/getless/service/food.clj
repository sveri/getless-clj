(ns de.sveri.getless.service.food
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.session :as sess]
            [de.sveri.getless.db.food :as db-food]
            [de.sveri.getless.service.off :as s-off]
            [de.sveri.getless.service.user :as s-user])
  (:import (java.text SimpleDateFormat)))

(s/fdef get-food-from-session :args (s/cat :session ::sess/session-map))
(defn get-food-from-session [session]
  (get-in session [:getless :food :products] {}))

(s/fdef add-food-to-session :args (s/cat :session ::sess/session-map :product ::s-off/product) :ret ::sess/session-map)
(defn add-food-to-session [session product]
  (update-in session [:getless :food :products] conj product))


(s/fdef foods->group-by-date :args (s/cat :foods ::db-food/foods) :ret (s/coll-of (s/coll-of ::db-food/foods)))
(defn foods->group-by-date [foods]
  (map (fn [[_ s]] s)
       (seq
         (group-by
           (fn [{:keys [:eaten-at]}]
             (.format (SimpleDateFormat. "yyyyMMdd") eaten-at))
           foods))))

(defn delete-food-from-session [session]
  (update-in session [:getless :food] dissoc :products))


(defn ->foods-with-product-grouped-by-date
  [db off-url off-user off-password]
  (foods->group-by-date
    (s-off/add-product
      (db-food/->food-by-user-id
        db (s-user/get-logged-in-user-id db))
      off-url off-user off-password)))


(defn ->nutriments-grouped-by-date [])
