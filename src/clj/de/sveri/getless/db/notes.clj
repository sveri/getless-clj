(ns de.sveri.getless.db.notes
  (:require [clojure.java.jdbc :as j]
            [clojure.spec.alpha :as s])
  (:import (java.sql Timestamp)))


(defn get-notes [db users_id]
  (j/query db ["select * from notes where users_id = ? order by created_at desc" users_id]))



;(defn save-goal [db goal s-m-l users_id]
;  (j/insert! db :notes {:goal goal :s_m_l s-m-l :users_id users_id :done false}))
;
;(defn get-goal [db id user-id]
;  (first (j/query db ["select * from notes where users_id = ? and id = ? order by created_at desc" user-id id])))
;
;(defn delete-goal [db id user-id]
;  (try
;      (j/delete! db :notes ["id = ? and users_id = ?" id user-id])
;    (catch Exception e
;      (println (.getNextException e))
;      (throw (Exception. e)))))
;
;(defn edit-goal [db id goal s-m-l done user-id]
;  (j/update! db :notes {:goal goal :s_m_l s-m-l :done done} ["id = ? and users_id = ?" id user-id]))