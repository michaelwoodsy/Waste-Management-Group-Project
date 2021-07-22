Feature: U11 - Modifying Businesses

  Scenario: AC1: As an administrator of a business account, I can modify any of the attributes.
    Given I am the administrator of a business account
    When I try to edit the business details to:
      | name              | description          | type                            | country   |
      | New Business Name | Some new description | Accommodation and Food Services | Australia |
    Then The business details are updated

  Scenario Outline: AC2: All validation rules still apply. Mandatory attributes still remain mandatory.
    Given I am the administrator of a business account
    When I try to edit the business details to:
      | name   | description   | type   | country   |
      | <name> | <description> | <type> | <country> |
    Then A Bad Request status is returned to the user

    Examples:
      | name              | description          | type                            | country   |
      |                   | Some new description | Accommodation and Food Services | Australia |
      | New Business Name | Some new description |                                 | Australia |
      | New Business Name | Some new description | Accommodation and Food Services |           |
      | New Business Name | Some new description | Invalid Business Type           | Australia |

# TODO: Implement steps for AC3 once discussed

#  Scenario: AC4: I can upload one or more images.
#    Given I am the administrator of a business account
#    When I upload a new image for the business
#    Then The image is saved to the repository for the business

#  Scenario: AC5: One of these images is deemed to be the primary image. I can change the primary image.
#    Given I am the administrator of a business account
#    And The business has at least 2 images, the first being the primary image
#    When I change the primary image to be the second image
#    Then The business' primary image is the second image

#  Scenario: AC6: A Thumbnail of the primary image is created
#    Given I am the administrator of a business account
#    And The business has at least 2 images, the first being the primary image
#    When I change the primary image to be the second image
#    Then A thumbnail of the new primary image is created