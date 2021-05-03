Feature: Logging in to a user account (U1)

  Scenario: AC3 - If when logging in, my details are incorrect, the system should generate an error message.
    Given I have created an account with the username "user1@gmail.com" and the password "Password123"
    When I try to log-in with username "user1@gmail.com" and incorrect password "password"
    Then Log-in is unsuccessful and the system generates an error message


  Scenario: AC12 - I can log back into my profile with my username (email address) and password.
    Given I have created an account with the username "user1@gmail.com" and the password "Password123"
    When I try to log-in with correct username "user1@gmail.com" and password "Password123"
    Then The log-in is successful