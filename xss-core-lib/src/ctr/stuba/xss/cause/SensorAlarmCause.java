package ctr.stuba.xss.cause;

/**
 * Sensor alarm cases.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public enum SensorAlarmCause implements Cause {

    /**
     * User requested action.
     */
    USER_REQUEST,
    /**
     * Simulated random event by REG.
     */
    SIMULATED_RANDOM_EVENT,
    /**
     * Event from physical sensor.
     */
    REAL_EVENT,
    /**
     * Tamper event from physical sensor.
     */
    REAL_TAMPER_EVENT
}
