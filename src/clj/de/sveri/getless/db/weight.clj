(ns de.sveri.getless.db.weight
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s])
  (:import (java.sql Timestamp)))

(s/def ::id number?)
(s/def ::users_id number?)
(s/def ::weighted_at (s/or :instant inst? :string string?))
(s/def ::weight number?)
(s/def ::weight-map (s/keys :req-un [::weight ::weighted_at]
                            :opt-un [::id ::users_id]))
(s/def ::weights (s/coll-of ::weight-map))


(s/fdef get-weights :args (s/cat :db any? :users_id number? :limit number?)
        :ret ::weights)
(defn get-weights [db users_id & [limit]]
  (let [query (if limit ["select * from weight where users_id = ? order by weighted_at desc limit ?" users_id limit]
                        ["select * from weight where users_id = ? order by weighted_at desc" users_id])]
    (j/query db query)))


(s/fdef save-weight :args (s/cat :db any? :weight-measure number?
                                 :date number? :users_id number?))
(defn save-weight [db weight-measure date users_id]
  (j/insert! db :weight {:weight weight-measure
                         :weighted_at (new Timestamp date)
                         :users_id users_id}))


(defn delete-weight [db id users-id]
  (j/delete! db :weight ["id = ? and users_id = ?" id users-id]))
