(ns de.sveri.getless.db.banking
  (:require [clojure.java.jdbc :as j])
  (:import (org.joda.time LocalDate)
           (java.sql Timestamp)
           (java.text SimpleDateFormat)))


(defn create-banking-account [db data]
  (j/execute! db ["INSERT INTO banking_account (institute_name, account_name, iban, users_id, type)
                    values (?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING"
                  (get data :institute-name "")
                  (get data :account-name "")
                  (:iban data)
                  (:user-id data)
                  (get data :type "")]))

(defn create-banking-account-balance [db banking-account-id ^LocalDate date balance]
  (j/execute! db ["INSERT INTO banking_account_balance (banking_account_id, balance, balance_date)
                    values (?, ?, ?)
                    ON CONFLICT (banking_account_id, balance_date) DO UPDATE SET balance = ?"
                  banking-account-id
                  balance
                  (Timestamp. (.getMillis (.toDateTimeAtStartOfDay date)))
                  balance]))

(defn create-banking-account-transaction [db banking-account-id data]
  (j/execute! db ["INSERT INTO banking_account_transaction (banking_account_id, booking_date, value_date, booking_text,
                    contractor_beneficiary, purpose, banking_account_number, blz, amount, amount_currency, creditor_id,
                    mandate_reference, customer_reference)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING"
                  banking-account-id
                  (Timestamp. (.getMillis (.toDateTimeAtStartOfDay (:booking-date data))))
                  (Timestamp. (.getMillis (.toDateTimeAtStartOfDay (:value-date data))))
                  (get data :booking-text "")
                  (get data :contractor-beneficiary "")
                  (get data :purpose "")
                  (:banking-account-number data)
                  (get data :blz "")
                  (:amount data)
                  (get data :amount-currency "")
                  (get data :creditor-id "")
                  (get data :mandate-reference "")
                  (get data :customer-reference "")]))

(defn get-bank-account-by-iban [db iban]
  (first (j/query db ["SELECT * FROM banking_account WHERE iban = ?" iban])))


(defn sql-date-to-string [d]
  (let [df (SimpleDateFormat. "dd.MM.yyyy")]
    (.format df d)))



(defn get-all-banking-data-by-user [db user-id]
  (j/query db ["select * from banking_account_transaction bat
               join banking_account ba ON bat.banking_account_id = ba.id
               where users_id = ?"
               user-id]
           {:identifiers #(.replace % \_ \-)
            :row-fn (fn [t]
                      (-> t
                          (assoc :amount (double (:amount t)))))}))
                          ;(assoc :booking-date (sql-date-to-string (:booking-date t)))
                          ;(assoc :value-date (sql-date-to-string (:value-date t)))))}))

