Feature: UD2 - Popular Listings

  Scenario: AC1: up to 10 of the most liked sale listings are shown
    Given More than 10 sale listings exist
    And Other users have liked the sales listings
    Then Retrieving the popular sale listings with no country selected retrieves the 10 most popular sale listings

  Scenario: AC1: up to 10 of the most liked sale listings are shown when filtering by a country
    Given More than 10 sale listings exist
    And Other users have liked the sales listings
    Then Retrieving the popular sale listings with the country "New Zealand" selected retrieves the countries most popular sale listings