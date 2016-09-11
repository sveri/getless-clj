(ns de.sveri.getless.service.weight-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.service.weight :as w]
            [clojure.spec.test :as stest]
            [clojure.instant :as inst]))

(deftest convert-weight_at
  (let [weights [{:weight 10 :weighted-at (inst/read-instant-timestamp "1473587458")}]]
                 ;{:weight 20 :weighted-at "2017"}]]
    (is (= "[\"2016\",\"2017\"]" (w/weight->js-string :weighted-at weights)))))

;(deftest convert-weight
;  (let [weights [{:weight 10 :weighted-at "2016"}
;                 {:weight 20 :weighted-at "2017"}]]
;    (is (= "[\"10\",\"20\"]" (w/weight->js-string :weight weights)))))
;
;
;(deftest convert-weight-at-date-format
;  (let [weights [{:weight 10 :weighted-at "2016-09-03 00:00:00.0"}
;                 {:weight 20 :weighted-at "2016-09-04 00:00:00.0"}]]
;    (is (= "03.09.2016" (-> (w/format-weighted-at weights w/postgres-date-formatter w/weight-date-format)
;                            first :weighted-at)))
;    (is (= "04.09.2016" (-> (w/format-weighted-at weights w/postgres-date-formatter w/weight-date-format)
;                            second :weighted-at)))))



(stest/instrument)