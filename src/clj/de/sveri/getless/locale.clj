(ns de.sveri.getless.locale
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io PushbackReader)))

(defn from-edn [fname]
  "reads an edn file from classpath"
  (with-open [rdr (-> (io/resource fname)
                      io/reader
                      PushbackReader.)]
    (edn/read rdr)))

(def local-dict
  {:de (from-edn "i18n/de.edn")
   :en (from-edn "i18n/en.edn")
   :ru (from-edn "i18n/ru.edn")
   :nl (from-edn "i18n/nl.edn")})
