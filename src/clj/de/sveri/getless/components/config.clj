(ns de.sveri.getless.components.config
  (:require [com.stuartsierra.component :as component]
            [de.sveri.getless.service.config :as s-c]))

(defn prod-conf-or-dev []
  (s-c/prod-conf-or-dev))


(defrecord Config []
  component/Lifecycle
  (start [component]
    (assoc component :config (prod-conf-or-dev)))
  (stop [component]
    (assoc component :config nil)))

(defn new-config []
  (->Config))
