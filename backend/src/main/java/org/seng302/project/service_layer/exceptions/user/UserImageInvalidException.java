package org.seng302.project.service_layer.exceptions.user;

/**
 * Exception thrown by addImage() method in UserImagesController
 * when request is made with an image file that is invalid
 */
public class UserImageInvalidException extends RuntimeException {
    public UserImageInvalidException() {
        super("UserImageInvalidException: Image supplied is of invalid type");
    }
}
