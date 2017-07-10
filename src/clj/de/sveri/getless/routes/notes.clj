(ns de.sveri.getless.routes.notes
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.service.user :as s-user]
            [clojure.tools.logging :as log]
            [de.sveri.getless.db.notes :as db-notes]
            [clojure.edn :as edn]))


(defn notes-page [db]
  (let [notes (db-notes/get-notes db (s-user/get-logged-in-user-id db))]
    (layout/render "notes/index.html" {:notes notes})))
                                       



(defn add-notes-page []
  (layout/render "notes/add.html"))


(defn add-notes [db {:keys [localize]} goal s-m-l]
  (let [user-id (s-user/get-logged-in-user-id db)]
    (try
      ;(db-notes/save-goal db goal s-m-l user-id)
      (redirect "/notes")
      (catch Exception e
        (layout/flash-result (localize [:generic/some_error]) "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/notes")))



(defn edit-goal-page [db {:keys [localize]} id]
  (try
    (let [user-id (s-user/get-logged-in-user-id db)])
          ;goal (db-notes/get-goal db (edn/read-string id) user-id)]
      ;(layout/render "notes/edit.html" {:goal goal}))
    (catch Exception e
      (layout/flash-result (localize [:generic/some_error]) "alert-danger")
      (log/error "Error adding activity")
      (.printStackTrace e)
      (redirect "/notes"))))

(defn delete-goal [db {:keys [localize]} id]
  (try
    (let [user-id (s-user/get-logged-in-user-id db)])
      ;(db-notes/delete-goal db (edn/read-string id) user-id))
    (catch Exception e
      (layout/flash-result (localize [:generic/some_error]) "alert-danger")
      (log/error "Error adding activity")
      (.printStackTrace e)))
  (redirect "/notes"))

(defn edit-goal [db {:keys [localize]} id goal s-m-l done]
  (let [user-id (s-user/get-logged-in-user-id db)
        done (if (= "on" done) true false)]
    (try
      ;(db-notes/edit-goal db (edn/read-string id) goal s-m-l done user-id)
      (redirect "/notes")
      (catch Exception e
        (layout/flash-result (localize [:generic/some_error]) "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/notes")))

(defn notes-routes [config db]
  (routes
    (GET "/notes" [] (notes-page db))
    (GET "/notes/add" [] (add-notes-page))))
    ;(GET "/notes/edit/:id" [id :as req] (edit-goal-page db req id))
    ;(POST "/notes/add" [goal s-m-l :as req] (add-notes db req goal s-m-l))
    ;(POST "/notes/edit" [id goal s-m-l done :as req] (edit-goal db req id goal s-m-l done))
    ;(GET "/notes/delete/:id" [id :as req] (delete-goal db req id))))
