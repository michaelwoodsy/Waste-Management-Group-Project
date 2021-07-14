package org.seng302.project.serviceLayer.exceptions.dgaa;

import org.seng302.project.serviceLayer.exceptions.ForbiddenException;

public class ForbiddenSystemAdminActionException extends ForbiddenException {
    public ForbiddenSystemAdminActionException() {
        super("You must be a system admin to make this request");
    }
}
