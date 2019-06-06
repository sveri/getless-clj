(ns de.sveri.getless.core
  (:require [clojure.tools.logging :as log]
            [de.sveri.getless.components.components :refer [prod-system]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defonce system (prod-system))

(defn -main [& args]
  (alter-var-root #'system component/start)
  (log/info "server started."))
