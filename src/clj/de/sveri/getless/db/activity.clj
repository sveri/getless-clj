(ns de.sveri.getless.db.activity
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s])
  (:import (java.sql Timestamp)
           (java.util Date)))

(s/def ::id number?)
(s/def ::users_id number?)
(s/def ::for_date inst?)
(s/def ::content string?)
(s/def ::activity-map (s/keys :req-un [::content ::for_date]
                              :opt-un [::id ::users_id]))
(s/def ::activities (s/coll-of ::activity-map))


(s/fdef get-activities :args (s/cat :db any? :users_id number? :limit (s/? number?))
        :ret ::activities)
(defn get-activities [db users_id & [limit]]
  (let [query (if limit ["select * from activity where users_id = ? order by for_date desc limit ?" users_id limit]
                        ["select * from activity where users_id = ? order by for_date desc" users_id])]
    (j/query db query {:identifiers #(.replace % \_ \-)})))


(s/fdef insert-activity :args (s/cat :db any? :content ::content :date inst? :users_id number?))
(defn insert-activity [db content date users_id]
  (j/insert! db :activity {:content  content
                           :for_date (new java.sql.Date (.getTime date))
                           :users_id users_id}))

(s/fdef insert-or-update-activity :args (s/cat :db any? :content ::content :date inst? :users_id number?))
(defn insert-or-update-activity [db content date users_id]
  (let [for-date (new java.sql.Date (.getTime date))
        get-query ["select * from activity where users_id = ? and for_date = ?" users_id for-date]
        get-result (j/query db get-query {:identifiers #(.replace % \_ \-)})]
    (if (< 0 (count get-result))
      (j/update! db :activity {:content content} ["users_id = ? and for_date = ?" users_id for-date])
      (insert-activity db content date users_id))))
