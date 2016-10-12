(ns de.sveri.getless.core
  (:require [clojure.tools.logging :as log]
            [reloaded.repl :refer [go]]
            [de.sveri.getless.cljccore :as cljc]
            [de.sveri.getless.components.components :refer [prod-system]])
  (:gen-class))

(defn -main [& args]
  (reloaded.repl/set-init! prod-system)
  (go)
  (cljc/foo-cljc "hello from cljx")
  (log/info "server started."))
