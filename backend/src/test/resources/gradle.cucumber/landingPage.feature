Feature: UD1 Landing Page

  Scenario: AC3 - The landing page shows the number of currently registered users, the number of currently available sale listings,
            and the number of sales that have been made.
    Given I not logged in
    When I navigate to the landing page
    Then I am able to see the specified statistics about resale

  Scenario: AC5 - When contacting Resale, I am required to provide my email address and a message (which may be multi-line).
    Given I not logged in
    When I try to contact resale
    Then I must enter an email and a message