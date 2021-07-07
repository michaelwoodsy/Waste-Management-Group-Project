Feature: UCM2 - Card Creation

  Scenario: AC1: I can create a card to be displayed in one of the three sections.
    Given A user exists
    When A user creates a card to be displayed in the "ForSale" section
    Then The card is successfully created

  Scenario: AC1: I can create a card to be displayed in one of the three sections.
    Given A user exists
    When A user creates a card to be displayed in the "Wanted" section
    Then The card is successfully created

  Scenario: AC1: I can create a card to be displayed in one of the three sections.
    Given A user exists
    When A user creates a card to be displayed in the "Exchange" section
    Then The card is successfully created

  Scenario: AC2: My name and location are automatically included so that other marketplace users can contact me.
    Given A card exists
    When A user views a card in the card display section
    Then The card creator's name and location are displayed successfully

  Scenario: AC3: The card has a title (e.g. “1982 Lada Samara”) which is intended to be suitable for one-line
  displays such as the tabular display in the corresponding marketplace section.
    Given A card exists
    When A user views a card in the card display section
    Then The card's title is shown

  Scenario: AC4: The card also has a text field, which may be multi-line, containing a more detailed description
    Given A card exists
    When A user views a card in the card display section
    Then The card's description is shown

  Scenario: AC5: One or more keywords/phrases can be added.
    Given A user exists
    When A user creates a card with keywords: "A", "B", "C", and "D"
    Then The card's keywords are successfully saved with the card

  Scenario: AC6: The newly-created card is added to the corresponding marketplace section.
    Given A card exists in the "ForSale" section
    When A user views the "ForSale" section
    Then The card is successfully displayed in this section

  Scenario: AC6: The newly-created card is added to the corresponding marketplace section.
    Given A card exists in the "Wanted" section
    When A user views the "Wanted" section
    Then The card is successfully displayed in this section

  Scenario: AC6: The newly-created card is added to the corresponding marketplace section.
    Given A card exists in the "Exchange" section
    When A user views the "Exchange" section
    Then The card is successfully displayed in this section
