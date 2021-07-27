Feature: U29 - Browse/Search Sales listings

  Scenario: AC2: If no filtering is applied then all current sales listings are displayed
    Given I am logged in
    When I search sales listings with no filters
    Then All sales listings are returned

  Scenario: AC4: I can order sales listings by Price ascending
    Given I am logged in
    When I search sales listings, sorting by price ascending
    Then All sales listings are returned in price ascending order

  Scenario: AC4: I can order sales listings by Price descending
    Given I am logged in
    When I search sales listings, sorting by price descending
    Then All sales listings are returned in price descending order

  Scenario: AC4: I can order sales listings by product name
    Given I am logged in
    When I search sales listings, sorting by product name
    Then All sales listings are returned, ordered by product name

  Scenario: AC5: I can limit search results by business type
    Given I am logged in
    When I search sales listings, filtering by business type "Retail Trade"
    Then Sale listings are returned that correspond to a business with type Retail Trade

  Scenario: AC6: I can limit search results by part of a product name
    Given I am logged in
    When I search sales listings, filtering by part of product name "firs"
    Then One sales listing is returned that has "firs" in its product name

  Scenario: AC7 I can limit search results by setting a price range
    Given I am logged in
    When I search sales listings, filtering by price in between 10.00 and 20.00
    Then Three sale listings are returned that have a price in between 10.00 and 20.00 inclusive

  Scenario: AC8: I can limit search results by part of the name of the seller (business name)
    Given I am logged in
    When I search sales listings, filtering by part of seller name "sec"
    Then Two sales listing is returned that belong to the business named "Second Business"

  Scenario: AC9: I can limit search results by part of the sellers country
    Given I am logged in
    When I search sales listings, filtering by part of seller location "net"
    Then Two sales listing is returned that belong to the business named "First Business"

  Scenario: AC10: I can limit search results by setting a closing date range range
    Given I am logged in
    When I search sales listings, filtering by closing date in between "2021-9-5" and "2021-11-30"
    Then Two sale listings are returned that have a closing date in between "2021-9-5" and "2021-11-30"