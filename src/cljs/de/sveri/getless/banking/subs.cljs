(ns de.sveri.getless.banking.subs
  (:require [re-frame.core :as rf]
            [de.sveri.getless.banking.service :as b-s]))

(rf/reg-sub
  ::transactions
  (fn [db _]
    (-> db :transactions)))

(rf/reg-sub
  ::selected-range
  (fn [db _]
    (-> db :selected-range)))


(rf/reg-sub
  ::filtered-transactions
  (fn []
    [(rf/subscribe [::transactions])
     (rf/subscribe [::selected-range])])
  (fn [[transactions selected-range]]
    (b-s/get-transactions-in-time-range transactions selected-range)))


(rf/reg-sub
  ::amount-summary
  (fn []
    [(rf/subscribe [::filtered-transactions])])
  (fn [[transactions]]
    (reduce (fn [sum f]
              (+ sum (js/parseFloat (get f :amount 0.0)))) 0.0 transactions)))
