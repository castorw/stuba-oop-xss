package ctr.stuba.xss.console.impl;

import ctr.stuba.xss.exception.GenericException;

public class DataReloadFailedException extends GenericException {

    public DataReloadFailedException(String message) {
        super(message);
    }
}
