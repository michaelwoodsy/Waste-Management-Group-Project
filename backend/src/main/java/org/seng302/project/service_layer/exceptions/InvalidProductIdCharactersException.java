package org.seng302.project.service_layer.exceptions;

public class InvalidProductIdCharactersException extends RuntimeException {
    public InvalidProductIdCharactersException() {
        super("InvalidProductIdCharactersException: " +
                "This productId contains invalid characters. Acceptable characters are letters, numbers and dashes.");
    }
}
