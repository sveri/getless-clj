(ns de.sveri.getless.service.food
  (:require [clojure.spec :as s]
            [de.sveri.getless.service.session :as sess]
            [de.sveri.getless.db.food :as db-food]
            [de.sveri.getless.service.off :as s-off]
            [de.sveri.getless.service.user :as s-user]
            [clojure.spec :as s])
  (:import (java.text SimpleDateFormat)))



(s/def ::grouped-foods-with-products (s/coll-of (s/coll-of (s/merge ::db-food/food (s/keys :req-un [::s-off/product])))))

(s/def ::eaten-at inst?)

(s/def ::nutriments-grouped-by-date
  (s/coll-of (s/keys :opt-un [::eaten-at ::s-off/sugars ::s-off/energy-kcal ::s-off/fat_100g])))


(s/fdef add-products-to-session :args (s/cat :session ::sess/session-map :products ::s-off/products))
(defn add-products-to-session [session products]
  (update-in session [:getless :food :products] concat products))


(s/fdef get-food-from-session :args (s/cat :session ::sess/session-map))
(defn get-food-from-session [session]
  (get-in session [:getless :food :products] {}))

(s/fdef add-food-to-session :args (s/cat :session ::sess/session-map :product ::s-off/product) :ret ::sess/session-map)
(defn add-food-to-session [session product]
  (update-in session [:getless :food :products] conj product))


(s/fdef remove-product-from-session :args (s/cat :session ::sess/session-map :product-id ::s-off/id)
        :ret ::sess/session-map)
(defn remove-product-from-session [session product-id]
  (let [cleaned-products (vec (remove #(= (:id %) product-id) (get-in session [:getless :food :products])))]
    (assoc-in session [:getless :food :products] cleaned-products)))


(defn remove-products-from-session [session]
  (update-in session [:getless :food] dissoc :products))


;(s/fdef foods->group-by-date :args (s/cat :foods ::db-food/foods) :ret (s/coll-of (s/coll-of ::db-food/foods)))
(defn foods->group-by-date [foods]
  (mapv (fn [[_ s]] s)
        (seq
          (group-by
            (fn [{:keys [:eaten-at]}]
              (.format (SimpleDateFormat. "yyyyMMdd") eaten-at))
            foods))))

(def fetch-last-x-foods-from-user 500)
(def default-last-x-days 100)


(s/fdef sort-grouped-products-by-date :args (s/cat :products (s/coll-of (s/coll-of (s/keys :req-un [::db-food/eaten-at])))
                                                   :date-key keyword?)
        :ret (s/coll-of (s/coll-of (s/keys :req-un [::db-food/eaten-at]))))
(defn sort-grouped-products-by-date [products date-key]
  (let [inner-sorted-products (mapv (fn [productlist] (reverse (sort-by #(.getTime (date-key %)) productlist)))
                                    products)]
    (vec (reverse (sort-by #(.getTime (date-key (first %))) inner-sorted-products)))))


(s/fdef ->foods-with-product-grouped-by-date :ret (s/coll-of ::db-food/foods))
(defn ->foods-with-product-grouped-by-date
  [db off-url off-user off-password & [last-x-days]]
  (let [foods-grouped-by-date (-> (s-user/get-logged-in-user-id db)
                                  (db-food/->food-by-user-id db fetch-last-x-foods-from-user)
                                  (s-off/add-product off-url off-user off-password)
                                  foods->group-by-date
                                  (sort-grouped-products-by-date :eaten-at))
        last-x-days' (or last-x-days default-last-x-days)
        last-x-days'' (if (< (count foods-grouped-by-date) last-x-days')
                        (count foods-grouped-by-date)
                        last-x-days')]
    (subvec foods-grouped-by-date 0 last-x-days'')))



(s/fdef add-nutriment :args (s/cat :nutriments ::s-off/nutriments :amount number?
                                   :summarized_nutriment (s/nilable number?) :nutriment_key keyword?))
(defn add-nutriment [nutriments amount summarized_nutriment nutriment_key]
  (let [nutriment_all (or summarized_nutriment 0)
        cur_nutriment_number (if (number? (nutriment_key nutriments))
                               (nutriment_key nutriments)
                               (read-string (or (nutriment_key nutriments) "0")))
        nutriment (double (* cur_nutriment_number (/ amount 100)))]
    (+ nutriment_all nutriment)))


(s/fdef ->nutriments-grouped-by-date :args (s/cat :foods ::grouped-foods-with-products)
        :ret ::nutriments-grouped-by-date)
(defn ->nutriments-grouped-by-date [foods]
  (mapv #(reduce
          (fn [{:keys [sugars_100g energy-kcal fat_100g]} {:keys [amount eaten-at product] :as food}]
            (let [part-add-nutr-fn (partial add-nutriment (:nutriments product) amount)]
              {:eaten-at    eaten-at
               :sugars_100g (part-add-nutr-fn sugars_100g :sugars_100g)
               :energy-kcal (part-add-nutr-fn energy-kcal :energy-kcal)
               :fat_100g (part-add-nutr-fn fat_100g :fat_100g)}))
          {} %) foods))
