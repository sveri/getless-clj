(ns de.sveri.getless.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [de.sveri.getless.layout :as layout]
            [ring.util.response :refer [response redirect]]))

(defn home-page []
  (layout/render "home/index.html"))

(defn contact-page []
  (layout/render "home/contact.html"))

(defn tos-page []
  (layout/render "home/dataprivacypolicy.html"))

(defn ajax-initial-data []
  (response {:ok "fooo" :loaded true}))

(defroutes home-routes
           (GET "/contact" [] (contact-page))
           (GET "/dataprivacypolicy" [] (tos-page))
           (GET "/" [] (home-page))
           (GET "/ajax/page/init" [] (ajax-initial-data)))
