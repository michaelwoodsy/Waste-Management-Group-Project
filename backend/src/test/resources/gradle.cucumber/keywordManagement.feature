Feature: UCM6 - Keyword management

  Scenario: AC1/AC2: When creating a card, a user can add one or more keywords.
    The keywords may be selected from a system-wide list.
    Given The keyword "apples" exists
    When A user creates a card and adds the keyword "apples"
    Then The card is created with the keyword "apples"

  Scenario: AC1/AC2: When creating a card, a user can add one or more keywords.
    The keywords may be selected from a system-wide list.
    Given The keyword "apples" and the keyword "free" exists
    When A user creates a card and adds the keywords "apples" and "free"
    Then The card is created with the keywords "apples" and "free"

  Scenario: AC3: The user can also enter keywords via the keyboard.
    In this case, an autocomplete feature indicates potential matches.
    Given The keyword "apples" exists
    When The user enters "app" as a partial keyword
    Then The keyword "apples" is included as a potential match


  Scenario: AC4: If no match is found then the user can add the keyword to the list as a new entry.
    Given There is no keyword "bananas"
    When The user enters "bananas" as a partial keyword
    And There are no matches
    Then It is possible to add "bananas" as a new keyword


  Scenario: AC5: To prevent abuse, system administrators are notified when a new keyword is added.
    Given There is no keyword "cabbages"
    When The new keyword "cabbages" is created
    Then A system admin notification is created for the new keyword "cabbages"


  Scenario: AC6: Only system administrators can delete a keyword.
    In this case, the keyword is removed from the list and from any cards it appears on.
    Given A card with the keyword "random" exists
    When A system admin deletes the keyword "random"
    Then The keyword "random" no longer exists
    And The card no longer has the keyword "random"

  Scenario: AC6: Only system administrators can delete a keyword.
  In this case, the keyword is removed from the list and from any cards it appears on.
    Given A card with the keyword "random" exists
    When A regular user tries to delete the keyword "random"
    Then The keyword "random" still exists
    And The card still has the keyword "random"