(ns de.sveri.getless.db.activity
  (:require [clojure.java.jdbc :as j]
            [clojure.spec :as s]))

(s/def ::id number?)
(s/def ::users_id number?)
(s/def ::for-date inst?)
(s/def ::planned string?)
(s/def ::done (s/nilable string?))
(s/def ::activity-map (s/keys :req-un [::planned ::for-date]
                              :opt-un [::id ::users_id ::done]))
(s/def ::activities (s/coll-of ::activity-map))


(s/fdef get-activities :args (s/cat :db any? :users_id number? :limit (s/? number?))
        :ret ::activities)
(defn get-activities [db users_id & [limit]]
  (let [query (if limit ["select * from activity where users_id = ? order by for_date desc limit ?" users_id limit]
                        ["select * from activity where users_id = ? order by for_date desc" users_id])]
    (j/query db query {:identifiers #(.replace % \_ \-)})))


(s/fdef insert-activity :args (s/cat :db any? :planned ::planned :done ::done :date inst? :users_id number?))
(defn insert-activity [db planned done date users_id]
  (j/insert! db :activity {:planned planned
                           :done done
                           :for_date (new java.sql.Date (.getTime date))
                           :users_id users_id}))

(s/fdef insert-or-update-activity :args (s/cat :db any? :planned ::planned :done ::done :date inst? :users_id number?))
(defn insert-or-update-activity [db planned done date users_id]
  (let [for-date (new java.sql.Date (.getTime date))
        get-query ["select * from activity where users_id = ? and for_date = ?" users_id for-date]
        get-result (j/query db get-query {:identifiers #(.replace % \_ \-)})]
    (if (< 0 (count get-result))
      (j/update! db :activity {:planned planned :done done} ["users_id = ? and for_date = ?" users_id for-date])
      (insert-activity db planned done date users_id))))
