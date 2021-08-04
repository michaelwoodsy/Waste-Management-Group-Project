Feature: U31 - Purchases

#  Scenario: AC1: When a sales listing is purchased all other users who have liked the sales listing are notified
#    Given A sales listing exists for a business
#    And Other users have liked that sales listing
#    When A user purchases the sale listing
#    Then Notifications are created for the users that liked that sales listing notifying them that it has been purchased
#    And The purchased sale listing is no longer for sale
#
#  Scenario: AC2: A notification is sent to the purchaser of a sales listing detailing payment and collection details
#    Given A sales listing exists for a business
#    When A user purchases the sale listing
#    Then A notification is sent to the purchaser with payment and collection details

  Scenario: AC3: When a sale listing is purchased, the sellers inventory is updated to reflect that the goods have been sold
    Given A sales listing exists for a business
    When A user purchases the sale listing
    Then The businesses inventory item that the sale listing is for is updated to show that the sale listing was purchased

  Scenario: AC4: The sale listing is removed after purchase
    Given A sales listing exists for a business
    When A user purchases the sale listing
    Then The sale listing gets removed from the businesses sales listings

  Scenario: AC5: After a sales listing is purchased, information about the sale is stored in sales history
    Given A sales listing exists for a business
    When A user purchases the sale listing
    Then The sale listing is stored in sales history