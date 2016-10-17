(ns de.sveri.getless.db.food
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s]
            [de.sveri.getless.service.off :as s-off])
  (:import (java.sql Timestamp)))

(s/def ::users-id number?)
(s/def ::product-id number?)
(s/def ::eaten-at inst?)
(s/def ::food (s/keys :req-un [::eaten-at ::product-id ::users-id]))
(s/def ::foods (s/coll-of ::food))

(s/fdef ->food-by-user-id :args (s/cat :db any? :users-id number?) :ret ::foods)
(defn ->food-by-user-id [db users-id]
  (j/query db ["select * from food where users_id = ? order by eaten_at asc" users-id]
              {:identifiers #(.replace % \_ \-)}))

(s/fdef insert-food :args (s/cat :db any? :date number? :users-id number? :products ::s-off/products))
(defn insert-food [db date users-id products]
  (let [sql-timestamp (new Timestamp date)
        rows (mapv (fn [p] {:users_id users-id :eaten_at sql-timestamp :product_id (read-string (:id p))}) products)]
    (j/insert-multi! db :food rows)))

;(defn update-user [db id fields]
;  (j/update! db :users fields ["id = ?" id]))
;
;(defn delete-user [db id] (j/delete! db :users ["id = ?" id]))
;
;(defn change-password [db email pw] (j/update! db :users {:pass pw} ["email = ?" email]))
