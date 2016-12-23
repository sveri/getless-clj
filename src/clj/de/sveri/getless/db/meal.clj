(ns de.sveri.getless.db.meal
  (:require [clojure.spec :as s]
            [de.sveri.getless.db.food :as db-food]))


(s/def ::id number?)
(s/def ::users-id number?)
(s/def ::name string?)
(s/def ::products string?)


(s/def ::products-edn (s/coll-of (s/keys :req-un [::db-food/product-id ::db-food/amount])))

(s/def ::meal (s/keys :req-un [::name ::products-edn]
                      :opt-un [::id ::users-id]))
(s/def ::meals (s/coll-of ::meal))






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
