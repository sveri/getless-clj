(ns de.sveri.getless.service.config
  (:require [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (edn/read-string
    (slurp (if-let [config-path (System/getProperty "closp-config-path")]
             (io/file config-path)
             (io/resource "closp.edn")))))
