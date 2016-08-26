(ns getless.dev
  (:require [schema.core :as s]
            [de.sveri.getless.core :as core]))

(s/set-fn-validation! true)

(enable-console-print!)

(defn main [] (core/main))
