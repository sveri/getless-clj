(ns de.sveri.getless.banking.service
  (:require [cljs-time.core :as t]
            [cljs-time.coerce :as t-c]))


(defn get-transactions-for-this-month [transactions]
  (let [now (t/now)
        year (t/year now)
        month (t/month now)
        year-after (t/year (t/plus now (t/months 1)))
        month-after (t/month (t/plus now (t/months 1)))]
    (filter #(t/within? (t/interval (t/date-time year month)
                                    (t/date-time year-after month-after))
                        (t-c/from-date (:booking-date %)))
            transactions)))


(defn get-transactions-for-last-month [transactions]
  (let [now (t/now)
        year (t/year now)
        month (t/month now)
        year-before (t/year (t/minus now (t/months 1)))
        month-before (t/month (t/minus now (t/months 1)))]
    (filter #(t/within? (t/interval (t/date-time year-before month-before)
                                    (t/date-time year month))
                        (t-c/from-date (:booking-date %)))
            transactions)))


(defn get-transactions-in-time-range [transactions time-range]
  (cond
    (= "this_month" time-range) (get-transactions-for-this-month transactions)
    (= "last_month" time-range) (get-transactions-for-last-month transactions)
    :else transactions))
