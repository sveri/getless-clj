(ns de.sveri.getless.service.off-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.off :as off]
            [de.sveri.getless.service.off-helper :as offh]
            [clojure.string :as str]
            [clojure.spec.test :as stest]))


(deftest find-off-steak
  (let [steaks (off/search-products "Steak" offh/off-url  offh/off-user  offh/off-password)]
    (is (< 0 (count (:products steaks))))))


(deftest find-by-id
  (let [product (off/get-by-id 22114166  offh/off-url  offh/off-user  offh/off-password)]
    (is = (:code product "22114166"))))


(deftest add-ingredients-text
  (let [ingredients [{:text "first" :rank 1} {:text "third" :rank 3} {:text "second" :rank 2}]
        textified (off/add-string-fields {:ingredients ingredients})]
    (is (= "first, second, third" (:ingredients_text textified)))))

(deftest check-one-with-ingredients
  (let [product (off/get-by-id 3273220086056 offh/off-url  offh/off-user  offh/off-password)]
    (is (not (str/blank? (:ingredients_text product))))))


(stest/instrument)
