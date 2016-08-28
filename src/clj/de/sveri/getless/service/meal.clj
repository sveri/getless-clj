(ns de.sveri.getless.service.meal
  (:require [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(def breakfast "BREAKFAST")
(def launch "LAUNCH")
(def supper "SUPPER")
(def snack "SNACK")

(s/def ::breakfast #(= % breakfast))
(s/def ::launch #(= % launch))
(s/def ::supper #(= % supper))
(s/def ::snack #(= % snack))
(s/def ::meal-type (s/or :snack ::snack :supper ::supper :launch ::launch :breakfast ::breakfast))

(s/def ::session-map (s/keys))


(s/fdef save-new-meal :args (s/cat :session ::session-map :breakfast-type (s/? ::meal-type))
        :ret any?)
(defn save-new-meal [session & [breakfast-type]]
  (assoc-in session [:meal :type] (or breakfast-type breakfast)))


(defn get-meal [session]
  (get session :meal {}))

(stest/instrument)