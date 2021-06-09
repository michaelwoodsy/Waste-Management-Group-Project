package org.seng302.project.service_layer.exceptions.business;

import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class BusinessNotFoundException extends NotAcceptableException {

    public BusinessNotFoundException(Integer id) {
        super(String.format("NoBusinessExistsException: no business with id %d exists.", id));
    }

}
