(ns de.sveri.getless.user
  (:require [reloaded.repl :refer [go reset stop]]
          [de.sveri.getless.components.components :refer [dev-system]]))

(defn start-dev-system []
  (reloaded.repl/set-init! dev-system)
  (go))

