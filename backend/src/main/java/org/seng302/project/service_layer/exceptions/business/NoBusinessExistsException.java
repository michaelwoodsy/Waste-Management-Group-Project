package org.seng302.project.service_layer.exceptions.business;

/**
 * Exception triggered when a business with a given ID doesn't exist
 */
public class NoBusinessExistsException extends RuntimeException {
    public NoBusinessExistsException(Integer id) {
        super(String.format("NoBusinessExistsException: no business with id %d exists.", id));
    }
}
