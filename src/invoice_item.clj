(ns invoice-item
  (:require [clojure.test :refer :all]))

(defn- discount-factor
  [{:invoice-item/keys [discount-rate]
    :or {discount-rate 0}}]
  "Calculate discount factor based on the given discount rate."
  (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as item
    :or {discount-rate 0}}]
  "Calculate the subtotal for an item considering quantity, price, and discount rate."
  (* precise-price precise-quantity (discount-factor item)))

; Function to define an item with quantity, price, and discount rate
(defn create-item
  [precise-quantity precise-price discount-rate]
  "Create an item with specified quantity, price, and discount rate."
  {:invoice-item/precise-quantity precise-quantity
   :invoice-item/precise-price    precise-price
   :invoice-item/discount-rate    discount-rate})

; Function to calculate the expected result
(defn expected-result
  [precise-quantity precise-price discount-rate]
  "Calculate the expected result for an item based on quantity, price, and discount rate."
  (double (* precise-quantity precise-price (- 1 (/ discount-rate 100.0)))))

; Test cases

; Test case: Subtotal with zero quantity
(deftest subtotal-with-zero-quantity
  (let [item (create-item 0.0 2.99 10)
        expected-result 0.0]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with zero price
(deftest subtotal-with-zero-price
  (let [item (create-item 10 0.0 10)
        expected-result 0.0]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with no discount
(deftest subtotal-no-discount
  (let [item {:invoice-item/precise-quantity 10
              :invoice-item/precise-price    2.99}
        expected-result (expected-result 10 2.99 0)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with 10% discount
(deftest subtotal-with-discount-10-percent
  (let [item {:invoice-item/precise-quantity 10
              :invoice-item/precise-price    2.99
              :invoice-item/discount-rate    10}
        expected-result (expected-result 10 2.99 10)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with zero quantity item
(deftest subtotal-with-zero-quantity-item
  (let [item {:invoice-item/precise-quantity 0
              :invoice-item/precise-price    4.99
              :invoice-item/discount-rate    10}
        expected-result 0.0] ; Subtotal should be 0 for zero quantity
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with zero price item
(deftest subtotal-with-zero-price-item
  (let [item {:invoice-item/precise-quantity 15
              :invoice-item/precise-price    0.0
              :invoice-item/discount-rate    20}
        expected-result 0.0] ; Subtotal should be 0 for zero price
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with different items and discount rates
(deftest subtotal-with-different-items-and-discount-rates
  (let [item1 {:invoice-item/precise-quantity 7
               :invoice-item/precise-price    1.85
               :invoice-item/discount-rate    5}
        item2 {:invoice-item/precise-quantity 14
               :invoice-item/precise-price    2.49
               :invoice-item/discount-rate    15}
        item3 {:invoice-item/precise-quantity 21
               :invoice-item/precise-price    3.15
               :invoice-item/discount-rate    25}
        item4 {:invoice-item/precise-quantity 28
               :invoice-item/precise-price    3.79
               :invoice-item/discount-rate    35}
        expected-result1 (expected-result 7 1.85 5)
        expected-result2 (expected-result 14 2.49 15)
        expected-result3 (expected-result 21 3.15 25)
        expected-result4 (expected-result 28 3.79 35)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))
    (is (= expected-result4 (subtotal item4)))))

; Test case: Subtotal with varied quantities and same discount rate
(deftest subtotal-with-varied-quantities-and-same-discount-rates
  (let [item1 (create-item 5 2.99 10)
        item2 (create-item 15 2.99 10)
        item3 (create-item 20 2.99 10)
        expected-result1 (expected-result 5 2.99 10)
        expected-result2 (expected-result 15 2.99 10)
        expected-result3 (expected-result 20 2.99 10)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with varied prices and same quantity
(deftest subtotal-with-varied-prices-and-same-quantity
  (let [item1 (create-item 10 1.99 10)
        item2 (create-item 10 3.99 10)
        item3 (create-item 10 4.99 10)
        expected-result1 (expected-result 10 1.99 10)
        expected-result2 (expected-result 10 3.99 10)
        expected-result3 (expected-result 10 4.99 10)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with equal items and different discount rates
(deftest subtotal-with-equal-items-and-different-discount-rates
  (let [item1 (create-item 15 1.50 5)
        item2 (create-item 15 1.50 15)
        item3 (create-item 15 1.50 25)
        expected-result1 (expected-result 15 1.50 5)
        expected-result2 (expected-result 15 1.50 15)
        expected-result3 (expected-result 15 1.50 25)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with varied discount rates and same quantity and price
(deftest subtotal-with-varied-discount-rates
  (let [item1 (create-item 10 2.99 5)
        item2 (create-item 10 2.99 15)
        item3 (create-item 10 2.99 20)
        expected-result1 (expected-result 10 2.99 5)
        expected-result2 (expected-result 10 2.99 15)
        expected-result3 (expected-result 10 2.99 20)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with negative quantity
(deftest subtotal-with-negative-quantity
  (let [item (create-item -5 2.99 10)
        expected-result (expected-result -5 2.99 10)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with negative price
(deftest subtotal-with-negative-price
  (let [item (create-item 10 -2.99 10)
        expected-result (expected-result 10 -2.99 10)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with negative discount rate
(deftest subtotal-with-negative-discount-rate
  (let [item (create-item 10 2.99 -10)
        expected-result (expected-result 10 2.99 -10)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with large quantity, price, and discount rate
(deftest subtotal-with-large-values
  (let [item (create-item 10000 999.99 50)
        expected-result (expected-result 10000 999.99 50)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with decimal values for quantity, price, and discount rate
(deftest subtotal-with-decimal-values
  (let [item (create-item 7.5 1.25 7.5)
        expected-result (expected-result 7.5 1.25 7.5)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with zero discount rate
(deftest subtotal-with-zero-discount-rate
  (let [item (create-item 20 4.99 0)
        expected-result (expected-result 20 4.99 0)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with 100% discount rate
(deftest subtotal-with-100-percent-discount
  (let [item (create-item 10 9.99 100)
        expected-result 0.0] ; Subtotal should be 0 for 100% discount
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with varied large values
(deftest subtotal-with-varied-large-values
  (let [item1 (create-item 1000 9999.99 25)
        item2 (create-item 500 4999.99 15)
        item3 (create-item 2000 19999.99 5)
        expected-result1 (expected-result 1000 9999.99 25)
        expected-result2 (expected-result 500 4999.99 15)
        expected-result3 (expected-result 2000 19999.99 5)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with decimal discount rate
(deftest subtotal-with-decimal-discount-rate
  (let [item (create-item 10 2.99 5.5)
        expected-result (expected-result 10 2.99 5.5)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with zero quantity, zero price, and zero discount rate
(deftest subtotal-with-zero-quantity-price-discount-rate
  (let [item (create-item 0 0.0 0)
        expected-result 0.0]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with varied decimal quantities, prices, and discount rates
(deftest subtotal-with-varied-decimal-values
  (let [item1 (create-item 7.5 1.25 7.5)
        item2 (create-item 3.33 4.44 1.11)
        item3 (create-item 9.99 0.99 8.88)
        expected-result1 (expected-result 7.5 1.25 7.5)
        expected-result2 (expected-result 3.33 4.44 1.11)
        expected-result3 (expected-result 9.99 0.99 8.88)]
    (is (= expected-result1 (subtotal item1)))
    (is (= expected-result2 (subtotal item2)))
    (is (= expected-result3 (subtotal item3)))))

; Test case: Subtotal with zero quantity, varied price, and varied discount rate
(deftest subtotal-with-zero-quantity-varied-price-and-discount
  (let [item (create-item 0 5.99 15)
        expected-result 0.0] ; Subtotal should be 0 for zero quantity
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with varied quantity, zero price, and varied discount rate
(deftest subtotal-with-varied-quantity-zero-price-and-discount
  (let [item (create-item 10 0.0 20)
        expected-result 0.0] ; Subtotal should be 0 for zero price
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with random values
(deftest subtotal-with-random-values
  (let [item (create-item 7.5 3.33 9.99)
        expected-result (expected-result 7.5 3.33 9.99)]
    (is (= expected-result (subtotal item)))))

; Test case: Subtotal with maximum quantity, price, and discount rate
(deftest subtotal-with-maximum-values
  (let [item (create-item Double/MAX_VALUE Double/MAX_VALUE Double/MAX_VALUE)
        expected-result (expected-result Double/MAX_VALUE Double/MAX_VALUE Double/MAX_VALUE)]
    (is (= expected-result (subtotal item)))))