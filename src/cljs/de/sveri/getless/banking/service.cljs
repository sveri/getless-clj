(ns de.sveri.getless.banking.service
  (:require [cljs-time.core :as t]
            [cljs-time.coerce :as t-c]
            [clojure.string :as str]))


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


(defn get-transactions-for-month [transactions time-range]
  (let [[m y] (str/split time-range #"/")
        month-year-now (t/local-date-time (int y) (int m) 1)
        month-year-after (t/plus month-year-now (t/months 1))]
    (filter #(t/within? (t/interval month-year-now month-year-after)
                        (t-c/from-date (:booking-date %)))
            transactions)))


(defn get-transactions-for-year [transactions year]
  (let [year-now (t/local-date-time (int year) 1 1)
        year-after (t/plus year-now (t/years 1))]
    (filter #(t/within? (t/interval year-now year-after)
                        (t-c/from-date (:booking-date %)))
            transactions)))


(defn get-transactions-in-time-range [transactions time-range]
  (cond
    (= "this_month" time-range) (get-transactions-for-this-month transactions)
    (str/includes? time-range "/") (get-transactions-for-month transactions time-range)
    (= "all" time-range) transactions
    :else (get-transactions-for-year transactions time-range)))
