(ns de.sveri.getless.components.selmer
  (:require [com.stuartsierra.component :as component]
            [selmer.parser :as sp]))

(defn localize-tag []
  (sp/add-tag!
   :localize
   (fn [args context-map]
     (clojure.pprint/pprint (:localize-fn context-map))
     (clojure.pprint/pprint (mapv keyword args))
     (str "foo " (first args)))))

(defrecord Selmer [template-caching?]
  component/Lifecycle
  (start [component]
    (localize-tag)
    (if template-caching?
      (sp/cache-on!)
      (sp/cache-off!))
    component)
  (stop [component] component))

(defn new-selmer [template-caching?]
  (->Selmer template-caching?))
