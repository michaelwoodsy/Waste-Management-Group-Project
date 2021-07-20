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

  Scenario: I can upload one or more images for a user.
    Given A user has no images
    When I upload an image for a user
    Then The image is saved in the repository on the user

  Scenario: I can delete user images
    Given A user has an image
    When I delete an image for a user
    Then The user no longer has that image as one of it's images
    And The user's image is no longer saved

  Scenario: AC6: The user is able to change which image is the primary image
    Given A user has at least 2 images the first is the primary image
    When The user changes the primary image to be the second image
    Then The primary image for the user is the second image

  Scenario: AC6: The user is able to change which image is the primary image
    Given A user has 0 images
    When An image is uploaded
    Then The uploaded image is the primary image for the user

  Scenario: AC7: A thumbnail of the primary image is automatically created
    Given A user has 0 images
    When An image is uploaded
    Then The uploaded image has a thumbnail created