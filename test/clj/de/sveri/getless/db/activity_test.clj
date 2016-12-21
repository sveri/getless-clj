(ns de.sveri.getless.db.activity-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as j]
            [de.sveri.getless.db.activity :as db-act]
            [clj-time.core :as t-core]
            [clj-time.coerce :as t-coerce]
            [clojure.instant :as inst]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/getless-test?user=getless&password=getless"})

(defn db-setup [f]
  (j/execute! db ["truncate table activity cascade"])
  (j/execute! db ["truncate table users cascade"])
  (f))

(use-fixtures :each db-setup)


(defn insert-admin-user [db]
  (j/insert! db :users {:email "admin@localhost.de" :pass "bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575"
                        :is_active true :role "admin"}))



(deftest insert-activity
  (let [admin-user (first (insert-admin-user db))
        today (t-coerce/to-date (t-core/today))
        _ (db-act/insert-activity db "some text" today (:id admin-user))
        activities (db-act/get-activities db (:id admin-user))]
    (is (= (.getTime today) (.getTime (:for-date (first activities)))))))

(deftest insert-or-update-activity
  (let [admin-user (first (insert-admin-user db))
        today (t-coerce/to-date (t-core/today))
        first_content "some text"
        second_content "some text 2"
        _ (db-act/insert-or-update-activity db first_content today (:id admin-user))
        activities (db-act/get-activities db (:id admin-user))
        _ (db-act/insert-or-update-activity db second_content today (:id admin-user))
        second_activities (db-act/get-activities db (:id admin-user))]
    (is (= (.getTime today) (.getTime (:for-date (first activities)))))
    (is (= first_content (:content (first activities))))
    (is (= second_content (:content (first second_activities))))))
