package ctr.stuba.xss.sensor;

import ctr.stuba.xss.exception.GenericException;

/**
 * Sensor administratively down exception. Thrown when attempt to bring up
 * sensor which is administratively down occurs.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorAdministrativelyDownException extends GenericException {

    /**
     * Constructs exception with message
     *
     * @param message Message
     */
    public SensorAdministrativelyDownException(String message) {
        super(message);
    }
}
