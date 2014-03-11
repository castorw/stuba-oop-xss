package ctr.stuba.xss.event;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.hbm.XssEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 * Event writer.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
abstract public class Event {

    protected final Session dbSession;
    protected final String message;
    protected final Cause cause;
    protected XssEvent eventRecord = null;
    protected Boolean eventWritten = false;

    /**
     * Create new event.
     *
     * @param dbSession mysql session
     * @param message message
     * @param cause event cause
     * @throws EventAlreadyRecordedException
     */
    public Event(Session dbSession, String message, Cause cause) throws EventAlreadyRecordedException {
        this.dbSession = dbSession;
        this.message = message;
        this.cause = cause;
        this.eventRecord = new XssEvent();
        this.eventRecord.setEvent(this.getClass().getName());
        this.eventRecord.setCause(this.cause.getClass().getName() + "." + this.cause.toString());
        this.eventRecord.setMessage(this.message);
    }

    /**
     * Writes event to database.
     *
     * @throws EventAlreadyRecordedException
     */
    public void write() throws EventAlreadyRecordedException {
        if (this.eventWritten) {
            throw new EventAlreadyRecordedException("Event has already been logged");
        } else {
            this.dbSession.beginTransaction();
            this.dbSession.save(this.eventRecord);
            this.dbSession.getTransaction().commit();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "EVENT, {0}.{1}: {2}", new Object[]{this.cause.getClass().getName(), this.cause.toString(), this.message});
            this.eventWritten = true;
        }
    }
}
