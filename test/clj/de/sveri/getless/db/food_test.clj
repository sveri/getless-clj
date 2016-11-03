(ns de.sveri.getless.db.food-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as j]
            [de.sveri.getless.db.food :as db-food]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/getless-test?user=getless&password=getless"})

(defn db-setup [f]
  (j/execute! db ["truncate table food cascade"])
  (f))

(use-fixtures :each db-setup)


(defn insert-admin-user [db]
  (j/insert! db :users {:email "admin@localhost.de" :pass "bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575"
                        :is_active true :role "admin"}))



(deftest insert-food
  (let [admin-user (first (insert-admin-user db))
        _ (db-food/insert-food db 1476576000000 (:id admin-user) [1] [100] ["gramm"])
        foods (db-food/->food-by-user-id (:id admin-user) db)]
    (is (= "gramm"(-> foods first :unit)))
    (is (= 1 (-> foods first :product-id)))))
