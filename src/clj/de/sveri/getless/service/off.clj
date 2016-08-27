(ns de.sveri.getless.service.off
  (:require [org.httpkit.client :as client]
            [clojure.spec :as s]
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
(s/def ::ingredients string?)
(s/def ::ingredients_text_de string?)
(s/def ::packaging string?)
(s/def ::serving_quantity string?)
(s/def ::nutrition_data_per string?)
(s/def ::quantity string?)
(s/def ::_keywords (s/cat :keywords (s/* string?)))
(s/def ::rev integer?)


(s/def ::product (s/keys :req-un [::id ::code ::lang ::image_thumb_url ::image_small_url ::rev]
                         :opt-un [::product_name ::product_name_de ::brands ::ingredients_text_de ::packaging
                                  ::serving_quantity ::nutrition_data_per ::quantity ::ingredients ::_keywords]))
(s/def ::products (s/cat :product (s/* ::product)))

(s/def ::search-result (s/keys :req-un [::page-size ::count ::skip ::page ::products]))


(s/fdef sanitize-products :args (s/cat :products any?) :ret ::products)
(defn sanitize-products [products]
  (map #(select-keys % [:id :code :product_name :product_name_de :quantity :brands :image_thumb_url :image_small_url
                        :lang :ingredients :ingredients_text_de :packaging :serving_quantity :nutrition_data_per
                        :rev ::_keywords]) products))

(s/fdef search-products :args (s/cat :search string? :off-ur string? :off-user string? :off-password string?)
        :ret ::search-result)
(defn search-products [search off-url off-user off-password]
  (let [search-uri (str off-url "cgi/search.pl?search_terms="  search "&search_simple=1&json=1&page_size=1000")
        json-body (json/read-str (:body @(client/request {:url search-uri :basic-auth [off-user off-password]}))
                                 :key-fn keyword)
        sanitized-products (sanitize-products (get json-body :products))]
    (assoc json-body :products sanitized-products)))



