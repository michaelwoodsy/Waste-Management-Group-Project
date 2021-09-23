Feature: UD6 Sale Reviews

  Scenario: AC1: When acting as an individual user,
  I can select a sale listing from my purchase history to leave a review for the business.
    Given I have purchased a sale listing from a given business.
    When I leave a 5 star review with the comment "Great product and excellent service."
    Then A review is successfully left on the sale.

  Scenario: AC2: The review includes a star rating (out of 5) and a text message.
    Given I have purchased a sale listing from a given business.
    When I leave a review with comment "Happy with service provided and product is good" but no star rating.
    Then An error message is returned to say that no star rating has been provided for the review.

  Scenario: AC3: Individual users can view reviews they have left on their purchase history page, as well as any replies to their reviews.
    Given I am logged in as a user.
    When I view my purchase history.
    Then I can view the reviews left on my purchase history.

  Scenario: AC4: Users can view a list of reviews left for the business on the business’ profile page.
    Given I am logged in as a user.
    When I view a business' profile page.
    Then I can view the reviews left on their business.

  Scenario: AC5: A business’ average star rating is displayed on their profile page.
    Given I am logged in as a user.
    When I view a business' profile page.
    Then I can see the average star rating based on the average score of the business' reviews.