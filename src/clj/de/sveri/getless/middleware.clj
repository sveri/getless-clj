(ns de.sveri.getless.middleware
  (:require [prone.middleware :as prone]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [taoensso.tempura :refer [tr] :as tempura]
            [noir.session :as sess]
            [de.sveri.clojure.commons.middleware.util :refer [wrap-trimmings]]
            [clojure-miniprofiler :refer [wrap-miniprofiler in-memory-store]]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [de.sveri.getless.service.auth :refer [auth-session-backend]]
            [de.sveri.getless.service.auth :as auth]
            [de.sveri.getless.locale :as loc]
            [clojure.spec.test :as stest]))

(defonce in-memory-store-instance (in-memory-store))

(defn add-req-properties [handler config]
  (fn [req]
    (sess/put! :registration-allowed? (:registration-allowed? config))
    (sess/put! :captcha-enabled? (:captcha-enabled? config))
    (handler req)))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (tempura/parse-http-accept-header accept-language)]
      (sess/put! :exact-locale (first short-languages))
      (sess/put! :short-locale (subs (first short-languages) 0 2))
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             short-languages))))))

(def development-middleware
  [#(wrap-miniprofiler % {:store in-memory-store-instance})
   #(prone/wrap-exceptions % {:app-namespaces ['de.sveri]})
   wrap-reload])

(defn production-middleware [config]
  [#(add-req-properties % config)
   add-locale
   #(wrap-access-rules % {:rules auth/rules})
   #(wrap-authorization % auth/auth-session-backend)
   #(wrap-transit-response % {:encoding :json :opts {}})
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware [config]
  (when (= (:env config) :dev) (stest/instrument))
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))
