(ns de.sveri.getless.banking.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::transactions
  (fn [db _]
    (-> db :transactions)))

(rf/reg-sub
  ::amount-summary
  ;(fn []
  ;  [(rf/subscribe [::filtered-file-list])])
  (fn [db _]
    (reduce (fn [sum f]
              (+ sum (js/parseFloat (get f :amount 0.0)))) 0.0 (:transactions db))))
