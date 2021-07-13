Feature: UCM4 - Card expiry

  Scenario: AC1: I can delete any card that I created but I cannot delete a card created by another user.
    Given A user has created a card
    When That user tries to delete their card
    Then The card is successfully deleted

  Scenario: AC1: I can delete any card that I created but I cannot delete a card created by another user.
    Given A user has created a card
    When A different user tries to delete their card
    Then An appropriate exception is thrown
    And The card is not deleted

  Scenario: AC2: A system administrator can delete any cards.
    Given A system administrator exists
    And A user has created a card
    When The system administrator tries to delete the card
    Then The card is successfully deleted

  Scenario: AC4: If I take no action within 24 hours of the notification (card expiry) then the card will be deleted
  automatically and I will be notified this has happened
    Given A user has created a card
    When The card has an expiry date of more than 24 hours ago
    Then The card is automatically deleted and a notification is created