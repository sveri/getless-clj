(ns de.sveri.getless.middleware
  (:require [prone.middleware :as prone]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [taoensso.tempura :refer [tr] :as tempura]
            [noir.session :as sess]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [de.sveri.getless.service.auth :refer [auth-session-backend]]
            [de.sveri.getless.service.auth :as auth]
            [de.sveri.getless.locale :as loc]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as s]
            [clojure.walk :refer [prewalk]]))

(def trim-param-list [:params :form-params :edn-params])

(defn- trim-params [req p-list]
  (if (= :post (:request-method req))
    (let [prewalk-trim #(if (string? %) (s/trim %) %)]
      (reduce (fn [m k] (assoc m k (prewalk prewalk-trim (get-in req [k])))) req p-list))
    req))

(defn wrap-trimmings
  "string/trim every parameter in :params or :form-params"
  [handler]
  (fn [req] (handler (trim-params req trim-param-list))))

;(defonce in-memory-store-instance (in-memory-store))

(defn add-req-properties [handler config]
  (fn [req]
    (sess/put! :registration-allowed? (:registration-allowed? config))
    (sess/put! :captcha-enabled? (:captcha-enabled? config))
    (handler req)))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          parsed-languages (or (tempura/parse-http-accept-header accept-language) ["en"])]
      (sess/put! :exact-locale (first parsed-languages))
      (sess/put! :short-locale (subs (first parsed-languages) 0 2))
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             parsed-languages))))))

(def development-middleware
  [#(prone/wrap-exceptions % {:app-namespaces ['de.sveri]})
     wrap-reload]) ; still needed with tools-deps

(defn production-middleware [config]
  [#(add-req-properties % config)
   add-locale
   #(wrap-access-rules % {:rules auth/rules})
   #(wrap-authorization % auth/auth-session-backend)
   ;#(wrap-restful-format % :formats [:json-kw :transit-json])
   #(wrap-transit-response % {:encoding :json :opts {}})
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware [config]
  (when (= (:env config) :dev) (stest/instrument))
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))
