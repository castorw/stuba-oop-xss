package ctr.stuba.xss.cause;

/**
 * Enumeration containing causes for sensor changes.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public enum SensorChangeCause implements Cause {

    /**
     * Operational change caused by user interaction.
     */
    USER_REQUEST,
    /**
     * Sensor configuration change caused by user interaction.
     */
    USER_CONFIG_CHANGE,
    /**
     * Connection lost. Cause of sensor change which happens when network
     * connection to physical sensor is lost.
     */
    CONNECTION_LOST,
    /**
     * Connection established. Cause of sensor change which happens when network
     * connection to physical sensor is successfully established.
     */
    CONNECTION_ESTABLISHED,
    /**
     * Connection not available. Event cause, when connection is not available.
     */
    CONNECTION_NOT_AVAILABLE,
    /**
     * System routing caused change.
     */
    SYSTEM_ROUTINE,
    /**
     * Sensor initialization caused change.
     */
    INITIALIZATION
}
