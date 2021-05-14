Feature: Default Global Application Admin (U4)

#
#
#  Scenario: AC3 - The DGAA will have a predefined username and password. Changing this username and password would
#  require access to the server itself (e.g. changing it in the database).
#    Given The DGAA has predefined username "dgaa@resale.com" and password "1AmTheDGAA"
#    When The DGAA username and password are changed to "admin@resale.com" and "1AmTheAdmin"
#    Then The DGAA username and password are changed in the database
#
#  Scenario: AC4 - The DGAA can search for a particular individual.
#    Given There exists a user with the first name "Bob" and last name "Bean"
#    When The DGAA searches with the search string "Bob Bean"
#    Then The user with the first name "Bob" and last name "Bean" is returned to the DGAA

  Scenario: AC6 - The DGAA can make the individual a GAA.
    Given There exists a user with the first name "Bob" and last name "Bean"
    When The DGAA assigns admin rights to the user
    Then The role of the user is updated to GAA

  Scenario: AC7 - The DGAA can remove admin rights from any individual.
    Given There exists a user with a role of GAA
    When The DGAA removes admin rights from the user
    Then The role of the user is updated to user

  Scenario: AC8 - Only individuals can be given application admin rights.
  Business accounts cannot be given admin rights.
    Given There exists a business with the name "Bob's Beans"
    When A DGAA assigns admin rights to the business
    Then An exception is thrown
