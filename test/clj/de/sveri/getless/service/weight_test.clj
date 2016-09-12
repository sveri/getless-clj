(ns de.sveri.getless.service.weight-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.weight :as w]
            [clojure.spec.test :as stest]
            [clojure.instant :as inst]))

(deftest convert-weight_at
  (let [weights [{:weight 10 :weighted_at (inst/read-instant-date "2016-09-03")}
                 {:weight 20 :weighted_at (inst/read-instant-date "2017-09-03")}]]
    (is (= "[\"Sat Sep 03 02:00:00 CEST 2016\",\"Sun Sep 03 02:00:00 CEST 2017\"]"
           (w/weight->js-string :weighted_at weights)))))

(deftest convert-weight
  (let [weights [{:weight 10 :weighted_at (inst/read-instant-date "2016-09-03")}
                 {:weight 20 :weighted_at (inst/read-instant-date "2017-09-03")}]]
    (is (= "[\"10\",\"20\"]" (w/weight->js-string :weight weights)))))


(deftest convert-weight-at-date-format
  (let [weights [{:weight 10 :weighted_at (inst/read-instant-date "2016-09-03")}
                 {:weight 20 :weighted_at (inst/read-instant-date "2016-09-04")}]]
    (is (= "03.09.2016" (-> (w/format-weighted-at weights w/weight-date-format)
                            first :weighted_at)))
    (is (= "04.09.2016" (-> (w/format-weighted-at weights w/weight-date-format)
                            second :weighted_at)))))



(stest/instrument)