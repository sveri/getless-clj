(ns de.sveri.getless.db.weight
  (:require [korma.core :refer [select where insert delete values update set-fields defentity limit order]]
            [korma.db :refer [h2]]
            [de.sveri.getless.db.entities :refer [weight]]
            [clojure.spec :as s])
  (:import (java.sql Timestamp)))

(defn get-weights [users_id]
  (select weight (where {:users_id users_id})
          (order :weighted_at :asc)))

(s/fdef save-weight :args (s/cat :weight-measure number? :date inst? :users_id number?))
(defn save-weight [weight-measure date users_id]
  (insert weight (values {:weight weight-measure
                          :weighted_at (new Timestamp (.getTime date))
                          :users_id users_id})))

;(defn get-user-by-email [email] (first (select users (where {:email email}) (limit 1))))
;(defn get-user-by-act-id [id] (first (select users (where {:activationid id}) (limit 1))))
;(defn get-user-by-id [id] (first (select users (where {:id id}) (limit 1))))
;
;(defn username-exists? [email] (some? (get-user-by-email email)))
;
;(defn create-user [email pw_crypted activationid & [is-active?]]
;  (insert users (values {:email email :pass pw_crypted :activationid activationid :is_active (or is-active? false)})))
;
;(defn set-user-active [activationid & [active]]
;  (update users (set-fields {:is_active (or active true)}) (where {:activationid activationid})))
;
;(defn update-user [id fields] (update users (set-fields fields) (where {:id id})))
;(defn delete-user [id] (delete users (where {:id id})))
;(defn change-password [email pw] (update users (set-fields {:pass pw}) (where {:email email})))
