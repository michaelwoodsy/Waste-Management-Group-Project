package org.seng302.project.service_layer.exceptions.product;

import org.seng302.project.service_layer.exceptions.NotAcceptableException;

/**
 * Exception thrown by setPrimaryImage() method in ProductImagesController
 * when request is made for a product that doesn't exist.
 * Different to NoProductExistsException because NoProductExistsException
 * gives a 400 response and we want a 406 response here.
 */
public class ProductNotFoundException extends NotAcceptableException {
    public ProductNotFoundException(String productName, Integer businessId) {
        super(String.format("ProductNotFoundException: no product with id '%s' found for business with id %d",
                productName, businessId));
    }
}
