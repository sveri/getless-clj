(ns de.sveri.getless.banking.views
  (:require [de.sveri.getless.banking.common :refer [<sub]]
            [de.sveri.getless.banking.subs :as b-sub]))

(defn transaction-table []
  [:div
   [:table.table
    [:thead
     [:tr
      [:th "Amount"]
      [:th "Date"]
      [:th "Purpose"]
      [:th "Contractor / Beneficiary"]]]
    [:tbody
     (let [transactions (<sub [::b-sub/transactions])]
       (for [transaction transactions]
         ^{:key (:id transaction)}
         [:tr
          [:td (:amount transaction)]
          [:td (:booking-date transaction)]
          [:td (str (:booking-text transaction) " - " (:purpose transaction))]
          [:td (str (:contractor-beneficiary transaction) " - "(:creditor-id transaction))]]))]]])

