(ns de.sveri.getless.service.spec-validation-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [de.sveri.getless.service.spec-validation :as sv]
            [de.sveri.getless.service.spec-common :as s-comm]
            [de.sveri.getless.db.meal :as db-meal]
            [de.sveri.getless.db.weight :as db-w]
            [clojure.instant :as ci]))


(deftest validate-empty-string
         (is (= "\"required-name\" should not be empty." (sv/validate "" ::s-comm/required-name)))
         (is (= nil (sv/validate "name" ::s-comm/required-name))))

(deftest validate-meal-type
  (is (= nil (sv/validate "BREAKFAST" ::db-meal/meal-type)))
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"" (sv/validate "" ::db-meal/meal-type))))

(deftest validate-two-specs
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"\n\"required-name\" should not be empty."
         (sv/validate-specs "" ::db-meal/meal-type "" ::s-comm/required-name))))

(deftest validate-numbers
  (is (= "\"50\" is not a number."
         (sv/validate "50" ::db-w/id))))

(deftest date-param
  (is (nil? (sv/validate (ci/read-instant-date "2016-09-03") ::db-w/weighted_at)))
  (is (nil? (sv/validate (read-string "99") ::db-w/weight)))
  (is (= "" (sv/validate-specs (ci/read-instant-date "2016-09-03") ::db-w/weighted_at (read-string "99") ::db-w/weight))))



(stest/instrument)