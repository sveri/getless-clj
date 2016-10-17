(ns de.sveri.getless.service.off
  (:require [org.httpkit.client :as client]
            [clojure.spec :as s]
            [clojure.string :as str]
            [clojure.data.json :as json]))


(s/def ::name (s/and string? #(not= "" %)))
(s/def ::page-size integer?)
(s/def ::count integer?)
(s/def ::skip integer?)
(s/def ::page integer?)

(s/def ::id string?)
(s/def ::code string?)
(s/def ::image_thumb_url string?)
(s/def ::image_small_url string?)
(s/def ::product_name string?)
(s/def ::product_name_de string?)
(s/def ::brands string?)
(s/def ::lang string?)
(s/def ::ingredients_text_de string?)
(s/def ::packaging string?)
(s/def ::serving_quantity integer?)
(s/def ::nutrition_data_per string?)
(s/def ::quantity string?)
(s/def ::_keywords (s/cat :keywords (s/* string?)))
(s/def ::rev integer?)

(s/def ::text string?)
(s/def ::rank integer?)

(s/def ::ingredient (s/keys :opt-un [::text ::id ::rank]))
(s/def ::ingredients (s/* ::ingredient))
(s/def ::ingredients_text string?)

(s/def ::product (s/keys :req-un [::id ::code ::lang ::image_thumb_url ::image_small_url ::rev]
                         :opt-un [::product_name ::product_name_de ::brands ::ingredients_text_de ::packaging
                                  ::serving_quantity ::nutrition_data_per ::quantity ::ingredients ::_keywords
                                  ::name ::ingredients_text]))
(s/def ::products (s/coll-of ::product))

(s/def ::search-result (s/keys :req-un [::page-size ::count ::skip ::page ::products]))


(s/fdef add-string-fields :args (s/cat :product (s/keys :opt-un [::ingredients]))
        :ret (s/keys :req-un [::ingredients_text]))
(defn add-string-fields [product]
  (let [ingredients (get product :ingredients)
        ordered_ingredients (sort-by :rank ingredients)]
    (assoc product :ingredients_text
                   (reduce (fn [a b]
                             (str a (when-not (str/blank? a) ", ") (:text b)))
                           "" ordered_ingredients))))

(defn sanitize-product [product]
  (-> product
      (select-keys [:id :code :product_name :product_name_de :quantity :brands :image_thumb_url :image_small_url
                            :lang :ingredients :ingredients_text_de :packaging :serving_quantity :nutrition_data_per
                            :rev :_keywords :name])
      add-string-fields))


(s/fdef sanitize-products :args (s/cat :products any?) :ret ::products)
(defn sanitize-products [products]
  (map sanitize-product products))

(s/fdef search-products :args (s/cat :search string? :off-ur string? :off-user string? :off-password string?)
        :ret ::search-result)
(defn search-products [search off-url off-user off-password]
  (let [search-uri (str off-url "cgi/search.pl?search_terms="  search "&search_simple=1&json=1&page_size=1000")
        json-body (json/read-str (:body @(client/request {:url search-uri :basic-auth [off-user off-password]}))
                                 :key-fn keyword)
        sanitized-products (sanitize-products (get json-body :products))]
    (assoc json-body :products sanitized-products)))


(s/fdef get-by-id :args (s/cat :id (s/or :int integer? :str string?) :off-ur string? :off-user string? :off-password string?)
        :ret ::product)
(defn get-by-id [id off-url off-user off-password]
  (let [uri (str off-url "api/v0/product/" id ".json")
        product (json/read-str (:body @(client/request {:url uri :basic-auth [off-user off-password]}))
                               :key-fn keyword)]
    (sanitize-product (get product :product {}))))



