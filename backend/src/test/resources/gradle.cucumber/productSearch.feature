Feature: U28 - Product Search

  Scenario: AC1: The product catalogue has a search feature that allows me to filter the catalogue to identify matching products.
    Given A product catalogue has products with the names "Wattie's Baked Beans" and "Heinz Baked Beans"
    When The business admin searches with the term "Wattie's" to match a product name
    Then Only the product with the name "Wattie's Baked Beans" is included in the results

  Scenario: AC2: I can only search the catalogue for the business I am currently acting as admin for.
    Given A product catalogue has products with the names "Wattie's Baked Beans" and "Heinz Baked Beans"
    When A user who is not the business admin tries searching the catalogue
    Then The user is prevented from doing so

  Scenario: AC3: I can enter one or more search terms.  If I enter more than one term then
    I can specify whether results must match all of the terms or any of them.
    Given A product catalogue has products with the names "Wattie's Baked Beans" and "Heinz Baked Beans"
    When The business admin searches with the term "Wattie's AND Beans" to match a product name
    Then Only the product with the name "Wattie's Baked Beans" is included in the results

  Scenario: AC3: I can enter one or more search terms.  If I enter more than one term then
  I can specify whether results must match all of the terms or any of them.
    Given A product catalogue has products with the names "Wattie's Baked Beans" and "Heinz Baked Beans"
    When The business admin searches with the term "Wattie's OR Beans" to match a product name
    Then Both products with the names "Wattie's Baked Beans" and "Heinz Baked Beans" are included in the results

  Scenario: AC3: Terms containing spaces can be quoted (e.g. “Baked Beans”) if necessary to avoid confusion.
    Given A product catalogue has products with the names "Wattie's Baked Beans" and "Kidney Beans"
    When The business admin searches with the term ""Baked Beans"" to match a product name
    Then Only the product with the name "Wattie's Baked Beans" is included in the results

  Scenario: AC4: I can specify which fields are to be searched.
    Given A product catalogue has a product with the name "Wattie's Baked Beans" and a different product with the id "MUNG_BEANS"
    When The business admin searches with the term "beans" to match a product id
    Then Only the product with the id "MUNG_BEANS" is included in the results