package org.seng302.project.serviceLayer.exceptions.productImages;

public class NoProductImageWithIdException extends RuntimeException {
    public NoProductImageWithIdException(String productId, Integer imageId) {
        super(String.format("NoProductImageWithIdException: Product %s does not have an image with id %d",
                productId, imageId));
    }
}
