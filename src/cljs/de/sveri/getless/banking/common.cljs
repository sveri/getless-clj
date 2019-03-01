(ns de.sveri.getless.banking.common
  (:require [re-frame.core :as rf]))


; loading screen

(defn show-loading-screen [db]
  (assoc db :show-loading-screen true))

(defn hide-loading-screen [db]
  (assoc db :show-loading-screen false))

(rf/reg-sub
  ::loading-screen
  (fn [db _] (:show-loading-screen db)))
