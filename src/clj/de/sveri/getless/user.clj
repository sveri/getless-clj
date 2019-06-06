(ns de.sveri.getless.user
  (:require [clojure.tools.namespace.repl :as tn]
            [de.sveri.getless.components.components :refer [dev-system]]
            [com.stuartsierra.component :as component]))


(defonce system (dev-system))

(defn start
  []
  (alter-var-root #'system component/start-system)
  :started)

(defn stop
  []
  (alter-var-root #'system component/stop-system)
  :stopped)

(defn reset []
  (stop)
  (tn/refresh-all)
  (start))
