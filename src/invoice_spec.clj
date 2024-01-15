(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]
    )
  (:import (java.text SimpleDateFormat)))

(use 'clojure.walk)

; Utility function to check if a string is not blank
(defn not-blank? [value]
  (-> value clojure.string/blank? not))

; Utility function to check if a string is non-empty
(defn non-empty-string? [x]
  (and (string? x) (not-blank? x)))

; Define specifications for customer information
(s/def :customer/name non-empty-string?)
(s/def :customer/email non-empty-string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

; Define specifications for tax information
(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

; Define specifications for invoice items
(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku non-empty-string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

; Define specifications for invoice
(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

; Function to parse a "dd/MM/yyyy" date string
(def date-format (SimpleDateFormat. "dd/MM/yyyy"))
(defn parse-date [date-str]
  (try
    (.parse date-format date-str)
    (catch Exception e
      (println "Error parsing date:" e))))

; Function to map JSON keys to corresponding spec keys
(defn map-json-keys [key value]
  (case key
    :issue_date (parse-date value)
    :payment_date (parse-date value)
    :tax_category (if (= value "IVA") :iva value)
    :tax_rate (double value)
    value))

; Map for replacing keys in JSON with spec-compliant keys
(def key-mapping
  {:issue_date   :invoice/issue-date
   :customer     :invoice/customer
   :items        :invoice/items
   :company_name :customer/name
   :email        :customer/email
   :price        :invoice-item/price
   :quantity     :invoice-item/quantity
   :sku          :invoice-item/sku
   :taxes        :invoice-item/taxes
   :tax_category :tax/category
   :tax_rate     :tax/rate})

; Function to replace JSON keys with spec-compliant keys
(defn replace-keys [invoice-map]
  (postwalk-replace key-mapping invoice-map))

; Function to parse JSON invoice file
(defn parse-json-invoice [file-name]
  (let [json-invoice (json/read-str (slurp file-name) :key-fn keyword :value-fn map-json-keys)]
    (get (replace-keys json-invoice) :invoice)))

; Test the function
(let [parsed-invoice (parse-json-invoice "../invoice.json")]
  (when parsed-invoice
    (println "Parsed Invoice:" parsed-invoice)
    (println "Is valid?:" (s/valid? ::invoice parsed-invoice))))