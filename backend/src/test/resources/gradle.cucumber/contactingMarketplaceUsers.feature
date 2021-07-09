Feature: UCM8 - Contacting other marketplace users

  Scenario: AC1: When I am looking at a displayed card, there is a button, or other suitable control,
    that enables me to contact the card creator.
    Given A card has been created by a user with the email "testUser@gmail.com"
    When Another user messages about the card
    Then The message is sent to the user with the email "testUser@gmail.com"

  Scenario: AC2:  I can enter a (plain text) message.
    Given A card has been created by a user with the email "testUser@gmail.com"
    When Another user messages about the card with the message "Is this still available?"
    Then The card creator receives a message with the text "Is this still available?"

  Scenario: AC3:  The sender’s name is included (e.g. “New message from: Dave Davidson”)
    together with the relevant card title (e.g. “About ‘1982 Lada Samara’”)
    Given A card has been created by a user with the email "testUser@gmail.com"
    When Another user messages about the card
    Then The sender's name and card title are included in the message

  Scenario: AC5: The recipient can reply to the message. The same process occurs,
    leading to the response appearing on the original sender’s feed.
    Given A card has been created by a user with the email "testUser@gmail.com"
    And A user with the email "potentialCustomer@gmail.com" messages about the card
    When The card creator replies to the message
    Then The user with the email "potentialCustomer@gmail.com" receives the reply
#
#  Scenario: AC6: A user can delete a message from their feed,
#    whether or not they have chosen to reply to it.
#    Given A card has been created by a user with the email "testUser@gmail.com"
#    And A user with the email "potentialCustomer@gmail.com" messages about the card
#    When The card creator deletes the message
#    Then The message is successfully removed
