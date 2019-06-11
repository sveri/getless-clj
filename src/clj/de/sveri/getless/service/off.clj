(ns de.sveri.getless.service.off
  (:require [clj-http.client :as client]
            [de.sveri.getless.db.food :as db-food]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [clojure.string :as str]
            [clojure.core.cache :as cache]
            [clojure.tools.logging :as log]
            [noir.session :as sess]))

(def product-cache (atom (cache/ttl-cache-factory {} :ttl 172800000)))
(def product-search-cache (atom (cache/ttl-cache-factory {} :ttl 82800000)))



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

(s/def ::unit #{"g" "mg" "kJ" "kCal" "kcal"})

(s/def ::string-or-number (s/or :number number? :string string?))

(s/def ::sugars_100g ::string-or-number)
(s/def ::sugars_unit ::unit)
(s/def ::fat_100g ::string-or-number)
(s/def ::fat_unit ::unit)
(s/def ::salt ::string-or-number)
(s/def ::salt_unit ::unit)
(s/def ::energy_100g ::string-or-number)
(s/def ::energy_unit ::unit)
(s/def ::energy-kcal ::string-or-number)
(s/def ::nutriments
  (s/nilable (s/keys :opt-un [::sugars_100g ::sugars_unit ::energy_100g ::energy_unit ::salt ::salt_unit ::fat_100g ::fat_unit])))


(s/def ::product (s/keys :req-un [::id]
                         :opt-un [::product_name ::code ::product_name_de ::brands ::ingredients_text_de ::packaging
                                  ::serving_quantity ::nutrition_data_per ::quantity ::ingredients ::_keywords
                                  ::name ::rev ::image_thumb_url ::image_small_url ::lang ::ingredients_text
                                  ::nutriments]))
(s/def ::products (s/coll-of ::product))

(s/def ::search-result (s/keys :opt-un [::page-size ::count ::skip ::page ::products]))



(def nutriments-to-extract ["energy" "fat" "saturated-fat" "carbohydrates" "sugars" "proteins" "salt"
                            "fiber" "cholesterol" "monounsaturated-fat" "polyunsaturated-fat"])


(defn get-non-empty-nutriments-to-extract [nutriments-to-extract' nutriments-from-product]
  (filter
    (fn [nte]
      (let [nutriment (str (get nutriments-from-product (keyword (str nte "_100g"))))]
        (and (not (str/blank? nutriment))
             (if (string? nutriment)
               (< 0 (read-string nutriment))
               (< 0 nutriment)))))
    nutriments-to-extract'))

(s/fdef add-nutriments :args (s/cat :product (s/keys :opt-un [::nutriments]) :localize fn?
                                    :nutriments-to-extract (s/coll-of string?))
        :ret (s/keys :req-un [::nutriments-sanitized]))
(defn add-nutriments [product localize nutriments-to-extract]
  (let [nutriments (:nutriments product)
        non-empty-nutriments (get-non-empty-nutriments-to-extract nutriments-to-extract nutriments)
        nutriments_text (str/join "<br />"
                                  (map (fn [nutriment-string]
                                         (cond
                                           (= "energy" nutriment-string)
                                           (str (localize [:food/energy]) ": " (format "%.3s" (read-string (:energy-kcal nutriments))) " kCal")

                                           :else
                                           (str (localize [(keyword (str "food/" nutriment-string))])
                                                ": " (get nutriments (keyword (str nutriment-string "_100g")) "0") " g")))
                                       non-empty-nutriments))]
    (assoc product :nutriments-text nutriments_text)))



(s/fdef add-ingredients :args (s/cat :product (s/keys :opt-un [::ingredients]))
        :ret (s/keys :req-un [::ingredients_text]))
(defn add-ingredients [product]
  (let [ingredients (get product :ingredients)
        ordered_ingredients (sort-by :rank ingredients)]
    (assoc product :ingredients_text
                   (reduce (fn [a b]
                             (str a (when-not (str/blank? a) ", ") (str/replace (:text b) #"\_|\ \*|\*|\*\ " "")))
                           "" ordered_ingredients))))


(defn kJ->kCal
  "adds a :energy-kcal key / value to the :nutriements map"
  [product]
  (if-let [kj (-> product :nutriments :energy_100g)]
    (assoc-in product [:nutriments :energy-kcal] (str (/ (if (number? kj) kj (read-string kj)) 4.187)))
    (assoc-in product [:nutriments :energy-kcal] "0")))


(defn sanitize-product [product]
  (-> product
      (select-keys [:id :code :product_name :product_name_de :quantity :brands :image_thumb_url :image_small_url
                    :lang :ingredients :ingredients_text_de :packaging :serving_quantity :nutrition_data_per
                    :rev :_keywords :name :nutriments])
      add-ingredients
      kJ->kCal))


(s/fdef sanitize-products :args (s/cat :products any?)
        :ret ::products)
(defn sanitize-products [products]
  (map sanitize-product products))



(defn get-search-products-from-cache-or-api [search only-one-locale off-url off-user off-password]
  (let [sanitized-search-term (.replace search " " "%20")
        cache-key (str sanitized-search-term (if only-one-locale (str "-" (sess/get :short-locale)) ""))]
    (if (cache/has? @product-search-cache cache-key)
      (cache/lookup @product-search-cache cache-key)
      (try
        (let [search-uri (str off-url "cgi/search.pl?search_terms=" sanitized-search-term
                              (if only-one-locale (str "&tagtype_0=countries&tag_contains_0=contains&tag_0=" (sess/get :short-locale)) "")
                              "&search_simple=1&json=1&page_size=1000")
              json-body (:body (client/get search-uri {:basic-auth [off-user off-password] :insecure? true :as :json}))
              sanitized-products (sanitize-products (get json-body :products))]
          (swap! product-search-cache #(cache/miss % cache-key (assoc json-body :products sanitized-products)))
          (assoc json-body :products sanitized-products))
        (catch Exception e
          (log/error "Cannot retrieve off products.")
          (.printStackTrace e))))))


(s/fdef search-products :args (s/cat :search string? :only-one-locale boolean? :off-url string?
                                     :off-user string? :off-password string?)
        :ret ::search-result)
(defn search-products [search only-one-locale off-url off-user off-password]
  (get-search-products-from-cache-or-api search only-one-locale off-url off-user off-password))



(defn get-by-id-from-cache-or-api [id off-url off-user off-password]
  (let [uri (str off-url "api/v0/product/" id ".json")]
    (if (cache/has? @product-cache id)
      (cache/lookup @product-cache id)
      (try
        (let [product (:body (client/get uri {:basic-auth [off-user off-password] :insecure? true :as :json}))]
          (swap! product-cache #(cache/miss % id product))
          product)
        (catch Exception e
          (log/error "Cannot retrieve off products for id: " uri)
          (.printStackTrace e))))))


(s/fdef get-by-id :args (s/cat :id (s/or :int integer? :str string?) :off-url string?
                               :off-user string? :off-password string?)
        :ret ::product)
(defn get-by-id [id off-url off-user off-password]
  (let [product (get-by-id-from-cache-or-api id off-url off-user off-password)]
    (sanitize-product (get product :product {}))))


(s/fdef add-product :args (s/cat :map-or-list (s/or :map (s/keys :req-un [::db-food/product-id])
                                                    :list (s/coll-of (s/keys :req-un [::db-food/product-id])))
                                 :off-url string? :off-user string? :off-password string?))
(defn add-product
  "adds the off product to the given map or maps. The map must contain the key :product-id"
  [map-or-list off-url off-user off-password]
  (let [f #(assoc % :product (get-by-id (:product-id %) off-url off-user off-password))]
    (if (map? map-or-list)
      (f map-or-list)
      (mapv f map-or-list))))





;{:salt "1.905",
; :sugars_unit "g",
; :cholesterol_label "Cholestérol",
; :energy_unit "kJ",
; :cholesterol_serving "0",
; :energy_value "692",
; :fat_serving "6.5",
; :nutrition-score-uk "6",
; :proteins_value "16.5",
; :monounsaturated-fat_label "Acides gras monoinsaturés",
; :proteins_unit "g",
; :carbohydrates_unit "g",
; :sodium_value "0.75",
; :cholesterol_100g "0",
; :monounsaturated-fat_100g "4.3",
; :salt_serving "1.91",
; :saturated-fat_value "0.7",
; :fat "6.5",
; :fiber "7.6",
; :energy "692",
; :salt_value "1.905",
; :fiber_unit "g",
; :cholesterol_value "0",
; :fiber_serving "7.6",
; :polyunsaturated-fat_100g "1.5",
; :proteins_serving "16.5",
; :saturated-fat_unit "g",
; :fat_100g "6.5",
; :sugars_serving "5.2",
; :monounsaturated-fat_value "4.3",
; :saturated-fat_serving "0.7",
; :sugars_value "5.2",
; :sodium_100g "0.75",
; :carbohydrates_value "6.5",
; :sodium_serving "0.75",
; :cholesterol "0",
; :proteins_100g "16.5",
; :fat_unit "g",
; :saturated-fat "0.7",
; :sugars_100g "5.2",
; :monounsaturated-fat "4.3",
; :cholesterol_unit "g",
; :fiber_value "7.6",
; :sodium "0.75",
; :polyunsaturated-fat_unit "g",
; :polyunsaturated-fat_label "Acides gras polyinsaturés",
; :fiber_100g "7.6",
; :monounsaturated-fat_unit "g",
; :nutrition-score-fr "6",
; :sugars "5.2",
; :carbohydrates "6.5",
; :polyunsaturated-fat "1.5",
; :monounsaturated-fat_serving "4.3",
; :energy_100g "692",
; :polyunsaturated-fat_value "1.5",
; :energy_serving "692",
; :salt_100g "1.905",
; :saturated-fat_100g "0.7",
; :nutrition-score-uk_100g "6",
; :sodium_unit "g",
; :salt_unit "g",
; :carbohydrates_serving "6.5",
; :carbohydrates_100g "6.5",
; :proteins "16.5",
; :fat_value "6.5",
; :nutrition-score-fr_100g "6",
; :polyunsaturated-fat_serving "1.5"}



;(clojure.pprint/pprint {:clojure.spec.alpha/problems ({:path [:product :nutriments :clojure.spec.alpha/nil], :pred nil?, :val {:salt "3.5", :sugars_unit "", :energy-kcal "201.81514210652017", :energy_unit "kcal", :energy_value 202, :proteins_value "17.6", :proteins_unit "", :carbohydrates_unit "", :saturated-fat_value "8.2", :fat 12, :energy 845, :salt_value "3.5", :saturated-fat_unit "", :fat_100g 12, :sugars_value 6, :sodium_100g "1.37795275590551", :carbohydrates_value 6, :proteins_100g "17.6", :fat_unit "", :saturated-fat "8.2", :sugars_100g 6, :sodium "1.37795275590551", :sugars 6, :carbohydrates 6, :energy_100g 845, :salt_100g "3.5", :saturated-fat_100g "8.2", :salt_unit "", :carbohydrates_100g 6, :proteins "17.6", :fat_value 12}, :via [:de.sveri.getless.service.off/nutriments], :in [0 :nutriments]} {:path [:product :nutriments :clojure.spec.alpha/pred :sugars_unit], :pred #{"kJ" "kcal" "kCal" "g" "mg"}, :val "", :via [:de.sveri.getless.service.off/nutriments :de.sveri.getless.service.off/unit], :in [0 :nutriments :sugars_unit]} {:path [:product :nutriments :clojure.spec.alpha/pred :fat_unit], :pred #{"kJ" "kcal" "kCal" "g" "mg"}, :val "", :via [:de.sveri.getless.service.off/nutriments :de.sveri.getless.service.off/unit], :in [0 :nutriments :fat_unit]} {:path [:product :nutriments :clojure.spec.alpha/pred :salt_unit], :pred #{"kJ" "kcal" "kCal" "g" "mg"}, :val "", :via [:de.sveri.getless.service.off/nutriments :de.sveri.getless.service.off/unit], :in [0 :nutriments :salt_unit]}), :clojure.spec.alpha/spec #object[clojure.spec.alpha$regex_spec_impl$reify__2509 0x302765e4 "clojure.spec.alpha$regex_spec_impl$reify__2509@302765e4"], :clojure.spec.alpha/value ({:image_thumb_url "https://static.openfoodfacts.org/images/products/20987664/front_de.3.100.jpg", :_keywords ["schmelzkase" "light" "toast"], :image_small_url "https://static.openfoodfacts.org/images/products/20987664/front_de.3.200.jpg", :nutriments {:salt "3.5", :sugars_unit "", :energy-kcal "201.81514210652017", :energy_unit "kcal", :energy_value 202, :proteins_value "17.6", :proteins_unit "", :carbohydrates_unit "", :saturated-fat_value "8.2", :fat 12, :energy 845, :salt_value "3.5", :saturated-fat_unit "", :fat_100g 12, :sugars_value 6, :sodium_100g "1.37795275590551", :carbohydrates_value 6, :proteins_100g "17.6", :fat_unit "", :saturated-fat "8.2", :sugars_100g 6, :sodium "1.37795275590551", :sugars 6, :carbohydrates 6, :energy_100g 845, :salt_100g "3.5", :saturated-fat_100g "8.2", :salt_unit "", :carbohydrates_100g 6, :proteins "17.6", :fat_value 12}, :ingredients_text "", :rev 10, :lang "de", :ingredients [], :id "20987664", :code "20987664", :product_name_de "Toast Light Schmelzkäse", :nutrition_data_per "100g", :quantity "250 g", :product_name "Toast Light Schmelzkäse"} #object[clojure.core$partial$fn__5841 0x73f4c45a "clojure.core$partial$fn__5841@73f4c45a"] ["energy" "fat" "saturated-fat" "carbohydrates" "sugars" "proteins" "salt" "fiber" "cholesterol" "monounsaturated-fat" "polyunsaturated-fat"]), :clojure.spec.alpha/fn de.sveri.getless.service.off/add-nutriments, :clojure.spec.alpha/args ({:image_thumb_url "https://static.openfoodfacts.org/images/products/20987664/front_de.3.100.jpg", :_keywords ["schmelzkase" "light" "toast"], :image_small_url "https://static.openfoodfacts.org/images/products/20987664/front_de.3.200.jpg", :nutriments {:salt "3.5", :sugars_unit "", :energy-kcal "201.81514210652017", :energy_unit "kcal", :energy_value 202, :proteins_value "17.6", :proteins_unit "", :carbohydrates_unit "", :saturated-fat_value "8.2", :fat 12, :energy 845, :salt_value "3.5", :saturated-fat_unit "", :fat_100g 12, :sugars_value 6, :sodium_100g "1.37795275590551", :carbohydrates_value 6, :proteins_100g "17.6", :fat_unit "", :saturated-fat "8.2", :sugars_100g 6, :sodium "1.37795275590551", :sugars 6, :carbohydrates 6, :energy_100g 845, :salt_100g "3.5", :saturated-fat_100g "8.2", :salt_unit "", :carbohydrates_100g 6, :proteins "17.6", :fat_value 12}, :ingredients_text "", :rev 10, :lang "de", :ingredients [], :id "20987664", :code "20987664", :product_name_de "Toast Light Schmelzkäse", :nutrition_data_per "100g", :quantity "250 g", :product_name "Toast Light Schmelzkäse"} #object[clojure.core$partial$fn__5841 0x73f4c45a "clojure.core$partial$fn__5841@73f4c45a"] ["energy" "fat" "saturated-fat" "carbohydrates" "sugars" "proteins" "salt" "fiber" "cholesterol" "monounsaturated-fat" "polyunsaturated-fat"]), :clojure.spec.alpha/failure :instrument, :clojure.spec.test.alpha/caller {:file "off.clj", :line 15, :var-scope de.sveri.getless.routes.off/search-page, :local-fn fn}})