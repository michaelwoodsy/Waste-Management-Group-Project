Feature: U10 - Modifying Individuals

  Scenario: AC1: As a registered individual, I can update any of my attributes.
    Given I am logged in as a user
    When I try to edit my account to the details:
    | firstName | lastName | middleName | nickname | email | dateOfBirth | bio | phoneNumber | password |
    | Rutger    | van Kruiningen |  | Rodger | new.email@gmail.com | 2000-09-08 | My new bio | 022 123 4567 | SecurePassword123 |
    Then My details are updated.

  Scenario: AC2: All validation rules still apply. For example, I can only modify my date of birth if I still remain over the required age.
    Given I am logged in as a user
    When I try to edit my date of birth to 10 years ago
    Then An error message is returned to say the date of birth is too young.

  Scenario: AC3: Mandatory attributes still remain mandatory.
    Given I am logged in as a user
    When I try to edit my account and dont enter in a first name
    Then An error message is shown saying the first name is required.