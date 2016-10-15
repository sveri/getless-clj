(ns de.sveri.getless.service.food
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.session :as sess]
            [de.sveri.getless.service.off :as s-off]))


;(s/fdef save-new-meal :args (s/cat :session ::session-map :breakfast-type (s/? ::meal-type))
;        :ret ::session-map)
;(defn save-new-meal [session & [breakfast-type]]
;  (assoc-in session [:getless :meal :type] (or breakfast-type breakfast)))


(s/fdef get-food-from-session :args (s/cat :session ::sess/session-map))
(defn get-food-from-session [session]
  (get-in session [:getless :food] {}))

(s/fdef add-food-to-session :args (s/cat :session ::sess/session-map :product ::s-off/product) :ret ::sess/session-map)
(defn add-food-to-session [session product]
  (update-in session [:getless :food :products] conj product))

