Feature: UCM5 Find My Cards
  Scenario: AC1 - I can find all my own active cards.
    Given I am logged in with email "cardUser@gmail.com" and the following cards exist:
      | My Card       | Test card description   | Wanted  | cardUser@gmail.com      |
      | My Other Card | Other card description  | ForSale | cardUser@gmail.com      |
      | Your Card     | Your card description   | Wanted  | otherCardUser@gmail.com |
    When I search for cards created by user with email "cardUser@gmail.com"
    Then I find 2 cards
    And All returned cards are by user with email "cardUser@gmail.com"

  Scenario: AC2 - I can find all the active cards created by a user.
    Given I am logged in with email "cardUser@gmail.com" and the following cards exist:
      | My Card       | Test card description   | Wanted  | cardUser@gmail.com      |
      | My Other Card | Other card description  | ForSale | cardUser@gmail.com      |
      | Your Card     | Your card description   | Wanted  | otherCardUser@gmail.com |
    When I search for cards created by user with email "otherCardUser@gmail.com"
    Then I find 1 cards
    And All returned cards are by user with email "otherCardUser@gmail.com"