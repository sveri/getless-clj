(ns de.sveri.getless.banking.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [de.sveri.getless.banking.common :refer [<sub >evt]]
            [de.sveri.getless.banking.subs]
            [de.sveri.getless.banking.events :as ev]
            [de.sveri.getless.banking.views :as v]))

;(rf/reg-event-db
;  ::initialize-db
;  (fn [db _]
;    (-> db
;        (assoc :foobar "state"))))
;
;(rf/reg-event-db
;  ::s-changed
;  (fn [db _]
;    (-> db
;        (assoc :foobar (str (rand-int 10))))))
;
;
;(rf/reg-sub
;  ::foobar
;  (fn [db _]
;    (println "ee")
;    (-> db :foobar)))


(defn ui []
  ;(let [s (<sub [::foobar])]
  ;  [:h3
  ;   {:on-click #(>evt [::s-changed])}
  ;   s]))
  [v/transaction-table])


(defn render []
  (reagent/render [ui] (js/document.getElementById "banking_js")))

(defn ^:export run []
  (rf/dispatch-sync [::ev/initialize-db])
  ;(rf/dispatch-sync [::initialize-db])
  (render))
