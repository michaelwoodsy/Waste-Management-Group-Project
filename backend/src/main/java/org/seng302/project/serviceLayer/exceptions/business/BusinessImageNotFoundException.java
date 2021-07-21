package org.seng302.project.serviceLayer.exceptions.business;

/**
 * Exception thrown by setPrimaryImage() method in BusinessImageService
 * when request is made with an image id that doesn't correspond to one
 * of the business images.
 */
public class BusinessImageNotFoundException extends RuntimeException {
    public BusinessImageNotFoundException(Integer businessId, Integer imageId) {
        super(String.format("BusinessImageNotFoundException: Business %s does not have an image with id %d",
                businessId, imageId));    }
}
