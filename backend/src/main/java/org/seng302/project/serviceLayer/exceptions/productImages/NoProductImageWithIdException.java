package org.seng302.project.serviceLayer.exceptions.productImages;

/**
 * Exception thrown by setPrimaryImage() method in ProductImagesController
 * when request is made with an image id that doesn't correspond to one
 * of the product's images.
 */
public class NoProductImageWithIdException extends RuntimeException {
    public NoProductImageWithIdException(String productId, Integer imageId) {
        super(String.format("NoProductImageWithIdException: Product %s does not have an image with id %d",
                productId, imageId));
    }
}
