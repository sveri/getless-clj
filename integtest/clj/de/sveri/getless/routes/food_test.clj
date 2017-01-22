(ns de.sveri.getless.routes.food-test
  (:require [clojure.test :refer :all]
            [kerodon.core :as k]
            [de.sveri.getless.setup :as s]
            [de.sveri.getless.components.handler :as h]))



(use-fixtures :each s/clean-db)
(use-fixtures :once s/server-setup)

(deftest ^:integration retrieve-all-foods
  ;(reloaded.repl/set-init! s/test-system)
  ;(reloaded.repl/go))
  ;(s/start-server)
  (clojure.pprint/pprint (:handler (:handler reloaded.repl/system)))
  (-> (k/session (:handler (:handler reloaded.repl/system)))
      (k/visit "/")))
