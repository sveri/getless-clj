(ns de.sveri.getless.db.banking
  (:require [clojure.java.jdbc :as j])
  (:import (org.joda.time LocalDate)
           (java.sql Timestamp)))


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

(defn get-bank-account-by-iban [db iban]
  (first (j/query db ["SELECT * FROM banking_account WHERE iban = ?" iban])))
