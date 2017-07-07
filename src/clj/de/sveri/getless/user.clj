(ns de.sveri.getless.user
  (:require [reloaded.repl :as sr]
            [clojure.tools.namespace.repl :as tn]
            [de.sveri.getless.components.components :refer [dev-system]]))

(defn start-dev-system []
  (sr/go))

(defn reset []
  (tn/refresh)
  (sr/reset))

(sr/set-init! #'dev-system)
