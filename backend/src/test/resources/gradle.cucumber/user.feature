Feature: U1 - Registering and logging into an individual account

  Scenario: AC12 - I can log back into my profile with my username (email address) and password.
    Given I have created an account with the username "sarahbear77@gmail.com" and the password "password"
    When I log back into my profile with the username "sarahbear77@gmail.com" and the password "password"
    Then The login is successful