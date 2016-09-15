(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-f]
            [de.sveri.getless.db.weight :as db-w]))

(def weight-date-format (time-f/formatter "dd.MM.yyyy"))



(s/fdef weight->js-string :args (s/cat :k #{:weighted_at :weight} :weights (s/spec ::db-w/weights))
        :ret string?)
(defn weight->js-string [k weights]
  (str (reduce (fn [acc weight] (str acc (when-not (= acc "[") ",")
                                     "\""
                                     (k weight) "\"")) "[" weights)
       "]"))

(s/fdef format-weighted-at :args (s/cat :weights (s/spec ::db-w/weights) :to-formatter any?)
        :ret ::db-w/weights)
(defn format-weighted-at [weights to-formatter]
  (mapv (fn [w-map]
          (assoc w-map :weighted_at
                       (->> w-map :weighted_at (time-coerce/from-date) (time-f/unparse to-formatter))))
        weights))
