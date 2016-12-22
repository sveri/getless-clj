(ns de.sveri.getless.service.activity
  (:require [clojure.spec :as s]
            [de.sveri.getless.db.activity :as db-act]
            [clj-time.core :as t-core]
            [clj-time.coerce :as t-coerce]))

(s/def ::tomorrow inst?)
(s/def ::today inst?)
(s/def ::yesterday inst?)

(s/fdef get-three-dates :ret (s/keys :req-un [::today ::yesterday ::tomorrow]))
(defn get-three-dates []
  {:today     (t-coerce/to-date (t-core/today))
   :yesterday (t-coerce/to-date (t-core/minus (t-core/today) (t-core/days 1)))
   :tomorrow  (t-coerce/to-date (t-core/plus (t-core/today) (t-core/days 1)))})


(s/fdef pad-with-tomorrow-today-yesterday-if-needed :args (s/cat :activities ::db-act/activities)
        :ret ::db-act/activities)
(defn pad-with-tomorrow-today-yesterday-if-needed [activities]
  (if (empty? activities)
    activities
    (let [three-dates (get-three-dates)
          first-activity-date (-> activities first :for-date)]
      (cond
        (= (:tomorrow three-dates) first-activity-date)
        activities

        (= (:today three-dates) first-activity-date)
        (cons {} (vec activities))

        :else (cons {} (cons {} (vec activities)))))))





