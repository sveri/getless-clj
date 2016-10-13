(ns de.sveri.getless.service.spec-validation
  (:require [clojure.spec :as s]
            [clojure.string :as str]))

(def not-blank '(not (blank? %)))


(defmulti parse-pred (fn [pred] pred))

(defmethod parse-pred not-blank [_]
  " should not be empty.")

(defmethod parse-pred :default [_]
  "")

(defn get-via [problem]
  (str "\"" (-> problem :via first name) "\""))


(defn parse-problem [problem]
  (let [via (get-via problem)
        pred (parse-pred (:pred problem))]
    (str via pred)))

(defn parse-problems [problems]
  (reduce (fn [_ problem] (parse-problem problem)) "" problems))


(defmulti dispatch-on-spec-type
          (fn [_ spec _]
            (let [description (s/describe spec)] ; use explain-date
              (if (seq? description)
                (first description)
                description))))

(defmethod dispatch-on-spec-type 'or [to-validate spec problems]
  (str "\""to-validate"\" is not a valid type. Expecting: " (get-via (first problems))))

(defmethod dispatch-on-spec-type 'number? [to-validate spec problems]
  (str "\""to-validate"\" is not a number."))


(defmethod dispatch-on-spec-type :default [to-validate spec problems]
  (parse-problems problems))




;(s/fdef validate-new-meal :args (s/cat :to-validate any? :spec any? :argss (s/& (s/* any?) #(even? (count %)))))
(s/fdef validate :args (s/cat :to-validate any? :spec any?))
(defn validate [to-validate spec]
  (when-let [expl (s/explain-data spec to-validate)]
    (let [problems (::s/problems expl)]
      (dispatch-on-spec-type to-validate spec problems))))


;(s/* (s/cat :x1 any? :x2 any?))

(s/fdef validate-specs :args (s/cat :v (s/* (s/cat :to-validate any? :spec any?))))
(defn validate-specs [ & v]
  (str/trim (str/join "\n" (map (fn [[to-validate spec]] (validate to-validate spec)) (partition 2 v)))))