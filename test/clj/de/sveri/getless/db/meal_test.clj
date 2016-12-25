(ns de.sveri.getless.db.meal-test
  (:require [clojure.test :refer :all]
            [de.sveri.getless.db.meal :as db-meal]
            [de.sveri.getless.service.meal :as s-meal]
            [clojure.java.jdbc :as j]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/getless-test?user=getless&password=getless"})

(defn insert-admin-user [db]
  (j/insert! db :users {:id 1 :email "admin@localhost.de" :pass "bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575"
                        :is_active true :role "admin"}))


(defn db-setup [f]
  (j/execute! db ["truncate table meal cascade"])
  (j/execute! db ["truncate table users cascade"])
  (insert-admin-user db)
  (f))

(use-fixtures :each db-setup)

(def test-products [{:product-id 1, :amount 100, :unit "gramm"}
                    {:product-id 2, :amount 200, :unit "gramm"}])

(deftest insert-into-meal
  (let [_ (db-meal/insert-meal db 1 "foo" test-products)
        meal-from-db (db-meal/meals-by-user-id 1 db)]
    (is (= [{:product-id 1, :amount 100, :unit "gramm"}
            {:product-id 2, :amount 200, :unit "gramm"}]
           (:products-edn (first meal-from-db))))))

