(ns de.sveri.getless.components.locale
  (:require [com.stuartsierra.component :as component]))


(def my-tempura-dictionary
  {:de {:missing ":de missing text"
        :__load-resource "resources/i18/de.clj"}
   :en {:missing ":en missing text"
        :__load-resource "resources/i18/en.clj"}})

  ;{:en-GB ; Locale
  ; {:missing ":en-GB missing text" ; Fallback for missing resources
  ;  :example ; You can nest ids if you like
  ;           {:greet "Good day %1!"}} ; Note Clojure fn-style %1 args
  ;
  ;
  ; :en ; A second locale
  ; {:missing ":en missing text"
  ;  :example
  ;           {:greet "Hello %1"
  ;            :farewell "Goodbye %1"
  ;            :foo "foo"
  ;            :bar "bar"
  ;            :bar-copy :en.example/bar ; Can alias entries
  ;            :baz [:div "This is a **Hiccup** form"]
  ;
  ;            ;; Can use arbitrary fns as resources
  ;            :qux (fn [[arg1 arg2]] (str arg1 " and " arg2))}
  ;
  ;  :example-copy :en/example ; Can alias entire subtrees
  ;
  ;  :import-example
  ;  {:__load-resource ; Inline edn content loaded from disk/resource
  ;   "resources/i18n.clj"}}})

(def tconfig
  {:fallback-locale :en
   :dictionary      {:en
                     {:generic
                      {:some_error        "Some error occured."
                       :deletion_canceled "Deletion canceled."}
                      :user
                      {:email_invalid           "A valid email is required."
                       :pass_min_length         "Password must be at least 5 characters."
                       :pass_match              "Entered passwords do not match."
                       :pass_correct            "Please provide a correct password."
                       :pass_changed            "Password changed."
                       :wrong_cur_pass          "Current password was incorrect."
                       :username_exists         "This username already exists. Choose another."
                       :username_wrong          "Please provide a correct username."
                       :deleted                 "User deleted successfully."
                       :updated                 "User %s updated successfully."
                       :user_added              "User added."
                       :captcha_wrong           "Please provide the correct captcha input."
                       :email_failed            "Something went wrong sending the email, please contact us."
                       :signup_title            "Signup"}

                      :admin
                      {:title "User Overview"}}}})


(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] (dissoc component :tconfig)))

(defn new-locale []
  (map->Locale {}))
