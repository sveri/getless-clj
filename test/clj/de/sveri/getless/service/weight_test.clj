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
  (let [weights [{:weight 10 :date (inst/read-instant-date "2016-09-03")}
                 {:weight 20 :date (inst/read-instant-date "2016-09-04")}]]
    (is (= "03.09.2016" (-> (w/format-weighted-at weights w/weight-date-format)
                            first :date)))
    (is (= "04.09.2016" (-> (w/format-weighted-at weights w/weight-date-format)
                            second :date)))))



(def nutriments [{:eaten-at (inst/read-instant-date "2016-09-01") :sugars_100g 10}
                 {:eaten-at (inst/read-instant-date "2016-09-05") :sugars_100g 20}
                 {:eaten-at (inst/read-instant-date "2016-09-08") :sugars_100g 30}])

(def weights [{:weighted_at (inst/read-instant-date "2016-09-02") :weight 90}
              {:weighted_at (inst/read-instant-date "2016-09-03") :weight 91}
              {:weighted_at (inst/read-instant-date "2016-09-05") :weight 92}
              {:weighted_at (inst/read-instant-date "2016-09-08") :weight 94}
              {:weighted_at (inst/read-instant-date "2016-09-09") :weight 94}])


(deftest merge-weights-and-nutriments-test
  (let [merged-maps (w/merge-weights-and-nutriments weights nutriments)
        third (get merged-maps 2)]
    (is (= 92 (:weight third)))
    (is (= 20 (:sugars_100g third)))
    (is (:date third))))


(stest/instrument)