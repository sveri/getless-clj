(ns de.sveri.getless.routes.banking
  (:require [compojure.core :refer [routes GET POST]]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.core :as t-c]
            [clj-time.coerce :as t-coer]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.banking-import :as bi]
            [de.sveri.getless.db.banking :as db-b]
            [de.sveri.getless.service.user :as s-u]))

(defn links-page []
  (layout/render "banking/index.html"))



(defn upload-csv [db req]
  (let [user-id (s-u/get-logged-in-user-id db)
        temp-file (-> req :params :filename :tempfile)]
    (with-open [reader (io/reader (.getPath temp-file) :encoding "Cp1252")]
      (bi/parse-and-insert-banking-data db user-id (csv/read-csv reader :separator \;)))
    (redirect "/banking")))


(defn find-unbooked-transactions [transactions]
  (let [look-back 3
        now (t-c/now)
        this-month (t-c/month now)
        this-year (t-c/year now)
        to-date (t-c/date-time this-year this-month)

        from-date (t-c/minus now (t-c/months look-back))
        from-year (t-c/year from-date)
        from-month (t-c/month from-date)
        from-date (t-c/date-time from-year from-month)

        from-date-1 (t-c/minus now (t-c/months 1))
        from-year-1 (t-c/year from-date-1)
        from-month-1 (t-c/month from-date-1)
        from-date-1 (t-c/date-time from-year-1 from-month-1)

        lookup-transactions (vec (filter
                                   #(t-c/within?
                                      (t-c/interval from-date to-date)
                                      (t-coer/from-sql-date (:booking-date %)))
                                   transactions))
        grouped-transactions (group-by :amount lookup-transactions)
        recurring-transactions (reduce (fn [acc elem]
                                         (if (= look-back (count (second elem)))
                                           (conj acc (first (second elem)))
                                           acc))
                                       []
                                       grouped-transactions)
        recurring-transactions (vec (filter
                                      #(t-c/within?
                                         (t-c/interval from-date-1 to-date)
                                         (t-coer/from-sql-date (:booking-date %)))
                                      recurring-transactions))]
    (filter #(> (t-c/day (t-coer/from-sql-date (:booking-date %)))
                (t-c/day (t-coer/from-sql-date (:booking-date (first transactions)))))
            recurring-transactions)))

(defn initial-data [db]
  (let [user-id (s-u/get-logged-in-user-id db)
        transactions-list (db-b/get-all-banking-data-by-user db user-id)
        unbooked-transactions (find-unbooked-transactions transactions-list)]
        ;transactions-list (find-recurring-transactions transactions-list)]
    (response (reduce #(assoc %1 (:id %2) %2) {} transactions-list))))

(defn banking-routes [config db]
  (routes (GET "/banking" [] (links-page))
          (GET "/banking/api/data/initial" [] (initial-data db))
          (POST "/banking/upload" req (upload-csv db req))))


