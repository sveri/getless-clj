(ns de.sveri.getless.user
  (:require [de.sveri.getless.components.components :refer [dev-system]]
            [system.repl :refer [system set-init! start stop reset]]
            [clojure.tools.logging :as log]))

(defn startup []
  (set-init! #'dev-system)
  (start))