(ns de.sveri.getless.routes.goals
  (:require [compojure.core :refer [routes GET POST]]
            [de.sveri.getless.layout :as layout]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.service.user :as s-user]
            [clojure.tools.logging :as log]
            [de.sveri.getless.db.goals :as db-goals]
            [clojure.edn :as edn]))

;(defn home-page [{:keys [localize]}]
;  (layout/render "home/index.html" {:rules [(localize [:home/we-dont-judge])
;                                            (localize [:home/we-support-each-other])
;                                            (localize [:home/we-use-means])]}))

(defn filter-goals [goals s-m-l]
  (filter #(= s-m-l (:s_m_l %)) goals))

(defn goals-page [db]
  (let [goals (db-goals/get-goals db (s-user/get-logged-in-user-id db))
        goals-grouped (group-by :done goals)
        short-goals-done (filter-goals (get goals-grouped true) "s")
        mid-goals-done (filter-goals (get goals-grouped true) "m")
        long-goals-done (filter-goals (get goals-grouped true) "l")
        short-goals-todo (filter-goals (get goals-grouped false) "s")
        mid-goals-todo (filter-goals (get goals-grouped false) "m")
        long-goals-todo (filter-goals (get goals-grouped false) "l")]
    (layout/render "goals/index.html" {:shorts-done short-goals-done :mids-done mid-goals-done :longs-done long-goals-done
                                       :shorts-todo short-goals-todo :mids-todo mid-goals-todo :longs-todo long-goals-todo})))
                                       



(defn add-goals-page []
  (layout/render "goals/add.html"))


(defn add-goals [db {:keys [localize]} goal s-m-l]
  (let [user-id (s-user/get-logged-in-user-id db)]
    (try
      (db-goals/save-goal db goal s-m-l user-id)
      (redirect "/goals")
      (catch Exception e
        (layout/flash-result (localize [:generic/some_error]) "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/goals")))



(defn edit-goal-page [db {:keys [localize]} id]
  (try
    (let [user-id (s-user/get-logged-in-user-id db)
          goal (db-goals/get-goal db (edn/read-string id) user-id)]
      (layout/render "goals/edit.html" {:goal goal}))
    (catch Exception e
      (layout/flash-result (localize [:generic/some_error]) "alert-danger")
      (log/error "Error adding activity")
      (.printStackTrace e)
      (redirect "/goals"))))

(defn delete-goal [db {:keys [localize]} id]
  (try
    (let [user-id (s-user/get-logged-in-user-id db)]
      (db-goals/delete-goal db (edn/read-string id) user-id))
    (catch Exception e
      (layout/flash-result (localize [:generic/some_error]) "alert-danger")
      (log/error "Error adding activity")
      (.printStackTrace e)))
  (redirect "/goals"))

(defn edit-goal [db {:keys [localize]} id goal s-m-l done]
  (let [user-id (s-user/get-logged-in-user-id db)
        done (if (= "on" done) true false)]
    (try
      (db-goals/edit-goal db (edn/read-string id) goal s-m-l done user-id)
      (redirect "/goals")
      (catch Exception e
        (layout/flash-result (localize [:generic/some_error]) "alert-danger")
        (log/error "Error adding activity")
        (.printStackTrace e)))
    (redirect "/goals")))

(defn goal-routes [config db]
  (routes
    (GET "/goals" [] (goals-page db))
    (GET "/goals/add" [] (add-goals-page))
    (GET "/goals/edit/:id" [id :as req] (edit-goal-page db req id))
    (POST "/goals/add" [goal s-m-l :as req] (add-goals db req goal s-m-l))
    (POST "/goals/edit" [id goal s-m-l done :as req] (edit-goal db req id goal s-m-l done))
    (GET "/goals/delete/:id" [id :as req] (delete-goal db req id))))
