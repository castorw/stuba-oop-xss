package ctr.stuba.xss.event;

import ctr.stuba.xss.exception.GenericException;

public class EventAlreadyRecordedException extends GenericException {

    public EventAlreadyRecordedException(String message) {
        super(message);
    }
}
