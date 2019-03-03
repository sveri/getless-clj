(ns de.sveri.getless.banking.common
  (:require [re-frame.core :as rf]
            [cljs.pprint :as pprint]
            [cljs-time.format :as t-f]
            [cljs-time.coerce :as t-c]))


(def <sub (comp deref rf/subscribe))

(def >evt rf/dispatch)


(defn format-amount [a]
  (pprint/cl-format nil  "~,2f €" a))

(def german-date-formatter (t-f/formatter "dd.MM.yyyy"))

(defn format-js-date-german [d]
  (t-f/unparse german-date-formatter (t-c/from-date d)))

; loading screen

(defn show-loading-screen [db]
  (assoc db :show-loading-screen true))

(defn hide-loading-screen [db]
  (assoc db :show-loading-screen false))

(rf/reg-sub
  ::loading-screen
  (fn [db _] (:show-loading-screen db)))
