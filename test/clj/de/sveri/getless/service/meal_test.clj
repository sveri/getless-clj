(ns de.sveri.getless.service.meal-test
  (:require [clojure.test :refer :all]
    [de.sveri.getless.service.meal :as meal]
    [clojure.spec.test :as stest]))


(deftest save-meal-in-session
  (meal/save-new-meal)
  (let [meal (meal/get-meal)]
    (is (= "BREAKFAST" (:type meal)))))

(stest/instrument)

