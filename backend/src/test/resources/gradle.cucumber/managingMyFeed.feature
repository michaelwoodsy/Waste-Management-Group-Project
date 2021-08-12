Feature: U32 Managing my feed

#  Scenario: AC1: I can delete messages and notifications from my home page.
#    Given I have a message on my home page
#    When I attempt to delete the message from my home page
#    Then The message is successfully removed from my home page
#
#  Scenario: AC1: I can delete messages and notifications from my home page.
#    Given I have a notification on my home page
#    When I attempt to delete the notification from my home page
#    Then The notification is successfully removed from my home page

  Scenario: AC2: I can easily distinguish between messages and notifications I have clicked on (“read”) and those I haven’t (“unread”).
    Given I have an unread message
    When I click on (read) the message
    Then The message is marked as read

#  Scenario: AC2: I can easily distinguish between messages and notifications I have clicked on (“read”) and those I haven’t (“unread”).
#    Given I have an unread notification
#    When I click on (read) the notification
#    Then The notification is marked as read
#
  Scenario: AC2: New messages and notifications are initially unread.
    Given I have a new message
    When I see the message on my home page
    Then The message is marked as unread
#
#  Scenario: AC2: New messages and notifications are initially unread.
#    Given I have a new notification
#    When I see the notification on my home page
#    Then The notification is marked as unread
#
#  Scenario: AC3: I can “star” sale listings to mark them as high importance.
#  Starred items remain at the top of my feed even when new items arrive.
#    Given I have a liked sale listing
#    When I star the liked sale listing
#    Then The liked sale listing is marked as starred
#    And The listing appears at the top of my feed

  Scenario: AC6: I can “tag” a liked sale listing.
    Given I have a liked sale listing
    When I tag the listing with the colour "red"
    Then The liked sale listing is marked with a "red" tag

  Scenario: AC7: I can filter my feed to show only sale listings with a particular tag.
    Given I have a liked sale listing tagged as "red"
    And I have a liked sale listing tagged as "blue"
#    When I filter my feed by "red"
#    Then Only the listing tagged as "red" is shown on my feed
#
#  Scenario: AC7: Clearing the filter restores the feed to its previous state.
#    Given I have a liked sale listing tagged as "red"
#    And I have a liked sale listing tagged as "blue"
#    When I clear the tag filter
#    Then Both the listings tagged as "red" and "blue" are shown on my feed