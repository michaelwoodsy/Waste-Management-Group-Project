Feature: UCM6 - Keyword management

  Scenario: AC1/AC2: When creating a card, a user can add one or more keywords.
    The keywords may be selected from a system-wide list.
    Given The keyword "Apples" exists
    When A user creates a card and adds the keyword "Apples"
    Then The card is created with the keyword "Apples"

  Scenario: AC1/AC2: When creating a card, a user can add one or more keywords.
    The keywords may be selected from a system-wide list.
    Given The keyword "Apples" and the keyword "Free" exists
    When A user creates a card and adds the keywords "Apples" and "Free"
    Then The card is created with the keywords "Apples" and "Free"

  Scenario: AC3: The user can also enter keywords via the keyboard.
    In this case, an autocomplete feature indicates potential matches.
    Given The keyword "Apples" exists
    When The user enters "app" as a partial keyword
    Then The keyword "Apples" is included as a potential match


  Scenario: AC4: If no match is found then the user can add the keyword to the list as a new entry.
    Given There is no keyword "Bananas"
    When The user enters "Bananas" as a partial keyword
    And There are no matches
    Then It is possible to add "Bananas" as a new keyword


#  Scenario: AC5: To prevent abuse, system administrators are notified when a new keyword is added.
#    Given There is no keyword "Cabbages"
#    When The new keyword "Cabbages" is created
#    Then A system admin notification is created for the new keyword "Cabbages"
#
#
  Scenario: AC6: Only system administrators can delete a keyword.
    In this case, the keyword is removed from the list and from any cards it appears on.
    Given A card with the keyword "Random" exists
    When A system admin deletes the keyword "Random"
    Then The keyword "Random" no longer exists
    And The card no longer has the keyword "Random"

  Scenario: AC6: Only system administrators can delete a keyword.
  In this case, the keyword is removed from the list and from any cards it appears on.
    Given A card with the keyword "Random" exists
    When A regular user tries to delete the keyword "Random"
    Then The keyword "Random" still exists
    And The card still has the keyword "Random"