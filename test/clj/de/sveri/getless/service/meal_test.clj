(ns de.sveri.getless.service.meal-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.meal :as meal]
            [de.sveri.getless.db.meal :as db-meal]
            [clojure.spec.test :as stest]
            [de.sveri.getless.service.off :as off]
            [de.sveri.getless.service.off-helper :as offh]))


(deftest foods-to-products-test
  (let [products (meal/foods-to-products [1 2 3] [100 200 300 ] ["gramm" "gramm" "gramm"])]
    (is (= {:product-id 1, :amount 100, :unit "gramm"} (first products)))
    (is (= {:product-id 2, :amount 200, :unit "gramm"} (second products)))
    (is (= {:product-id 3, :amount 300, :unit "gramm"} (nth products 2)))))


;(deftest save-meal-in-session
;  (let [session (meal/save-new-meal {})
;        meal (meal/get-meal session)]
;    (is (= "BREAKFAST" (:type meal)))))
;
;
;(deftest save-meal-in-session-with-launch
;  (let [session (meal/save-new-meal {} db-meal/launch)
;        meal (meal/get-meal session)]
;    (is (= db-meal/launch (:type meal)))))
;
;
;(deftest ^:integration add-product-to-meal
;  (let [off-product (off/get-by-id 22114166 offh/off-url offh/off-user offh/off-password)
;        session (meal/add-product-to-meal {} off-product)
;        meal (meal/get-meal session)]
;    (is (= 1 (count (:products meal))))
;    (let [sess2 (meal/add-product-to-meal session off-product)
;          meal (meal/get-meal sess2)]
;      (is (= 2 (count (:products meal)))))))
;
;(stest/instrument)

