Feature: U30 - Individual Full Sale Listing

  Scenario: AC3: I can “like'' or “bookmark” — teams can choose the exact terminology — this sale listing.
    Given I am logged in and there is a sale listing
    When I try to like the sale listing
    Then The sale listing is added to my list of liked sale listings

  Scenario: AC5: Any user (including myself) can “like” the listing at most once.
    Given I am logged in and I have already liked a sale listing
    When I try to like the sale listing again
    Then An error is thrown

  Scenario: AC6: I can “unlike” this listing. This will decrement the number of “likes”
  the listing has (AC5) and add an appropriate message to my home feed.
