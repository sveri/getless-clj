(ns de.sveri.getless.routes.food
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.response :refer [response redirect]]
            [de.sveri.getless.layout :as layout]
            [de.sveri.getless.db.food :as db-food]
            [de.sveri.getless.db.meal :as db-meal]
            [de.sveri.getless.service.user :as s-user]
            [de.sveri.getless.service.food :as s-food]
            [de.sveri.getless.service.off :as s-off]
            [de.sveri.getless.service.meal :as s-meal]
            [clojure.instant :as inst]
            [clojure.tools.logging :as log]))

(defn index-page [db {:keys [off-url off-user off-password]} {:keys [localize]}]
  (let [products-list (s-food/->foods-with-product-grouped-by-date db off-url off-user off-password 10)
        products-with-nutriments (mapv (fn [products]
                                         (mapv
                                           #(assoc % :product (s-off/add-nutriments (:product %) localize s-off/nutriments-to-extract))
                                           products))
                                       products-list)]
    (layout/render "food/index.html" {:products-list products-with-nutriments
                                      :delete-uri    "/food/delete/database/"})))
  

(defn add-food-page [{:keys [session localize]}]
  (let [products (s-food/get-food-from-session session)
        products-with-nutriments (mapv #(s-off/add-nutriments % localize s-off/nutriments-to-extract) products)]
    (if (empty? products)
      (redirect "/off/search")
      (layout/render "food/add-food.html" {:products products-with-nutriments}))))


(defn add-meal-to-session [db {:keys [session localize]} mealid {:keys [off-url off-user off-password]}]
  (let [products (s-meal/get-products-from-meal db (read-string mealid) off-url off-user off-password)
        products-with-nutriments (mapv #(s-off/add-nutriments % localize s-off/nutriments-to-extract) products)]
    (assoc (redirect "/food/add") :session (s-food/add-products-to-session session products-with-nutriments))))


(defn add-food-from-template-page [db {:keys [off-url off-user off-password]}]
  (let [user-id (s-user/get-logged-in-user-id db)
        meals (db-meal/meals-by-user-id user-id db)
        meals-with-products (mapv
                              (fn [meal]
                                (let [products-in-meal (:products-edn meal)
                                      products-with-off-products (s-off/add-product products-in-meal off-url off-user off-password)]
                                  (assoc meal :products-edn products-with-off-products)))
                              meals)]
    (layout/render "food/add-food-from-template.html" {:meals meals-with-products})))


(defn add-product-to-session [productid {:keys [session]} {:keys [off-url off-user off-password]}]
  (let [session (s-food/add-food-to-session session (s-off/get-by-id productid off-url off-user off-password))]
    (assoc (redirect "/food/add") :session session)))


(defn save-food-to-db-or-as-template [save_or_template meal-name date productids amounts units {:keys [session]} db]
  (let [user-id (s-user/get-logged-in-user-id db)
        amount-numbers (mapv read-string amounts)
        productid-numbers (mapv read-string productids)]
    (try
      (if (= "Speichern" save_or_template)
        (db-food/insert-food db (.getTime (inst/read-instant-date date)) user-id productid-numbers
                                amount-numbers units)
        (db-meal/insert-meal db user-id meal-name (s-meal/foods-to-products productid-numbers amount-numbers units)))
      (catch Exception e
        (layout/flash-result "Etwas schlug beim Speichern fehl." "alert-danger")
        (log/error "Error adding food")
        (.printStackTrace e)))
    (assoc (redirect "/food") :session (s-food/remove-products-from-session session))))


(defn delete-product-from-session [productid {:keys [session]}]
  (assoc (redirect "/food/add") :session (s-food/remove-product-from-session session productid)))


(defn delete-product-from-database [productid db]
  (try
    (db-food/delete-food db (read-string productid) (s-user/get-logged-in-user-id db))
    (catch Exception e
      (log/error "Error deleting food")
      (.printStackTrace e)))
  (redirect "/food"))


(defn contents-page [db {:keys [off-url off-user off-password]}]
  (layout/render "food/contents.html"
                 {:nutriments (-> (s-food/->foods-with-product-grouped-by-date db off-url off-user off-password)
                                  s-food/->nutriments-grouped-by-date)}))



(defn food-routes [config db]
  (routes
    (GET "/food" req (index-page db config req))
    (GET "/food/add" req (add-food-page req))
    (GET "/food/add/:mealid" [mealid :as req] (add-meal-to-session db req mealid config))
    (GET "/food/add-from-template" [] (add-food-from-template-page db config))
    (POST "/food/add" [save_or_template meal-name date productid amount unit :as req]
      (save-food-to-db-or-as-template save_or_template meal-name date productid amount unit req db))
    (GET "/food/add/product/:productid" [productid :as req] (add-product-to-session productid req config))
    (GET "/food/delete/session/:productid" [productid :as req] (delete-product-from-session productid req))
    (GET "/food/delete/database/:productid" [productid] (delete-product-from-database productid db))
    (GET "/food/contents" req (contents-page db config))))
