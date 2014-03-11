package ctr.stuba.xss.server.comm;

import ctr.stuba.xss.exception.GenericException;

public class ManagementLoginFailedException extends GenericException {

    public ManagementLoginFailedException(String message) {
        super(message);
    }
}
