package org.seng302.project.webLayer.controllerAdvice;

import org.seng302.project.serviceLayer.exceptions.user.UserImageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserImageControllerAdvice {
    /**
     * Exception thrown by the setPrimaryImage() method when the imageId
     * given doesn't match any of the user's images.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(UserImageNotFoundException.class)
    public ResponseEntity<String> noUserImageWithId(UserImageNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
