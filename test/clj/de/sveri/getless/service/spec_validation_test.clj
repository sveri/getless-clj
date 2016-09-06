(ns de.sveri.getless.service.spec-validation-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test :as stest]
            [de.sveri.getless.service.spec-validation :as sv]
            [de.sveri.getless.service.spec-common :as s-comm]
            [de.sveri.getless.service.meal :as s-meal]))


(deftest validate-empty-string
         (is (= "\"required-name\" should not be empty." (sv/validate "" ::s-comm/required-name)))
         (is (= nil (sv/validate "name" ::s-comm/required-name))))

(deftest validate-meal-type
  (is (= nil (sv/validate "BREAKFAST" ::s-meal/meal-type)))
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"" (sv/validate "" ::s-meal/meal-type))))

(deftest validate-two-specs
  (is (= "\"\" is not a valid type. Expecting: \"meal-type\"\n\"required-name\" should not be empty."
         (sv/validate-specs "" ::s-meal/meal-type "" ::s-comm/required-name))))



(stest/instrument)