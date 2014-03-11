package ctr.stuba.xss.sensor;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.hbm.XssSensor;

/**
 * Generic sensor interface.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public interface SensorInterface {

    public void reload(Cause cause);

    public void enable(Cause cause);

    public void disable(Cause cause);

    public void emulateAlarm(Cause cause);

    public XssSensor getDatabaseRecord();
}