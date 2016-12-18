(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-f]
            [de.sveri.getless.db.weight :as db-w]
            [de.sveri.getless.service.food :as s-food])
  (:import (java.text SimpleDateFormat)))

(def weight-date-format (time-f/formatter "dd.MM.yyyy"))


(s/fdef weight->js-string :args (s/cat :k #{:weighted_at :weight} :weights (s/spec ::db-w/weights))
        :ret string?)
(defn weight->js-string [k weights]
  (str (reduce (fn [acc weight] (str acc (when-not (= acc "[") ",")
                                     "\""
                                     (k weight) "\"")) "[" weights)
       "]"))

(s/fdef format-weighted-at :args (s/cat :weights ::db-w/weights :to-formatter any?)
        :ret ::db-w/weights)
(defn format-weighted-at [weights to-formatter]
  (mapv (fn [w-map]
          (assoc w-map :weighted_at
                       (->> w-map :weighted_at (time-coerce/from-date) (time-f/unparse to-formatter))))
        weights))

(s/fdef merge-weights-and-nutriments :args (s/cat :weights ::db-w/weights :nutriments ::s-food/nutriments-grouped-by-date)
        :ret (s/coll-of (s/merge ::db-w/weights ::s-food/nutriments-grouped-by-date)))
(defn merge-weights-and-nutriments [weights nutriments]
  (let [grouped-maps (mapv (fn [[_ s]] s)
                       (seq
                         (group-by
                           (fn [m]
                             (.format (SimpleDateFormat. "yyyyMMdd") (get m :eaten-at (get m :weighted_at))))
                           (concat weights nutriments))))]
    (mapv #(reduce merge %) grouped-maps)))


;[{:weighted_at #inst "2016-09-02T00:00:00.000-00:00", :weight 90}
; {:weighted_at #inst "2016-09-03T00:00:00.000-00:00", :weight 91}
; {:weighted_at #inst "2016-09-05T00:00:00.000-00:00",
;  :weight 92,
;  :eaten-at #inst "2016-09-05T00:00:00.000-00:00",
;  :sugars_100g 20}
; {:weighted_at #inst "2016-09-08T00:00:00.000-00:00",
;  :weight 94,
;  :eaten-at #inst "2016-09-08T00:00:00.000-00:00",
;  :sugars_100g 30}
; {:weighted_at #inst "2016-09-09T00:00:00.000-00:00", :weight 94}
; {:eaten-at #inst "2016-09-01T00:00:00.000-00:00", :sugars_100g 10}]
