(ns de.sveri.getless.service.off
  (:require [org.httpkit.client :as client]
            [clojure.spec :as s]
            [clojure.data.json :as json]))


(s/def ::name (s/and string? #(not= "" %)))
(s/def ::page-size integer?)
(s/def ::count integer?)
(s/def ::skip integer?)
(s/def ::page integer?)

(s/def ::image_thumb_url string?)
(s/def ::product_name_de string?)

(s/def ::product (s/keys :req-un [::name ::image_thumb_url ::product_name_de]))
(s/def ::products (s/cat :product (s/* ::product)))

(s/def ::search-result (s/keys :req-un [::page-size ::count ::skip ::page ::products]))


(s/fdef sanitize-products :args (s/cat :products any?) :ret ::products)
(defn sanitize-products [products]
  (map #(select-keys % [:name :image_thumb_url :product_name_de]) products))

(s/fdef search-products :args (s/cat :search string? :off-ur string? :off-user string? :off-password string?)
        :ret ::search-result)
(defn search-products [search off-url off-user off-password]
  (let [search-uri (str off-url "cgi/search.pl?search_terms="  search "&search_simple=1&json=1&page_size=1000")
        json-body (json/read-str (:body @(client/request {:url search-uri :basic-auth [off-user off-password]})) :key-fn keyword)
        products (get json-body :products)
        sanitized-products (sanitize-products products)]
    (clojure.pprint/pprint (first (:products json-body)))
    (assoc json-body :products sanitized-products)))



