(ns de.sveri.getless.service.spec-common
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::required-name (s/and string? #(not (str/blank? %))))
