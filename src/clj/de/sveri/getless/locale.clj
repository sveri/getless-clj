(ns de.sveri.getless.locale
  (:require [de.sveri.clojure.commons.files.edn :as comm-edn]))

(def local-dict
  {:de (comm-edn/from-edn "i18n/de.edn")
   :en (comm-edn/from-edn "i18n/en.edn")
   :ru (comm-edn/from-edn "i18n/ru.edn")})
