(ns de.sveri.getless.service.spec-validation
  (:require [clojure.spec :as s]))

(def not-blank '(not (blank? %)))


(defmulti parse-pred (fn [pred] pred))

(defmethod parse-pred not-blank [_]
  " should not be empty.")

(defn get-via [problem]
  (str "\"" (-> problem :via first name) "\""))

(defn parse-problem [problem]
  (let [via (get-via problem)
        pred (parse-pred (:pred problem))]
    (str via pred)))

(defn parse-problems [problems]
  (reduce (fn [acc b] (parse-problem b)) "" problems))




;(s/fdef validate-new-meal :args (s/cat :to-validate any? :spec any? :argss (s/& (s/* any?) #(even? (count %)))))
(s/fdef validate-new-meal :args (s/cat :to-validate any? :spec any?))
(defn validate [to-validate spec]
  (when-let [expl (s/explain-data spec to-validate)]
    (let [problems (::s/problems expl)]
      (parse-problems problems))))
