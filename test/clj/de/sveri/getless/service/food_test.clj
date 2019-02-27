(ns de.sveri.getless.service.food-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.food :as food]
            [clojure.spec.test.alpha :as stest]))


(def foods [{:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618}
            {:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618}])


(def foods-grouped-with-nutriments
  [[{:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621
     :amount 50 :product {:nutriments {:sugars_100g "20" :sugars_unit "g" :energy-kcal 500 :fat_100g "50" :fat_unit "mg"}}}
    {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621
     :amount 140.5 :product {:nutriments {:sugars_100g "20" :sugars_unit "g" :energy-kcal 800 :fat_100g "10" :fat_unit "mg"}}}
    {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621
     :amount 140.5 :product {:nutriments {:sugars_100g "20" :sugars_unit "g" :energy-kcal 800}}}]
   [{:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618
     :amount 1400.5 :product {:nutriments {:sugars_100g "20" :sugars_unit "g" :energy-kcal 100 :fat_100g "20" :fat_unit "mg"}}}
    {:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618
     :amount 20}]])

(deftest group-by-date
  (let [foods-grouped (food/foods->group-by-date foods)]
    (is (= [{:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}]
           (first foods-grouped)))
    (is (= [{:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618}
            {:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618}]
           (second foods-grouped)))))

(deftest group-nutriments-by-date
  (stest/unstrument ['de.sveri.getless.service.food/->nutriments-grouped-by-date])
  (let [nutriments (food/->nutriments-grouped-by-date foods-grouped-with-nutriments)]
    (is (= 66.2 (:sugars_100g (first nutriments))))
    (is (= 280.1 (:fat_100g (second nutriments))))
    (is (= 1400.5 (:energy-kcal (second nutriments))))))


(def grouped-products-to-sort [[{:id 1,
                                 :eaten-at #inst "2016-10-16T10:00:00.000-00:00",
                                 :users-id 1,
                                 :product-id 4008400401621}
                                {:id 5,
                                 :eaten-at #inst "2016-10-16T14:00:00.000-00:00",
                                 :users-id 1,
                                 :product-id 4008400401621}
                                {:id 2,
                                 :eaten-at #inst "2016-10-16T12:00:00.000-00:00",
                                 :users-id 1,
                                 :product-id 4008400401621}]
                               [{:id 3,
                                 :eaten-at #inst "2016-10-18T14:30:00.000-00:00",
                                 :users-id 1,
                                 :product-id 29015618}
                                {:id 4,
                                 :eaten-at #inst "2016-10-18T20:50:20.000-00:00",
                                 :users-id 1,
                                 :product-id 29015618}]])

(deftest sort-grouped-products-by-date-test
  (let [sorted-products (food/sort-grouped-products-by-date grouped-products-to-sort :eaten-at)]
    (is (= [{:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618}
            {:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618}]
           (first sorted-products)))
    (is (= [{:id 5, :eaten-at #inst "2016-10-16T14:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}]
           (second sorted-products)))))


(stest/instrument)
(stest/unstrument ['de.sveri.getless.service.food/add-nutriment])