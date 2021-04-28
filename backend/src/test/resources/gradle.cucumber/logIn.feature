Feature: Logging in to a user account (U1)

  Scenario: AC1 - Assuming I am not already logged in, the application gives me the ability to log-in.
    Given I am not already logged in to a user account
    When I click the link to log-in
    Then I am presented with the log-in screen

  Scenario: AC3 - If when logging in, my details are incorrect, the system should generate an error message.
    Given I have created an account with the username "user1@gmail.com" and the password "Password123"
    When I try to log-in with username "user1@gmail.com" and incorrect password "password"
    Then Log-in is unsuccessful and the system generates an error message

  Scenario: AC8 - On successful log-in, I am taken to my own profile page.
    Given I have created an account with the username "user1@gmail.com" and the password "Password123"
    When I try to log-in with correct username "user1@gmail.com" and password "Password123"
    Then I am taken to my profile page

  Scenario: AC12 - I can log back into my profile with my username (email address) and password.
    Given I have created an account with the username "user1@gmail.com" and the password "Password123"
    When I try to log-in with correct username "user1@gmail.com" and password "Password123"
    Then The log-in is successful