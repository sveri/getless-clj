(ns de.sveri.getless.service.meal
  (:require [clojure.spec :as s]
            [de.sveri.getless.db.meal :as db-meal]
            [de.sveri.getless.db.food :as db-food]
            [de.sveri.getless.service.off :as s-off]))


(s/fdef foods-to-products :args (s/cat :productids (s/coll-of ::db-food/product-id) :amounts ::db-food/amounts :units (s/coll-of ::db-food/unit))
        :ret ::db-meal/product-edn)

(defn foods-to-products [productids amounts units]
  (map (fn [productid amount unit] {:product-id productid :amount amount :unit unit}) productids amounts units))


(s/fdef get-products-from-meal :args (s/cat :db any? :meal-id number? :off-url string? :off-user string? :off-password string?))
(defn get-products-from-meal [db meal-id off-url off-user off-password]
  (let [meal (db-meal/meal-by-id db meal-id)
        products-edn (:products-edn meal)]
    (mapv #(let [off-product (s-off/get-by-id (:product-id %) off-url off-user off-password)]
             (assoc off-product :amount (:amount %) :unit (:unit %)))
          products-edn)))

