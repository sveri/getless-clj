(ns de.sveri.getless.service.session
  (:require [clojure.spec :as s]
            [de.sveri.getless.db.meal :as db-m]))

(s/def ::getless (s/keys :opt [::db-m/meal]))

(s/def ::session-map (s/keys :opt [::getless]))
