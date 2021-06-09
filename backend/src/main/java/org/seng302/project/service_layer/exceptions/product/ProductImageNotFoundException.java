package org.seng302.project.service_layer.exceptions.product;

/**
 * Exception thrown by setPrimaryImage() method in ProductImagesController
 * when request is made with an image id that doesn't correspond to one
 * of the product's images.
 */
public class ProductImageNotFoundException extends RuntimeException {
    public ProductImageNotFoundException(String productId, Integer imageId) {
        super(String.format("ProductImageNotFoundException: Product %s does not have an image with id %d",
                productId, imageId));
    }
}
