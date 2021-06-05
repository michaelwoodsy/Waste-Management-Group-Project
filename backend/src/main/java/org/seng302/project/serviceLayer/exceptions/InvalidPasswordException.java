package org.seng302.project.serviceLayer.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("InvalidPasswordException: password must contain a combination of uppercase letters, lowercase letters, and numbers");
    }

}
