(ns de.sveri.getless.db.weight
  (:require [korma.core :refer [select where insert delete values update set-fields defentity limit order]]
            [korma.db :refer [h2]]
            [de.sveri.getless.db.entities :refer [weight]]
            [clojure.spec :as s])
  (:import (java.sql Timestamp)))

(s/def ::id number?)
(s/def ::users_id number?)
(s/def ::weighted_at (s/or :instant inst? :string string?))
(s/def ::weight number?)
(s/def ::weight-map (s/keys :req-un [::weight ::weighted_at]
                            :opt-un [::id ::users_id]))
(s/def ::weights (s/cat :weight-map (s/* ::weight-map)))

(s/fdef get-weights :args (:users_id number?)
        :ret ::weights)
(defn get-weights [users_id]
  (select weight (where {:users_id users_id})
          (order :weighted_at :asc)))

(s/fdef save-weight :args (s/cat :weight-measure number? :date inst? :users_id number?))
(defn save-weight [weight-measure date users_id]
  (insert weight (values {:weight weight-measure
                          :weighted_at (new Timestamp (.getTime date))
                          :users_id users_id})))
