(ns de.sveri.getless.banking.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [de.sveri.getless.banking.events :as ev]))


(defn ui []
  [:h3 "sdfsdfs"])


(rf/reg-event-db
  ::initialize-db
  (fn [db _]
    {}))

(defn render []
  (rf/dispatch-sync [::ev/initialize-db])
  (reagent/render [ui] (js/document.getElementById "banking_js")))

(defn ^:export run []
  ;(rf/dispatch-sync [::ev/initialize-db])
  (render))
