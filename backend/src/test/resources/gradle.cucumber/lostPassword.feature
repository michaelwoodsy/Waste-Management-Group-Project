Feature: U25 Lost password

  Scenario: AC4:  The link can be used at most once (in case the user has remembered their password in the meantime)
  and expires after a fixed period (e.g. an hour).
    Given I have requested to reset my password
    And I have already used the password reset URL
    When I try to use the password reset URL
    Then I am unable to reuse the URL

  Scenario: AC4:  The link can be used at most once (in case the user has remembered their password in the meantime)
  and expires after a fixed period (e.g. an hour).
    Given I have requested to reset my password
    And An hour has passed
    When I try to use the password reset URL
    Then The URL has expired and I am unable to use it


  Scenario: AC5: A new password can be chosen.  It must obey the same rules as the original one.
  The old password is deleted and the new one can then be used to log in.
    Given I have requested to reset my password
    When I try to reset my password to "notValid"
    Then Then I am informed it is not a valid password


  Scenario: AC5: A new password can be chosen.  It must obey the same rules as the original one.
  The old password is deleted and the new one can then be used to log in.
    Given I have requested to reset my password
    When I try to reset my password to "ValidPa55word"
    Then I can log in with my new password "ValidPa55word"