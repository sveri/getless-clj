(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]
            [clj-time.core :as time-c]
            [clj-time.format :as time-f]))

(def postgres-date-formatter (time-f/formatter "yyyy-MM-dd HH:mm:ss.SS"))
(def weight-date-format (time-f/formatter "dd.MM.yyyy"))

(s/def ::weighted-at inst?)
(s/def ::weight number?)
(s/def ::weight-map (s/keys :req-un [::weight ::weighted-at]))
(s/def ::weights (s/cat :weight-map (s/* ::weight-map)))

(s/fdef weight->js-string :args (s/cat :k #{:weighted-at :weight} :weights (s/spec ::weights))
        :ret string?)
(defn weight->js-string [k weights]
  (str (reduce (fn [acc weight] (str acc (when-not (= acc "[") ",")
                                     "\""
                                     (k weight) "\"")) "[" weights)
       "]"))

(s/fdef format-weighted-at :args (s/cat :weights (s/spec ::weights) :from-formatter any? :to-formatter any?)
        :ret ::weights)
(defn format-weighted-at [weights from-formatter to-formatter]
  (mapv (fn [w-map]
          (assoc w-map :weighted-at
                       (->> w-map :weighted-at (time-f/parse from-formatter) (time-f/unparse to-formatter))))
        weights))
