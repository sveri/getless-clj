(ns de.sveri.getless.banking.main
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [de.sveri.getless.banking.common :refer [<sub >evt]]
            [de.sveri.getless.banking.subs]
            [de.sveri.getless.banking.events :as ev]
            [de.sveri.getless.banking.views :as v]))


(defn ui []
  [v/transaction-table])


(defn render []
  (rf/clear-subscription-cache!)
  (reagent/render [ui] (js/document.getElementById "banking_js")))

(defn ^:export run []
  (rf/dispatch-sync [::ev/initialize-db])
  (render))
