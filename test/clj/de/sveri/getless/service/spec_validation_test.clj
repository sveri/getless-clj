(ns de.sveri.getless.service.spec-validation-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test :as stest]
            [de.sveri.getless.service.spec-validation :as sv]
            [de.sveri.getless.service.spec-common :as s-comm]))


(deftest validate-empty-string
         (is (= "\"required-name\" should not be empty." (sv/validate "" ::s-comm/required-name)))
         (is (= nil (sv/validate "name" ::s-comm/required-name))))

(stest/instrument)