(ns de.sveri.getless.banking.views
  (:require [de.sveri.getless.banking.common :as comm :refer [<sub >evt]]
            [de.sveri.getless.banking.subs :as b-sub]
            [de.sveri.getless.banking.events :as b-ev]))

(defn get-value-of-event [e]
  (.-value (.-target e)))

(defn transaction-table []
  [:div
   [:div.row
    [:div.col-md-2
     (let [selected-range (<sub [::b-sub/selected-range])]
       [:select.form-control
        {:on-change #(>evt [::b-ev/selected-range (-> % (.-target) (.-value))])
         :value selected-range}
        [:option {:value "all"} "All"]
        [:option {:value "this_month"} "This Month"]
        [:option {:value "last_month"} "Last Month"]
        [:option {:value "forlast_month"} "Forlast Month"]])]
    [:div.col-md-5
     [:input.form-control {:value (<sub [::b-sub/search-text])
                           :on-change #(>evt [::b-ev/set-search-text (-> % (.-target) (.-value))])}]]
    [:div.col-md-3]
    [:div.col-md-2 (str "Amount Summary: " (comm/format-amount (<sub [::b-sub/amount-summary])))]]

   [:hr]

   [:table.table
    [:thead
     [:tr
      [:th "Amount"]
      [:th "Date"]
      [:th "Purpose"]
      [:th "Contractor / Beneficiary"]]]
    [:tbody
     (let [transactions (<sub [::b-sub/filtered-transactions])]
       (for [transaction transactions]
         ^{:key (:id transaction)}
         [:tr
          [:td (comm/format-amount (:amount transaction))]
          [:td (comm/format-js-date-german (:booking-date transaction))]
          [:td (str (:booking-text transaction) " - " (:purpose transaction))]
          [:td (str (:contractor-beneficiary transaction) " - "(:creditor-id transaction))]]))]]])

