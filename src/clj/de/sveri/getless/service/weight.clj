(ns de.sveri.getless.service.weight
  (:require [clojure.spec :as s]))

(s/def ::weighted_at string?)
(s/def ::weight number?)
(s/def ::weight (s/keys :req-un [::weight ::weighted_at]))
(s/def ::weights (s/cat :weight (s/* ::weight)))

(s/fdef weight->date-js-string :args (s/cat :weights) :ret string?)
(defn weight->date-js-string [weights])
