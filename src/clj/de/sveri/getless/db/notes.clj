(ns de.sveri.getless.db.notes
  (:require [clojure.java.jdbc :as j]
            [clojure.spec.alpha :as s])
  (:import (java.sql Timestamp)))


(defn get-notes [db users_id]
  (j/query db ["select * from notes where users_id = ? order by created_at desc" users_id]))


