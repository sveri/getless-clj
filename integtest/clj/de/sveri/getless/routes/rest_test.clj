(ns de.sveri.getless.routes.rest-test
  (:require [clojure.test :refer :all]
            [org.httpkit.client :as cl]
            [clojure.data.json :as json]
            [de.sveri.getless.setup :as s]))


(use-fixtures :each s/clean-db)
(use-fixtures :once s/server-setup)

(defn ->headers [& [auth-header]]
  (merge (if auth-header {"Authorization" (str "Token " auth-header)} {})
         {"Content-Type" "application/json"}))

(defn ->url [url]
  (str s/test-base-url "api/" url))

(defn post [url params & [auth-header]]
  @(cl/request {:method  :post :url (->url url) :body params
                :headers (->headers auth-header)}))

;(defn http-get [url & [auth-header]]
;  @(cl/request {:method :get :url (->url url) :headers (->headers auth-header)}))

(defn get-body [response]
  (-> response :body (json/read-str :key-fn keyword)))

(defn ->token-for-admin []
  (-> (post "login" (json/write-str {:username "admin@localhost.de" :password "admin"}))
      get-body
      :token))


(deftest ^:rest login
  (is (some? (-> (post "login" (json/write-str {:username "admin@localhost.de" :password "admin"}))
                 get-body
                 :token))))

(deftest ^:rest login-failed
  (is (= (-> (post "login" (json/write-str {:username "admin@localhost.de" :password "admi"}))
             get-body
             :error)
         "Unauthorized")))

(deftest ^:rest add-weight
  (is (= 90 (-> (post "weight" (json/write-str {:weight 90 :date 1475842686})
                      (->token-for-admin))
                get-body
                :weight))))

(deftest ^:rest add-weight-failed
  (is (= "\"90\" is not a number."
         (-> (post "weight" (json/write-str {:weight "90" :date 1475842685})
                   (->token-for-admin))
             get-body
             :validation))))
