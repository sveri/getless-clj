(ns de.sveri.getless.routes.activity
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.user :as s-user]
            [de.sveri.getless.db.activity :as db-act]
            [clj-time.coerce :as t-coerce]
            [clj-time.core :as t-core]
            [clojure.tools.logging :as log]))

(defn index-page [db]
  (let [activities (vec (db-act/get-activities db (s-user/get-logged-in-user-id db) 10))]
    (layout/render "activity/index.html" {:rest-activities (subvec activities 3)
                                          :activities activities})))

(defn save [db tomorrow-text today-text yesterday-text]
  (let [user-id (s-user/get-logged-in-user-id db)
        today (t-coerce/to-date (t-core/today))
        yesterday (t-coerce/to-date (t-core/yesterday))
        tomorrow (t-coerce/to-date (-> 1 t-core/days t-core/from-now))]
    (try
      (db-act/insert-or-update-activity db tomorrow-text tomorrow user-id)
      (db-act/insert-or-update-activity db today-text today user-id)
      (db-act/insert-or-update-activity db yesterday-text yesterday user-id)
      (catch Exception e
        (layout/flash-result "Etwas schlug beim speichern fehl." "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/activity")))



(defn activity-routes [_ db]
  (routes
    (GET "/activity" [] (index-page db))
    (POST "/activity" [tomorrow today yesterday] (save db tomorrow today yesterday))))
