(ns de.sveri.getless.banking.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [de.sveri.getless.banking.common :as comm]
            [ajax.core :as ajax]))


(rf/reg-event-fx
  ::initialize-db
  (fn [_ [_ _]]
    {:http-xhrio {:method     :get
                  :uri        "/banking/api/data/initial"
                  :response-format (ajax/detect-response-format)
                  :on-success [::initial-data-loaded]
                  :on-failure [::initial-data-error]}
     :db         (-> {}
                     comm/show-loading-screen)}))

(rf/reg-event-db
  ::initial-data-loaded
  (fn [db [_ response]]
    (-> db
        (assoc :transactions response)
        comm/hide-loading-screen)))

(rf/reg-event-fx
  ::initial-data-error
  (fn [{db :db} [_ response]]
    {:db (-> db
             (assoc :generic-error (:error response))
             comm/hide-loading-screen)}))
     ;:navigate-to :login}))
