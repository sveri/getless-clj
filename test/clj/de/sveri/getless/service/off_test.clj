(ns de.sveri.getless.service.off-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.off :as off]
            [clojure.spec.test :as stest]))

(def off-url "http://de.world.openfoodfacts.net/")
(def off-user "off")
(def off-password "off")

(deftest find-off-steak
  (let [steaks (off/search-products "Steak" off-url off-user off-password)]
    (is (< 0 (count (:products steaks))))))


(stest/instrument)
