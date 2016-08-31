(ns de.sveri.getless.service.meal
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.off :as s-off]))

(def breakfast "BREAKFAST")
(def launch "LAUNCH")
(def supper "SUPPER")
(def snack "SNACK")

(s/def ::breakfast #(= % breakfast))
(s/def ::launch #(= % launch))
(s/def ::supper #(= % supper))
(s/def ::snack #(= % snack))
(s/def ::meal-type (s/or :snack ::snack :supper ::supper :launch ::launch :breakfast ::breakfast))


(s/def ::meal (s/keys :req [::meal-type]))

(s/def ::getless (s/keys :opt [::meal]))

(s/def ::session-map (s/keys :opt [::getless]))


(s/fdef save-new-meal :args (s/cat :session ::session-map :breakfast-type (s/? ::meal-type))
        :ret ::session-map)
(defn save-new-meal [session & [breakfast-type]]
  (assoc-in session [:getless :meal :type] (or breakfast-type breakfast)))


(s/fdef get-meal :args (s/cat :session ::session-map) :ret ::meal)
(defn get-meal [session]
  (get-in session [:getless :meal] {}))

(s/fdef add-product-to-meal :args (s/cat :session ::session-map :product ::s-off/product) :ret ::session-map)
(defn add-product-to-meal [session product]
  (update-in session [:getless :meal :products] conj product))