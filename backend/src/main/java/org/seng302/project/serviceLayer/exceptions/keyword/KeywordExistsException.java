package org.seng302.project.serviceLayer.exceptions.keyword;

import org.seng302.project.serviceLayer.exceptions.BadRequestException;

/**
 * Exception thrown when a keyword is added that already exists.
 */
public class KeywordExistsException extends BadRequestException {

    public KeywordExistsException(String name) {
        super(String.format("Keyword with name '%s' already exists.", name));
    }

}
