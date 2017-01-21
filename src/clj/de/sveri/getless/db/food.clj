(ns de.sveri.getless.db.food
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s]
            [clj-time.format :as time-format])
  (:import (java.sql Timestamp)
           (java.text SimpleDateFormat)
           (org.joda.time DateTime)))

(s/def ::id number?)
(s/def ::users-id number?)
(s/def ::product-id number?)
(s/def ::eaten-at inst?)
(s/def ::amount number?)
(s/def ::amounts (s/coll-of ::amount))
(s/def ::food (s/keys :req-un [::eaten-at ::product-id ::users-id]
                      :opt-un [::id]))
(s/def ::foods (s/coll-of ::food))
(s/def ::unit #{"gramm" "liter"})

(s/fdef ->food-by-user-id :args (s/cat :users-id number? :db any? :limit (s/? number?))
        :ret ::foods)
(defn ->food-by-user-id [users-id db & [limit]]
  (let [query (if limit ["select * from food where users_id = ? order by eaten_at desc limit ?" users-id limit]
                        ["select * from food where users_id = ? order by eaten_at desc" users-id])]
    (mapv #(assoc % :unit (-> % :unit str))
          (j/query db query {:identifiers #(.replace % \_ \-)}))))



(s/fdef food-by-user-and-date :args (s/cat :db any? :users-id number? :date #(instance? DateTime %))
        :ret ::foods)
(defn food-by-user-and-date [db users-id date]
  (let [date_formatted (time-format/unparse (time-format/formatter "yyyy-MM-dd") date)
        ;date_formatted (.format (SimpleDateFormat. "yyyy-MM-dd") date)
        query ["select * from food where users_id = ? AND eaten_at >= ?::date AND eaten_at < (?::date + '1 day'::interval)" users-id date_formatted date_formatted]]
    (mapv #(assoc % :unit (-> % :unit str))
          (j/query db query {:identifiers #(.replace % \_ \-)}))))


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
                                 :amounts ::amounts :units (s/coll-of ::unit)))
(defn insert-food [db date users-id products amounts units]
  (let [sql-timestamp (new Timestamp date)
        rows (vec (for [i (range 0 (count products))]
                    {:users_id users-id :eaten_at sql-timestamp :product_id (get products i) :amount (get amounts i)
                     :unit (-> (get units i) ->unit)}))]
    (j/insert-multi! db :food rows)))


(s/fdef delete-food :args (s/cat :db any? :productid number? :userid number?))
(defn delete-food [db productid userid]
  (j/delete! db :food ["id = ? and users_id = ?" productid userid]))


(s/fdef update-food :args (s/cat :db any? :date number? :users-id number? :products (s/coll-of number?)
                                 :amounts ::amounts :units (s/coll-of ::unit)))
(defn update-food [db date user-id products amounts units]
  (doseq [i (range 0 (count products))]
    (j/update! db :food {:eaten_at (new Timestamp date) :amount (get amounts i) :unit (-> (get units i) ->unit)}
               ["id = ? and users_id = ?" (get products i) user-id])))


