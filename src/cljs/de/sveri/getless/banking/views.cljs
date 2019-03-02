(ns de.sveri.getless.banking.views
  (:require [de.sveri.getless.banking.common :as comm :refer [<sub]]
            [de.sveri.getless.banking.subs :as b-sub]))

(defn transaction-table []
  [:div
   [:h4 (str "Amount Summary: " (comm/format-amount (<sub [::b-sub/amount-summary])))]

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
          [:td (comm/format-amount (:amount transaction))]
          [:td (:booking-date transaction)]
          [:td (str (:booking-text transaction) " - " (:purpose transaction))]
          [:td (str (:contractor-beneficiary transaction) " - "(:creditor-id transaction))]]))]]])

