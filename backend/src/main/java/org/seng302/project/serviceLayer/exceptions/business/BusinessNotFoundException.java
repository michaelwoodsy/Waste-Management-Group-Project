package org.seng302.project.serviceLayer.exceptions.business;

import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class BusinessNotFoundException extends NotAcceptableException {

    public BusinessNotFoundException(Integer id) {
        super(String.format("BusinessNotFoundException: no business with id %d exists.", id));
    }

}
