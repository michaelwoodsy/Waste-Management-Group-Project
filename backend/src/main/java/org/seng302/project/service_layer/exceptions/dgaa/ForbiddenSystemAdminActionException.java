package org.seng302.project.service_layer.exceptions.dgaa;

import org.seng302.project.service_layer.exceptions.ForbiddenException;

public class ForbiddenSystemAdminActionException extends ForbiddenException {
    public ForbiddenSystemAdminActionException() {
        super("You must be a system admin to make this request");
    }
}
