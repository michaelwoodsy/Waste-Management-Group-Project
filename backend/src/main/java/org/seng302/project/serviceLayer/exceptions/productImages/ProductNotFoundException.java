package org.seng302.project.serviceLayer.exceptions.productImages;

/**
 * Exception thrown by setPrimaryImage() method in ProductImagesController
 * when request is made for a product that doesn't exist.
 * Different to NoProductExistsException because NoProductExistsException
 * gives a 400 response and we want a 406 response here.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName, Integer businessId) {
        super(String.format("ProductNotFoundException: no product with id '%s' found for business with id %d",
                productName, businessId));
    }
}
