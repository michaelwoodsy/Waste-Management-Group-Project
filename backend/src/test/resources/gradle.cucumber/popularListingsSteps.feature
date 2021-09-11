Feature: UD2 - Popular Listings

  Scenario: AC1: up to 10 of the most liked sale listings are shown
    Given Sale listings exist
    And Other users have liked the sales listings
    When I retrieve the popular sale listings with no country selected
    Then The most popular sale listings are retrieved, in order of most liked to least liked

  Scenario: AC1: up to 10 of the most liked sale listings are shown when filtering by a country
    Given Sale listings exist
    And Other users have liked the sales listings
    When I retrieve the popular sale listings with the country "New Zealand" selected
    Then the most popular sale listings for "New Zealand" are retrieved

  Scenario: AC1: Sale listings that have not been sold and have reached their closing date are removed
    Given A sale listing exists
    When The sale listing has a closing date before the current date
    Then The sale listing is removed