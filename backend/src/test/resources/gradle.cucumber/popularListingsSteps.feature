Feature: UD2 - Popular Listings

  Scenario: AC1: up to 10 of the most liked sale listings are shown
    Given More than 10 sale listings exist
    And Other users have liked the sales listings
    When I retrieve the popular sale listings with no country selected
    Then the 10 most popular sale listings are retrieved

  Scenario: AC1: up to 10 of the most liked sale listings are shown when filtering by a country
    Given More than 10 sale listings exist
    And Other users have liked the sales listings
    When I retrieve the popular sale listings with the country "New Zealand" selected
    Then the most popular sale listings for New Zealand are retrieved

  Scenario: AC1: Sale listings that have not been sold and have reached their closing date are removed
    Given A sale listing exists
    When The sale listing has a closing date before the current date
    Then The sale listing is removed