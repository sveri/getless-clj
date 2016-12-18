(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-f]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.food :as s-food])
  (:import (java.text SimpleDateFormat)))

(def weight-date-format (time-f/formatter "dd.MM.yyyy"))
(s/def ::date inst?)

(s/fdef weight->js-string :args (s/cat :k #{:date :weight :sugars_100g} :weights (s/spec ::db-w/weights))
        :ret string?)
(defn weight->js-string [k data-maps]
  (str (reduce (fn [acc data-map] (str acc (when-not (= acc "[") ",")
                                     "\""
                                     (k data-map "null") "\"")) "[" data-maps)
       "]"))


(s/fdef format-weighted-at :args (s/cat :weights (s/coll-of (s/keys :req-un [::date])) :to-formatter any?)
        :ret (s/coll-of (s/keys :req-un [::date])))
(defn format-weighted-at [weights to-formatter]
  (mapv (fn [w-map]
          (assoc w-map :date
                       (->> w-map :date (time-coerce/from-date) (time-f/unparse to-formatter))))
        weights))


(s/fdef merge-weights-and-nutriments :args (s/cat :weights ::db-w/weights :nutriments ::s-food/nutriments-grouped-by-date)
        :ret (s/coll-of (s/merge ::db-w/weights ::s-food/nutriments-grouped-by-date (s/keys :req-un [::date]))))
(defn merge-weights-and-nutriments [weights nutriments]
  (let [grouped-maps (mapv (fn [[_ s]] s)
                       (seq
                         (group-by
                           (fn [m]
                             (.format (SimpleDateFormat. "yyyyMMdd") (get m :eaten-at (get m :weighted_at))))
                           (concat weights nutriments))))]
    (mapv (fn [gms] (let [grouped-gms (reduce merge gms)]
                      (assoc grouped-gms :date (get grouped-gms :eaten-at (get grouped-gms :weighted_at)))))
          grouped-maps)))
