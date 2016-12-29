(ns de.sveri.getless.service.off-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.off :as off]
            [de.sveri.getless.service.off-helper :as offh]
            [clojure.string :as str]
            [taoensso.tempura :refer [tr]]
            [clojure.spec.test :as stest]
            [de.sveri.getless.locale :as loc]))

(defn localize []
  (partial tr
           {:default-locale :en
            :dict           loc/local-dict}
           [:en]))

(deftest ^:integration find-off-steak
  (let [steaks (off/search-products "Steak" true localize offh/off-url  offh/off-user  offh/off-password)]
    (is (< 0 (count (:products steaks))))))


(deftest ^:integration find-by-id
  (let [product (off/get-by-id 22114166 localize offh/off-url  offh/off-user  offh/off-password)]
    (is = (:code product "22114166"))))


(deftest add-ingredients-text
  (let [ingredients [{:text "first" :rank 1} {:text "third" :rank 3} {:text "second" :rank 2}]
        textified (off/add-ingredients {:ingredients ingredients})]
    (is (= "first, second, third" (:ingredients_text textified)))))

(deftest ^:integration check-one-with-ingredients
  (let [product (off/get-by-id 3273220086056 localize offh/off-url  offh/off-user  offh/off-password)]
    (is (not (str/blank? (:ingredients_text product))))))

(deftest ^:integration nutriments-exist
  (let [product (off/get-by-id 3273220086056 localize offh/off-url  offh/off-user  offh/off-password)]
    (is (not-empty (:nutriments product)))))

(deftest ^:integration kj->kcal
  (let [product (off/get-by-id 3273220086056 localize offh/off-url  offh/off-user  offh/off-password)]
    (is (str/starts-with? (:energy-kcal (:nutriments product)) "178."))))


(stest/instrument)
