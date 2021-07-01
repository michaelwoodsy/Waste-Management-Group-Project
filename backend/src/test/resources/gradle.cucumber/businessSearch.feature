Feature: U23 - Search for business

  Scenario: AC2: I can enter a full name, or part of a name, as a search term.
    Given A business exists with the name "Cara's Cookies"
    When I enter "Cara's Cookies" as a search term
    Then The business called "Cara's Cookies" is in the search results

  Scenario: AC2: I can enter a full name, or part of a name, as a search term.
    Given A business exists with the name "Cara's Cookies"
    When I enter "Cookies" as a search term
    Then The business called "Cara's Cookies" is in the search results

  Scenario: AC2: If I enter more than one term then I can specify whether results must match all of the terms or any of them.
    Given A business exists with the name "Cara's Cookies"
    When I enter "Cookies OR Muffins" as a search term
    Then The business called "Cara's Cookies" is in the search results

  Scenario: AC2: If I enter more than one term then I can specify whether results must match all of the terms or any of them.
    Given A business exists with the name "Cara's Cookies"
    When I enter "Cookies AND Muffins" as a search term
    Then The business called "Cara's Cookies" is NOT in the search results

  Scenario: AC2: Terms containing spaces can be quoted (e.g. “New World”) if necessary to avoid confusion.
    Given A business exists with the name "Cara's Cookies" and a business exists with the name "Carmen's Cookies"
    When I enter ""Cara's Cookies"" as a search term
    Then The business called "Cara's Cookies" is the ONLY search result

  Scenario: AC4:  The search facility also allows searches by business type.
  Instead of, or as well as, typing in parts of business names, I can choose the business type from a dropdown or other suitable UI element.
    Given Two businesses exist with the type "Retail Trade"
    When I search by the business type "Retail Trade"
    Then The two businesses are the ONLY search results

  Scenario: AC4:  The search facility also allows searches by business type.
  Instead of, or as well as, typing in parts of business names, I can choose the business type from a dropdown or other suitable UI element.
    Given Two businesses, with the names "Cara's Cookies" and "Myrtle's Muffins" exist with the type "Retail Trade"
    When I search by the business type "Retail Trade" and the search term "Cookies"
    Then The business called "Cara's Cookies" is the ONLY search result