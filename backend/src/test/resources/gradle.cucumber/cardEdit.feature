Feature: UCM9 - Edit Card

  Scenario: AC1: I can edit any card that I created but I cannot edit a card created by another user.
    Given The user has created a card
    When That user tries to edit their card
    Then The card is successfully edited

  Scenario: AC1: I can delete any card that I created but I cannot delete a card created by another user.
    Given The user has created a card
    When A different user tries to edit their card
    Then A Forbidden exception is thrown
    And The card is not edited

  Scenario: AC2: I can change the title, description and keywords.  The creation date and creator remain unchanged.
    Given The user has created a card
    Given The keywords "Onions" and "Free" exists
    When The user edits the card and sets the title to "Almost expired Onions", the description to "These Onions have almost expired" and sets the keywords to "Onions" and "Free"
    Then The card is successfully edited


  Scenario: AC4: A system administrator can edit any cards.
    Given The system administrator exists
    And The user has created a card
    When The system administrator tries to edit the card
    Then The card is successfully edited