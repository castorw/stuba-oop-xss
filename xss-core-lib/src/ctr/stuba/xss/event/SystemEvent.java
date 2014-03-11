package ctr.stuba.xss.event;

import ctr.stuba.xss.cause.EventCause;
import org.hibernate.Session;

public class SystemEvent extends Event {
    
    public SystemEvent(Session dbSession, String message) throws EventAlreadyRecordedException {
        super(dbSession, message, EventCause.SYSTEM_EVENT);
    }
}
