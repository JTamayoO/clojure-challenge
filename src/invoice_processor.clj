(ns invoice-processor)

(def invoice (clojure.edn/read-string
               (slurp "../invoice.edn")))

; Filters items with a specific tax rate
(defn filter-items-by-tax-rate [invoice tax-rate]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= tax-rate (:tax/rate %))) (get-in % [:taxable/taxes])))))

; Filters items with a specific retention rate
(defn filter-items-by-retention-rate [invoice retention-rate]
  (->> invoice
       :invoice/items
       (filter #(some (fn [%] (= retention-rate (:retention/rate %))) (get-in % [:retentionable/retentions])))))

; Filters items with either IVA tax rate 19 or retention rate 1, but not both
(defn filter-items-by-tax-or-retention [invoice]
  (->> invoice
       :invoice/items
       (filter (fn [item]
                 (let [has-iva (some #(and (= 19 (:tax/rate %)) (= :iva (:tax/category %))) (get-in item [:taxable/taxes])),
                       has-retention (some #(and (= 1 (:retention/rate %)) (= :ret_fuente (:retention/category %))) (get-in item [:retentionable/retentions]))]
                   (and (or has-iva has-retention) (not (and has-iva has-retention))))))))

; Print the results
(println "Items with a tax rate of 19: " (filter-items-by-tax-rate invoice 19))
(println "Items with a retention rate of 1: " (filter-items-by-retention-rate invoice 1))
(println "Items with IVA 19 or retention rate 1, but not both: " (filter-items-by-tax-or-retention invoice))
