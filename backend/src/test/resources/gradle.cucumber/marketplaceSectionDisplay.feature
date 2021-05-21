Feature: UCM3 Marketplace section display
  Scenario: AC2 - The information of a card is displayed
    Given There exists a card with title "Test Card" and a description "Test card description" and a section "Wanted"
    When A user gets that card
    Then The card information is provided
