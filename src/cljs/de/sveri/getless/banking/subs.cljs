(ns de.sveri.getless.banking.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::transactions
  (fn [db _]
    (-> db :transactions)))
