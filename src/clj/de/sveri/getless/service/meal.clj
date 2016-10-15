(ns de.sveri.getless.service.meal
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.session :as sess]
            [de.sveri.getless.db.meal :as db-meal]
            [de.sveri.getless.service.off :as s-off]))






(s/fdef save-new-meal :args (s/cat :session ::sess/session-map :breakfast-type (s/? ::db-meal/meal-type))
        :ret ::sess/session-map)
(defn save-new-meal [session & [breakfast-type]]
  (assoc-in session [:getless :meal :type] (or breakfast-type db-meal/breakfast)))


(s/fdef get-meal :args (s/cat :session ::sess/session-map) :ret ::db-meal/meal)
(defn get-meal [session]
  (get-in session [:getless :meal] {}))

(s/fdef add-product-to-meal :args (s/cat :session ::sess/session-map :product ::s-off/product) :ret ::sess/session-map)
(defn add-product-to-meal [session product]
  (update-in session [:getless :meal :products] conj product))

