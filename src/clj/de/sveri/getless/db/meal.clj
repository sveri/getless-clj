(ns de.sveri.getless.db.meal
  (:require [clojure.spec :as s]
            [de.sveri.getless.db.food :as db-food]
            [clojure.java.jdbc :as j]))


(s/def ::id number?)
(s/def ::users-id number?)
(s/def ::name string?)
(s/def ::products string?)

(s/def ::product-edn (s/keys :req-un [::db-food/product-id ::db-food/amount]))
(s/def ::products-edn (s/coll-of ::product-edn))

(s/def ::meal (s/keys :req-un [::name ::products-edn]
                      :opt-un [::id ::users-id]))
(s/def ::meals (s/coll-of ::meal))



;(s/fdef -by-user-id :args (s/cat :users-id number? :db any? :limit (s/? number?))
;        :ret ::foods)
;(defn meal-by-user-id [users-id db & [limit]]
;  (let [query (if limit ["select * from food where users_id = ? order by eaten_at desc limit ?" users-id limit]
;                        ["select * from food where users_id = ? order by eaten_at desc" users-id])]
;    (mapv #(assoc % :unit (-> % :unit str))
;          (j/query db query {:identifiers #(.replace % \_ \-)}))))


(s/fdef insert-meal :args (s/cat :db any? :users-id number? :name string? :products ::products-edn))

(defn insert-meal [db users-id name products]
  (j/insert! db :meal {:users_id users-id :name name :products (.toString products)}))



;(def breakfast "BREAKFAST")
;(def launch "LAUNCH")
;(def supper "SUPPER")
;(def snack "SNACK")
;
;(s/def ::breakfast #(= % breakfast))
;(s/def ::launch #(= % launch))
;(s/def ::supper #(= % supper))
;(s/def ::snack #(= % snack))
;(s/def ::meal-type (s/or :snack ::snack :supper ::supper :launch ::launch :breakfast ::breakfast))
;
;
;(s/def ::meal (s/keys :req [::meal-type]))
