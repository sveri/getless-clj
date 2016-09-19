(ns de.sveri.getless.service.config
  (:require [clojure.java.io :as io]
            [nomad :refer [read-config]]))

(defn read-config-from-nomad []
  (if-let [config-path (System/getProperty "closp-config-path")]
    (read-config (io/file config-path))
    (read-config (io/resource "closp.edn"))))
