(ns de.sveri.getless.service.spec-validation-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test :as stest]
            [de.sveri.getless.service.spec-validation :as sv]
            [de.sveri.getless.service.spec-common :as s-comm]
            [de.sveri.getless.service.meal :as s-meal]
            [de.sveri.getless.service.weight :as sw]
            [clojure.instant :as ci]))


(deftest validate-empty-string
         (is (= "\"required-name\" should not be empty." (sv/validate "" ::s-comm/required-name)))
         (is (= nil (sv/validate "name" ::s-comm/required-name))))

(deftest validate-meal-type
  (is (= nil (sv/validate "BREAKFAST" ::s-meal/meal-type)))
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"" (sv/validate "" ::s-meal/meal-type))))

(deftest validate-two-specs
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"\n\"required-name\" should not be empty."
         (sv/validate-specs "" ::s-meal/meal-type "" ::s-comm/required-name))))

(deftest date-param
  (is (nil? (sv/validate (ci/read-instant-date "2016-09-03") ::sw/weighted_at)))
  (is (nil? (sv/validate (read-string "99") ::sw/weight)))
  (is (= "" (sv/validate-specs (ci/read-instant-date "2016-09-03") ::sw/weighted_at (read-string "99") ::sw/weight))))



(stest/instrument)