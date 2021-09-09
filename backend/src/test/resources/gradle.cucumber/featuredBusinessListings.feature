Feature: UD3 - Featured Business Listings
  Scenario: AC2 - The user can make a sale listing be featured
    Given the user is acting as a business
    When the user tries to feature a listing
    Then the listing is now featured

  Scenario: AC3 - Only 5 sale listings can be featured at a time
    Given the user is acting as a business and 5 listings are featured
    When the user tires to feature a listing
    Then an error occurs and the listing is not featured

  Scenario: AC6 - The user can remove a sale listing from the featured list
    Given the user is acting as a business and 1 listings are featured
    When the user tries to remove a featured listing
    Then the listing is no longer featured