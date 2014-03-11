package ctr.stuba.xss.event;

import ctr.stuba.xss.cause.EventCause;
import org.hibernate.Session;

public class ManagementEvent extends Event {
    
    public ManagementEvent(Session dbSession, String message) throws EventAlreadyRecordedException {
        super(dbSession, message, EventCause.MANAGEMENT_EVENT);
    }
}
