(ns de.sveri.getless.components.selmer
  (:require [com.stuartsierra.component :as component]
            [selmer.parser :as sp]
            [selmer.filters :as sf]
            [clojure.string :as s]
            [de.sveri.getless.service.off :as s-off]))

(defn localize-tag []
  (sp/add-tag!
    :localize
    (fn [args context-map]
      ((:localize-fn context-map) [(keyword (first args))] (vec (rest args))))))

(defn active-tag []
  (sp/add-tag!
    :active
    (fn [args context-map]
      (let [page (:page context-map)]
        (when (some #{true} (mapv #(s/includes? page %) args))
          "active")))))
;(clojure.pprint/pprint  (type (vec args)))
;(clojure.pprint/pprint (:page context-map)))))
;((:active- context-map) [(keyword (first args))] (vec (rest args))))))

(defn timestamp-filter []
  (sf/add-filter!
    :timestamp
    (fn [x]
      (.getTime x))))


(defrecord Selmer [template-caching?]
  component/Lifecycle
  (start [component]
    (localize-tag)
    (timestamp-filter)
    (active-tag)
    (if template-caching?
      (sp/cache-on!)
      (sp/cache-off!))
    component)
  (stop [component] component))

(defn new-selmer [template-caching?]
  (->Selmer template-caching?))
