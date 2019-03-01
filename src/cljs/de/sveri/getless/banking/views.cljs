(ns de.sveri.getless.banking.views
  (:require [de.sveri.getless.banking.common :refer [<sub]]
            [de.sveri.getless.banking.subs :as b-sub]))

(defn transaction-table []
  [:div
   (let [transactions (<sub [::b-sub/transactions])]
     (println transactions)

     (for [transaction transactions]
       ^{:key (:id transaction)}
       ;[:h3 "sf"]))])
       [:tr (:amount transaction)]))])

