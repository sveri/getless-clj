(ns de.sveri.getless.banking.subs
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [de.sveri.getless.banking.service :as b-s]))

(rf/reg-sub
  ::transactions
  (fn [db _]
    (reverse (sort-by :booking-date (-> db :transactions vals)))))

(rf/reg-sub
  ::selected-range
  (fn [db _]
    (-> db :selected-range)))

(rf/reg-sub
  ::search-text
  (fn [db _]
    (-> db :search-text)))



(defn search-by-search-text [search-text transactions]
  (let [search-words (if (str/includes? search-text " ")
                       (str/split search-text #" ")
                       [search-text])]
    (flatten (mapv (fn [search-word]
                     (let [search-small (str/lower-case search-word)]
                       (filter #(or (str/includes? (str/lower-case (:booking-text %)) search-small)
                                    (str/includes? (str/lower-case (:purpose %)) search-small)
                                    (str/includes? (str/lower-case (:contractor-beneficiary %)) search-small))
                               transactions)))
                  search-words))))


(rf/reg-sub
  ::filtered-transactions
  (fn []
    [(rf/subscribe [::transactions])
     (rf/subscribe [::selected-range])
     (rf/subscribe [::search-text])])
  (fn [[transactions selected-range search-text]]
    (let [time-filtered-transactions (b-s/get-transactions-in-time-range transactions selected-range)]
      (if search-text
        (search-by-search-text search-text time-filtered-transactions)
        time-filtered-transactions))))



(rf/reg-sub
  ::amount-summary
  (fn []
    [(rf/subscribe [::filtered-transactions])])
  (fn [[transactions]]
    (reduce (fn [sum f]
              (+ sum (js/parseFloat (get f :amount 0.0)))) 0.0 transactions)))
