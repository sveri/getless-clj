(ns de.sveri.getless.components.components
  (:require
    [com.stuartsierra.component :as component]
    [de.sveri.getless.components.server :refer [new-web-server]]
    [de.sveri.getless.components.handler :refer [new-handler]]
    [de.sveri.getless.components.config :as c]
    [de.sveri.getless.components.selmer :as selm]
    [de.sveri.getless.components.db :refer [new-db]]))


(defn dev-system []
  (component/system-map
    :config (c/new-config)
    :selmer (selm/new-selmer false)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))


(defn prod-system []
  (component/system-map
    :config (c/new-config)
    :selmer (selm/new-selmer true)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))
