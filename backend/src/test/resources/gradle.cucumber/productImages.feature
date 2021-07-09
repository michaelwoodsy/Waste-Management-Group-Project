Feature: U16 - Product Images

  Scenario: AC1: I can upload one or more images for the product.
    Given A product has no images
    When I upload an image
    Then The image is saved in the repository on the product

  Scenario: AC2: The default primary image is the first image uploaded
    Given A product has no images
    When I upload an image
    Then The uploaded image is the primary image for the product

  Scenario: AC2: The user is able to change which image is the primary image
    Given A product has at least 2 images
    When I change the primary image from the first image to the second image
    Then The primary image for that product is the second image

  Scenario: AC3: A thumbnail of the primary image is automatically created
    Given A product has no images
    When I upload an image
    Then The uploaded image has a thumbnail

  Scenario: AC4: I can delete product images
    Given A product has an image
    When I delete an image
    Then The product no longer has that image as one of it's images
    And The image is no longer saved