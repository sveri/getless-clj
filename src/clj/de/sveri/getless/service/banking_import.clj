(ns de.sveri.getless.service.banking-import
  (:require [clojure.string :as str]
            [de.sveri.getless.db.banking :as db-b])
  (:import (java.util Locale)
           (java.text NumberFormat)
           (org.joda.time.format DateTimeFormat)))


(defn insert-banking-account-for-dkb [db user-id data]
  (let [account-name (-> data first second)
        iban (str/trim (first (str/split account-name #"/")))
        bank-account {:iban iban :institute-name "DKB" :account-name account-name :type "giro" :user-id user-id}
        _ (db-b/create-banking-account db bank-account)
        bank-account-id (:id (db-b/get-bank-account-by-iban db iban))]
    bank-account-id))

(defn insert-banking-account-balance-for-dkb [db bank-account-id data]
  (let [account-balance-raw (nth data 4)
        date-string (re-find #"[0-9]+\.[0-9]+\.[0-9]+" (first account-balance-raw))
        formatter (DateTimeFormat/forPattern "dd.MM.yyyy")
        date (.parseLocalDate formatter date-string)
        number-format (NumberFormat/getInstance Locale/GERMAN)
        balance (.parse number-format (first (str/split (second account-balance-raw) #" ")))]
    (db-b/create-banking-account-balance db bank-account-id date balance)))

(defn parse-and-insert-banking-data-dkb [db user-id data]
  (let [bank-account-id (insert-banking-account-for-dkb db user-id data)
        _ (insert-banking-account-balance-for-dkb db bank-account-id data)]))

(defn parse-and-insert-banking-data [db user-id data]
  (when (.startsWith (ffirst data) "Kontonummer:")
    (parse-and-insert-banking-data-dkb db user-id data)))

