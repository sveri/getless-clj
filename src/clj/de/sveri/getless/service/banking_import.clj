(ns de.sveri.getless.service.banking-import
  (:require [clojure.string :as str]
            [de.sveri.getless.db.banking :as db-b]
            [clj-time.coerce :as t-coer]
            [clj-time.core :as t-c])
  (:import (java.util Locale)
           (java.text NumberFormat)
           (org.joda.time.format DateTimeFormat)))


(defn convert-german-date-string-to-local-date [s]
  (let [date-string (re-find #"[0-9]+\.[0-9]+\.[0-9]+" s)
        formatter (DateTimeFormat/forPattern "dd.MM.yyyy")]
    (if (not (str/blank? date-string))
      (.parseLocalDate formatter date-string)
      "")))


(defn convert-german-date-string-to-date-time [s]
  (let [date-string (re-find #"[0-9]+\.[0-9]+\.[0-9]+" s)
        formatter (DateTimeFormat/forPattern "dd.MM.yyyy")]
    (if (not (str/blank? date-string))
      (.parseDateTime formatter date-string)
      "")))

(defn convert-german-number-to-numeric [s]
  (let [number-format (NumberFormat/getInstance Locale/GERMAN)]
    (.parse number-format s)))


; dkb giro

(defn insert-banking-account-for-dkb-giro [db user-id data]
  (let [account-name (-> data first second)
        iban (str/trim (first (str/split account-name #"/")))
        bank-account {:iban iban :institute-name "DKB" :account-name account-name :type "giro" :user-id user-id}
        _ (db-b/create-banking-account db bank-account)
        bank-account-id (:id (db-b/get-bank-account-by-iban db iban))]
    bank-account-id))

(defn insert-banking-account-balance-for-dkb-giro [db bank-account-id data]
  (let [account-balance-raw (nth data 4)
        balance (convert-german-number-to-numeric (first (str/split (second account-balance-raw) #" ")))
        date (convert-german-date-string-to-local-date (first account-balance-raw))]
    (db-b/create-banking-account-balance db bank-account-id date balance)))

(defn remove-already-inserted-data [db data bank-account-id booking-date-index]
  (let [last-inserted-entries (db-b/get-last-banking-data-by-user-account-id db bank-account-id)]
    (if (< 0 (count last-inserted-entries))
      (let [last-inserted-date (t-c/minus (t-coer/from-sql-date (:booking-date (first last-inserted-entries))) (t-c/days 1))
            data (vec (filter
                        #(t-c/after?
                           (convert-german-date-string-to-date-time (nth % booking-date-index))
                           last-inserted-date)
                        data))]
        data
        data))))

(defn insert-banking-account-transaction-for-dkb-giro [db bank-account-id data]
  (println (count data))
  (let [transactions-raw (subvec (vec data) 7 (- (count data) 1))
        transactions-raw (remove-already-inserted-data db transactions-raw bank-account-id 0)]
    (println "inserting last " (count transactions-raw) " entries for " bank-account-id)
    (clojure.pprint/pprint transactions-raw)
    (doseq [transaction-raw transactions-raw]
      (let [value-date (if (str/blank? (nth transaction-raw 1))
                         (nth transaction-raw 0)
                         (nth transaction-raw 1))
            transaction {:booking-date (convert-german-date-string-to-local-date (nth transaction-raw 0))
                         :value-date (convert-german-date-string-to-local-date value-date)
                         :booking-text (nth transaction-raw 2)
                         :contractor-beneficiary (nth transaction-raw 3)
                         :purpose (nth transaction-raw 4)
                         :banking-account-number (nth transaction-raw 5)
                         :blz (nth transaction-raw 6)
                         :amount (convert-german-number-to-numeric (nth transaction-raw 7))
                         :amount-currency "EUR"
                         :creditor-id (nth transaction-raw 8)
                         :mandate-reference (nth transaction-raw 9)
                         :customer-reference (nth transaction-raw 10)}]

        (db-b/create-banking-account-transaction db bank-account-id transaction)))))


(defn parse-and-insert-banking-data-dkb-giro [db user-id data]
  (let [bank-account-id (insert-banking-account-for-dkb-giro db user-id data)
        _ (insert-banking-account-balance-for-dkb-giro db bank-account-id data)
        _ (insert-banking-account-transaction-for-dkb-giro db bank-account-id data)]))



; dkb credit card

(defn insert-banking-account-for-dkb-credit [db user-id data]
  (let [iban (str/trim (second (first data)))
        account-name (str "KK-" iban)
        bank-account {:iban iban :institute-name "DKB" :account-name account-name :type "kreditkarte" :user-id user-id}
        _ (db-b/create-banking-account db bank-account)
        bank-account-id (:id (db-b/get-bank-account-by-iban db iban))]
    bank-account-id))

(defn insert-banking-account-balance-for-dkb-credit [db bank-account-id data]
  (let [line-offset (if (.startsWith (first (nth data 2)) "Von:") 1 0)
        account-balance-raw (nth data (+ 3 line-offset))
        balance (convert-german-number-to-numeric (first (str/split (second account-balance-raw) #" ")))
        date (convert-german-date-string-to-local-date (second (nth data (+ line-offset 4))))]
    (db-b/create-banking-account-balance db bank-account-id date balance)))

(defn insert-banking-account-transaction-for-dkb-credit [db bank-account-id data]
  (let [line-offset (if (.startsWith (first (nth data 2)) "Von:") 1 0)
        transactions-raw (subvec (vec data) (+ line-offset 7) (- (count data) 1))
        transactions-raw (remove-already-inserted-data db transactions-raw bank-account-id 2)]
    (println "inserting last " (count transactions-raw) " entries for " bank-account-id)
    (doseq [transaction-raw transactions-raw]
      (let [transaction {:booking-date    (convert-german-date-string-to-local-date (nth transaction-raw 2))
                         :value-date      (convert-german-date-string-to-local-date (nth transaction-raw 1))
                         :booking-text    (nth transaction-raw 3)
                         :amount          (convert-german-number-to-numeric (nth transaction-raw 4))
                         :amount-currency "EUR"}]
        (db-b/create-banking-account-transaction db bank-account-id transaction)))))

(defn parse-and-insert-banking-data-dkb-credit [db user-id data]
  (let [bank-account-id (insert-banking-account-for-dkb-credit db user-id data)
        _ (insert-banking-account-balance-for-dkb-credit db bank-account-id data)
        _ (insert-banking-account-transaction-for-dkb-credit db bank-account-id data)]))


(defn parse-and-insert-banking-data [db user-id data]
  (cond
    (.startsWith (ffirst data) "Kontonummer:")
    (parse-and-insert-banking-data-dkb-giro db user-id data)
    (.startsWith (ffirst data) "Kreditkarte:")
    (parse-and-insert-banking-data-dkb-credit db user-id data)))

