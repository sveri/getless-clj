(ns de.sveri.getless.routes.activity
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.user :as s-user]
            [de.sveri.getless.db.activity :as db-act]
            [clj-time.coerce :as t-coerce]
            [clj-time.core :as t-core]
            [de.sveri.getless.service.activity :as s-act]
            [clojure.tools.logging :as log]))

(defn index-page [db]
  (let [activities (vec (s-act/pad-with-tomorrow-today-yesterday-if-needed (db-act/get-activities db (s-user/get-logged-in-user-id db) 10)))]
    (layout/render "activity/index.html" {:rest-activities (if (< 3 (count activities)) (subvec activities 3) [])
                                          :activities activities})))

(defn save [db tomorrow-text today-text yesterday-text tomorrow-done today-done yesterday-done]
  (let [user-id (s-user/get-logged-in-user-id db)
        three-dates (s-act/get-three-dates)]
    (try
      (db-act/insert-or-update-activity db tomorrow-text tomorrow-done (:tomorrow three-dates) user-id)
      (db-act/insert-or-update-activity db today-text today-done (:today three-dates) user-id)
      (db-act/insert-or-update-activity db yesterday-text yesterday-done (:yesterday three-dates) user-id)
      (catch Exception e
        (layout/flash-result "Etwas schlug beim speichern fehl." "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/activity")))



(defn activity-routes [_ db]
  (routes
    (GET "/activity" [] (index-page db))
    (POST "/activity" [tomorrow today yesterday tomorrow-done today-done yesterday-done]
      (save db tomorrow today yesterday tomorrow-done today-done yesterday-done))))
