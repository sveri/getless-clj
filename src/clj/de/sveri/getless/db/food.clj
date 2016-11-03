(ns de.sveri.getless.db.food
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s]
            [de.sveri.getless.service.off :as s-off])
  (:import (java.sql Timestamp)))

(s/def ::id number?)
(s/def ::users-id number?)
(s/def ::product-id number?)
(s/def ::eaten-at inst?)
(s/def ::food (s/keys :req-un [::eaten-at ::product-id ::users-id]
                      :opt-un [::id]))
(s/def ::foods (s/coll-of ::food))
(s/def ::unit #{"gramm" "liter"})

(s/fdef ->food-by-user-id :args (s/cat :users-id number? :db any?)  :ret ::foods)
(defn ->food-by-user-id [users-id db]
  (mapv #(assoc % :unit (-> % :unit str))
    (j/query db ["select * from food where users_id = ? order by eaten_at desc" users-id]
                {:identifiers #(.replace % \_ \-)})))




(defn enum->pg-enum
  "Convert a keyword value into an enum-compatible object."
  [enum-type enum]
  (doto (org.postgresql.util.PGobject.)
    (.setType enum-type)
    (.setValue enum)))

(def ->unit
  "Convert a unit into a unit_enum enum object"
  (partial enum->pg-enum "unit_enum"))


(s/fdef insert-food :args (s/cat :db any? :date number? :users-id number? :products (s/coll-of number?)
                                 :amounts (s/coll-of number?) :units (s/coll-of ::unit)))
(defn insert-food [db date users-id products amounts units]
  (let [sql-timestamp (new Timestamp date)
        rows (vec (for [i (range 0 (count products))]
                    {:users_id users-id :eaten_at sql-timestamp :product_id (get products i) :amount (get amounts i)
                     :unit (-> (get units i) ->unit)}))]
    (j/insert-multi! db :food rows)))

