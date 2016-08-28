(ns de.sveri.getless.service.meal-test
  (:require [clojure.test :refer :all]
    [de.sveri.getless.service.meal :as meal]
    [clojure.spec.test :as stest]))


(deftest save-meal-in-session
  (let [session (meal/save-new-meal {})
        meal (meal/get-meal session)]
    (is (= "BREAKFAST" (:type meal)))))

(stest/instrument)

