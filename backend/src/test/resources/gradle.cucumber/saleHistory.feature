Feature: UD5: Sale History

  Scenario: AC1: As a logged-in individual user, I can access my sale history
    Given I am an individual user who has purchased a listing
    When I make a request to view my sale history
    Then The sales I have purchased are given to me