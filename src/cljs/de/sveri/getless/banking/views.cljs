(ns de.sveri.getless.banking.views
  (:require [de.sveri.getless.banking.common :as comm :refer [<sub >evt]]
            [de.sveri.getless.banking.subs :as b-sub]
            [de.sveri.getless.banking.events :as b-ev]
            [cljs-time.core :as t-c]))

(defn get-value-of-event [e]
  (.-value (.-target e)))

(defn date->month-year [d]
  (str (t-c/month d) "/" (t-c/year d)))

(defn transaction-table []
  [:div
   [:div.row
    [:div.col-md-2
     (let [selected-range (<sub [::b-sub/selected-range])]
       [:select.form-control
        {:on-change #(>evt [::b-ev/selected-range (get-value-of-event %)])
         :value selected-range}
        [:option {:value "this_month"} "This Month"]
        (let [current-year (t-c/year (t-c/now))]
          (for [y (range 0 4)]
            ^{:key (str "year_" y)}
            [:option {:value (- current-year y)} (- current-year y)]))
        (let [now (t-c/now)]
          (for [m (range 1 34)]
            (let [new-date (t-c/minus now (t-c/months m))
                  month-date (date->month-year new-date)]
              ^{:key (str "month_year_" month-date)}
              [:option {:value month-date} month-date])))
        [:option {:value "all"} "All"]])]
    [:div.col-md-5
     [:input.form-control {:value (<sub [::b-sub/search-text])
                           :on-change #(>evt [::b-ev/set-search-text (get-value-of-event %)])}]]
    [:div.col-md-3]
    [:div.col-md-2 (str "Amount Summary: " (comm/format-amount (<sub [::b-sub/amount-summary])))]]

   [:hr]

   [:table.table
    [:thead
     [:tr
      [:th "Amount"]
      [:th "Date"]
      [:th "Purpose"]
      [:th "Contractor / Beneficiary"]
      [:th "Recurring (Monthly)"]]]
    [:tbody
     (let [transactions (<sub [::b-sub/filtered-transactions])]
       (for [transaction transactions]
         ^{:key (str "transaction_id_"(:id transaction))}
         [:tr
          [:td (comm/format-amount (:amount transaction))]
          [:td (comm/format-js-date-german (:booking-date transaction))]
          [:td (str (:booking-text transaction) " - " (:purpose transaction))]
          [:td (str (:contractor-beneficiary transaction) " - "(:creditor-id transaction))]
          [:td [:input.form-control
                {:type "checkbox"
                 :checked (get transaction :recurring)}]]]))]]])
                 ;:on-change #(>evt [::b-ev/set-transaction-recurring
                 ;                   (:id transaction)
                 ;                   (.. % -target -checked)])}]]]))]]])

