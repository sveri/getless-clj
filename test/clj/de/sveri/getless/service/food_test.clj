(ns de.sveri.getless.service.food-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.food :as food]))


(def foods [{:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618}
            {:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618}])

(deftest groub-by-date
  (let [foods-grouped (food/foods->group-by-date foods)]
    (is (= [{:id 1, :eaten-at #inst "2016-10-16T10:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}
            {:id 2, :eaten-at #inst "2016-10-16T12:00:00.000000000-00:00", :users-id 1, :product-id 4008400401621}]
           (first foods-grouped)))
    (is (= [{:id 3, :eaten-at #inst "2016-10-18T14:30:00.000000000-00:00", :users-id 1, :product-id 29015618}
            {:id 4, :eaten-at #inst "2016-10-18T20:50:20.000000000-00:00", :users-id 1, :product-id 29015618}]
           (second foods-grouped)))))
