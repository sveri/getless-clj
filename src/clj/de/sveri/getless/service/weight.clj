(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-f]))

(def weight-date-format (time-f/formatter "dd.MM.yyyy"))

(s/def ::weighted_at inst?)
(s/def ::weight number?)
(s/def ::weight-map (s/keys :req-un [::weight ::weighted_at]))
(s/def ::weights (s/cat :weight-map (s/* ::weight-map)))

(s/fdef weight->js-string :args (s/cat :k #{:weighted_at :weight} :weights (s/spec ::weights))
        :ret string?)
(defn weight->js-string [k weights]
  (str (reduce (fn [acc weight] (str acc (when-not (= acc "[") ",")
                                     "\""
                                     (k weight) "\"")) "[" weights)
       "]"))

(s/fdef format-weighted-at :args (s/cat :weights (s/spec ::weights) :to-formatter any?)
        :ret ::weights)
(defn format-weighted-at [weights to-formatter]
  (mapv (fn [w-map]
          (assoc w-map :weighted_at
                       (->> w-map :weighted_at (time-coerce/from-date) (time-f/unparse to-formatter))))
        weights))
