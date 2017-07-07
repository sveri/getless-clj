(ns de.sveri.getless.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.getless.layout :as layout]
            [taoensso.tempura :refer [tr]]
            [ring.util.response :refer [response redirect]]))

(defn home-page [{:keys [localize]}]
  (layout/render "home/index.html" {:rules [(localize [:home/we-dont-judge])
                                            (localize [:home/we-support-each-other])
                                            (localize [:home/we-use-means])]}))

(defn contact-page []
  (layout/render "home/contact.html"))

(defn tos-page []
  (layout/render "home/dataprivacypolicy.html"))

(defn ajax-initial-data []
  (response {:ok "fooo" :loaded true}))

(defn about-page [{:keys [localize]}]
  (layout/render "home/about.html" {:about-text (localize [:about/text])
                                    :paypal-text (localize [:about/paypal-text])}))


(defroutes home-routes
           (GET "/contact" [] (contact-page))
           (GET "/dataprivacypolicy" [] (tos-page))
           (GET "/" req (home-page req))
           (GET "/about" req (about-page req))
           (GET "/ajax/page/init" [] (ajax-initial-data)))
