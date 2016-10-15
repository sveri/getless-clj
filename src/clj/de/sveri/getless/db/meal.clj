(ns de.sveri.getless.db.meal
  (:require [clojure.spec :as s]))



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
