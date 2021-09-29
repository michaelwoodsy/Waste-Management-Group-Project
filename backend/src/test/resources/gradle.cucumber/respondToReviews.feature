Feature: UD7 Responding to sale reviews

  Scenario: AC2: Businesses can also leave a reply to a review, which consists of a text message.
    Given I am an administrator for a business account
    When I respond to a review with the message "Thank you very much for the feedback".
    Then A response is successfully left on the review.