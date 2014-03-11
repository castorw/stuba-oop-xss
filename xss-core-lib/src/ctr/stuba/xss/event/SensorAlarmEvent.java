package ctr.stuba.xss.event;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.hbm.XssSensor;
import org.hibernate.Session;

public class SensorAlarmEvent extends Event {

    public SensorAlarmEvent(Session dbSession, XssSensor sensor, String message, Cause cause) throws EventAlreadyRecordedException {
        super(dbSession, message, cause);
        this.eventRecord.setSensorId(sensor.getId());
    }
}