Feature: U1 - Registering and logging into an individual account

  Scenario: AC2 - If I try to register an account with an email address that is already registered, the system should not create the account but let me know
    Given An account exists with the email "sarahbear77@gmail.com"
    When I try to register with the email "sarahbear77@gmail.com"
    Then The registration is unsuccessful


  Scenario: AC12 - I can log back into my profile with my username (email address) and password.
    Given I have created an account with the username "sarahbear77@gmail.com" and the password "password"
    When I log back into my profile with the username "sarahbear77@gmail.com" and the password "password"
    Then The login is successful