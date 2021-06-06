package org.seng302.project.serviceLayer.exceptions.dgaa;

public class DGAARevokeAdminSelfException extends RuntimeException {
    public DGAARevokeAdminSelfException() {
        super("DGAARevokeAdminSelfException: DGAA cannot revoke their own admin status.");
    }
}
