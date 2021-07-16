package org.seng302.project.serviceLayer.exceptions.user;

/**
 * Exception thrown by setPrimaryImage() method in UserImagesController
 * when request is made with an image id that doesn't correspond to one
 * of the user's images.
 */
public class UserImageNotFoundException extends RuntimeException {
    public UserImageNotFoundException(Integer userId, Integer imageId) {
        super(String.format("UserImageNotFoundException: User %s does not have an image with id %d",
                userId, imageId));
    }
}
