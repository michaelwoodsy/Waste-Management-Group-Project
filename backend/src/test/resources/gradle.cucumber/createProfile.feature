Feature: U1 - Registering and logging into an individual account

  Scenario: AC1: Assuming I am not already logged in, the application gives me the ability to either log in or register (create) a new account. When registering, the mandatory attributes are clearly marked.
    Given I am trying to register as a new User
    When I try to create the account with details:
      |email       | password | firstName | lastName | dateOfBirth | homeAddress
      |tom@gmail.com  | Ab123456 | Tom       | Rizzi    | 2001-02-15  | { country: New Zealand }
    Then The account is created.
    And The userId is returned.

  Scenario: AC2: The username I use to log in should be my email address that I have previously registered in the system. If I try to register an account with an email address that is already registered, the system should not create the account but let me know. Similarly, if I try to log in with an email address that has not been registered, the system should let me know.
    Given I am a user trying to register a new User with an already taken email:
      |email       | password | firstName | lastName | dateOfBirth | homeAddress
      |tom@gmail.com  | Ab123456 | Tom       | Rizzi    | 2001-02-15  | { country: New Zealand }
    When I try to create an account
    Then An error message is returned to say the email is already taken

  Scenario: AC4: Appropriate validation is carried out and errors are clearly conveyed.
    Given I am a user trying to register a new User:
      |email       | firstName | lastName | dateOfBirth | homeAddress
      |tom@gmail.com  | Tom       | Rizzi    | 2001-02-15  | { country: New Zealand }
    When I try to create an account
    Then An error message is shown that some required fields are missing, in particular the password.

  Scenario: AC6: Appropriate error messages should be shown (e.g. mandatory field not filled in). The error message should help me understand the problem and the location of the problem so that I can easily fix it.
    Given I am a user trying to register a new User without a firstName:
      |email       | password  | lastName | dateOfBirth | homeAddress
      |tom@gmail.com  | Ab123456  | Rizzi    | 2001-02-15  | { country: New Zealand }
    When I try to create an account
    Then An error message is shown saying the firstName is required.

    Given I am a user trying to register a new User without a lastName:
      |email       | password  | firstName | dateOfBirth | homeAddress
      |tom@gmail.com  | Ab123456  | Tom       | 2001-02-15  | { country: New Zealand }
    When I try to create an account
    Then An error message is shown saying the lastName is required.

    Given I am a user trying to register a new User without a username:
      |lastName  | password  | firstName | dateOfBirth | homeAddress
      |Rizzi     | Ab123456  | Tom       | 2001-02-15  | { country: New Zealand }
    When I try to create an account
    Then An error message is shown saying the email is required.
