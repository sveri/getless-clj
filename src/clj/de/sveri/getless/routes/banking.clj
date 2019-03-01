(ns de.sveri.getless.routes.banking
  (:require [compojure.core :refer [routes GET POST]]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.service.banking-import :as bi]
            [de.sveri.getless.service.user :as s-u]))

(defn links-page []
  (layout/render "banking/index.html"))



(defn upload-csv [config db filename req]
  (let [user-id (s-u/get-logged-in-user-id db)
        temp-file (-> req :params :filename :tempfile)]
    (with-open [reader (io/reader (.getPath temp-file) :encoding "Cp1252")]
      (bi/parse-and-insert-banking-data db user-id (csv/read-csv reader :separator \;)))
    (redirect "/banking")))

(defn banking-routes [config db]
  (routes (GET "/banking" [] (links-page))
          (POST "/banking/upload" [filename :as req] (upload-csv config db filename req))))
