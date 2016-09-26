(ns de.sveri.getless.routes.rest-test
  (:require [clojure.test :refer :all]
            [org.httpkit.client :as cl]
            [clojure.data.json :as json]
            [de.sveri.getless.setup :as s]))


(use-fixtures :each s/clean-db)
(use-fixtures :once s/server-setup)

;(defn get [url]
;  @(cl/request {:method :get :url (str s/test-base-url "api/" url)}))

(defn post [url params]
  @(cl/request {:method :post :url (str s/test-base-url "api/" url) :body params :headers {"Content-Type" "application/json"}}))

(deftest ^:rest login
  (println (-> (post "login" (json/write-str {:username "admin@localhost.de" :password "admin"})) :body)))


