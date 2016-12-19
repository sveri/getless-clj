(ns de.sveri.getless.db.weight
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s])
  (:import (java.sql Timestamp)))

(s/def ::id number?)
(s/def ::users_id number?)
(s/def ::written_at inst?)
(s/def ::edited_at inst?)
(s/def ::content string?)
(s/def ::activity-map (s/keys :req-un [::content ::written_at]
                              :opt-un [::id ::users_id]))
(s/def ::activities (s/coll-of ::activity-map))


(s/fdef get-activities :args (s/cat :db any? :users_id number? :limit (s/? number?))
                        :ret ::activities)
(defn get-activities [db users_id & [limit]]
  (let [query (if limit ["select * from activity where users_id = ? order by written_at desc limit ?" users_id limit]
                        ["select * from activity where users_id = ? order by written_at desc" users_id])]
    (j/query db query)))


(s/fdef save-activity :args (s/cat :db any? :content ::content :date number? :users_id number?))
(defn save-activity [db content date users_id]
  (j/insert! db :activity {:content content
                           :written_at (new Timestamp date)
                           :users_id users_id}))
