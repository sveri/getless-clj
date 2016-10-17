(ns de.sveri.getless.service.food
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.session :as sess]
            [de.sveri.getless.service.off :as s-off]))


(s/fdef get-food-from-session :args (s/cat :session ::sess/session-map))
(defn get-food-from-session [session]
  (get-in session [:getless :food :products] {}))

(s/fdef add-food-to-session :args (s/cat :session ::sess/session-map :product ::s-off/product) :ret ::sess/session-map)
(defn add-food-to-session [session product]
  (update-in session [:getless :food :products] conj product))

