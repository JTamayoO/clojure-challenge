# Engineering Challenge

## Getting started

This clojure challenge is made up of 3 questions that reflect the learning you accumulated for the past week. Complete
the following instructions:

1. Create a Github/Gitlab repo to show the challenge code. When complete, send us the link to your challenge results.
2. Duration: About 4-6 hours
3. Install Cursive Plugin to Intellij and setup a clojure deps project. https://cursive-ide.com/userguide/deps.html

1. Enjoy!

## Problems

### Problem 1 Thread-last Operator ->>

Given the invoice defined in **invoice.edn** in this repo, use the thread-last ->> operator to find all invoice items
that satisfy the given conditions. Please write a function that receives an invoice as an argument and returns all items
that satisfy the conditions described below.

#### Requirements

- Load invoice to play around with the function like this:

```
(def invoice (clojure.edn/read-string (slurp "invoice.edn")))
```

#### Definitions

- An invoice item is a clojure map { … } which has an :invoice-item/id field. EG.

```
{:invoice-item/id     "ii2"  
  :invoice-item/sku "SKU 2"}
```

- An invoice has two fields :invoice/id (its identifier) and :invoice/items a vector of invoice items

#### Invoice Item Conditions

- At least have one item that has :iva 19%
- At least one item has retention :ret\_fuente 1%
- Every item must satisfy EXACTLY one of the above two conditions. This means that an item cannot have BOTH :iva 19% and
  retention :ret\_fuente 1%.

### Notes about Solution

The solution is found in the file `invoice_processor.clj`, here is a summary of the proposed solution:

The code reads an invoice in EDN (Extensible Data Notation) format from a file, processes it by filtering elements based on specific criteria, and then prints the results.

#### **Filtering Items by Tax Rate**

Defines a function `filter-items-by-tax-rate` that takes an invoice and a tax rate as parameters. It filters the items in the invoice that have the specified tax rate and returns them.


#### **Filtering Items by Retention Rate**

Defines a function `filter-items-by-retention-rate` that takes an invoice and a retention rate as parameters. It filters the items in the invoice that have the specified retention rate and returns them.


#### **Filtering Items by Tax or Retention**

Defines a function `filter-items-by-tax-or-retention` that filters the items in the invoice with an IVA tax rate of 19 or a retention rate of 1, but not both. It returns the filtered items.

The code processes an invoice by filtering its items based on tax rates, retention rates, and a combination of tax and retention criteria. The final results are printed to the console.

## Problem 2: Core Generating Functions

Given the invoice defined in **invoice.json** found in this repo, generate an invoice that passes the spec **::invoice**
defined in **invoice-spec.clj**. Write a function that as an argument receives a file name (a JSON file name in this
case) and returns a clojure map such that

```
(s/valid? ::invoice invoice) => true 
```

where invoice represents an invoice constructed from the JSON.

### Notes about Solution

The solution is found in the file `invoice_spec.clj`, here is a summary of the proposed solution:

The code provides a structured way to validate and process JSON invoices, ensuring adherence to specific data specifications. It includes functions for parsing dates, mapping JSON keys, and validating invoices against predefined specifications.

The code concludes with a test to showcase the functionality of parsing a JSON-formatted invoice. After parsing the invoice file and applying the necessary transformations, it prints the parsed invoice and checks its validity based on the defined specifications.

## Problem 3: Test Driven Development

Given the function **subtotal** defined in **invoice-item.clj** in this repo, write at least five tests using clojure
core **deftest** that demonstrates its correctness. This subtotal function calculates the subtotal of an invoice-item
taking a discount-rate into account. Make sure the tests cover as many edge cases as you can!

### Notes about Solution

The code focuses on calculating subtotals and handling different cases that may arise in the context of invoice items. These functions and tests can be used to ensure the accuracy of calculations in scenarios related to invoices.

The solution is found in the file `invoice_item.clj`, here is a summary of the proposed solution:

#### Discount Factor Calculation:
Calculates a discount factor based on the provided discount rate.

- **Function:** `discount-factor`

#### Subtotal Calculation:
Calculates the subtotal for an item considering the quantity, price, and discount rate.

- **Function:** `subtotal`

#### Item Creation Function:
Creates an item with specified quantity, price, and discount rate.

- **Function:** `create-item`


#### Expected Result Calculation:
Calculates the expected result for an item based on quantity, price, and discount rate.

- **Function:** `expected-result`

#### Test Cases:

A total of 26 test cases have been implemented (using `deftest`) to verify the correctness of the functions in various scenarios. These scenarios include zero quantities, zero prices, different discount rates, negative values, large values, decimal values, among others.
