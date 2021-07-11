Feature: UCM7 Keyword Search
  Scenario: AC1: Only cards from the current section will be searched.
    Given I am logged in with email "cardUser@gmail.com" with the following 3 cards exist with keywords:
      | My Card       | Test card description   | Wanted  | cardUser@gmail.com      | testKeyword1 |
      | My Other Card | Other card description  | Wanted | cardUser@gmail.com      | testKeyword |
      | Your Card     | Your card description   | Wanted  | otherCardUser@gmail.com | testKeyword  |
    When I search for cards in the "Wanted" section
    Then I am returned 2 cards
    And All returned cards are with section "Wanted"

#  Scenario: AC2: Search cards using keywords, matching any of the keywords.
#    Given The Following cards exist:
#      | My Card       | Test card description   | Wanted  | cardUser@gmail.com      | testKeyword, apples
#      | My Other Card | Other card description  | Wanted | cardUser@gmail.com      | testKeyword, apples, oranges
#      | Your Card     | Your card description   | Wanted  | otherCardUser@gmail.com | DifferentKeyword, oranges
#    When I search for cards with the keywords "testKeyword" or "apples" or "oranges"
#    Then I find 3 cards
#    And All returned cards have at least one of the keywords "testKeyword" or "apples" or "oranges"
#
#  Scenario: AC2: Search cards using keywords, matching all of the keywords.
#    Given The Following cards exist:
#      | My Card       | Test card description   | Wanted  | cardUser@gmail.com      | testKeyword, apples
#      | My Other Card | Other card description  | Wanted | cardUser@gmail.com      | testKeyword, apples, oranges
#      | Your Card     | Your card description   | Wanted  | otherCardUser@gmail.com | DifferentKeyword, oranges
#    When I search for cards with the keywords "testKeyword", "apples", and "oranges"
#    Then I find 1 cards
#    And All returned cards have at all of the keywords "testKeyword", "apples", and "oranges"