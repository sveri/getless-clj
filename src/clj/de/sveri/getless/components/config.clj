(ns de.sveri.getless.components.config
  (:require [com.stuartsierra.component :as component]
            [de.sveri.getless.service.config :as s-c]))

(defn prod-conf-or-dev []
  (s-c/prod-conf-or-dev))


(defrecord Config [config]
  component/Lifecycle
  (start [component]
    (assoc component :config config))
  (stop [component]
    (assoc component :config nil)))

(defn new-config [config]
  (->Config config))
