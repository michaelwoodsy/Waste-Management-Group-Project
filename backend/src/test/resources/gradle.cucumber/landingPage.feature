Feature: UD1 Landing Page

  Scenario: AC3 - The landing page shows the number of currently registered users, the number of currently available sale listings,
  and the number of sales that have been made.
    Given I am not logged in
    When I navigate to the landing page
    Then I am able to see the specified statistics about resale

  Scenario: AC4 - From the landing page, I am able to access a means of contacting Resale with any questions I may have.
    Given I am not logged in
    When I try to contact resale with email "myrtle.t@gmail.com" and message "Hello!"
    Then The request is successful

  Scenario: AC5 - When contacting Resale, I am required to provide my email address and a message.
    Given I am not logged in
    When I try to contact resale without an email or message
    Then The request is not successful
