package ctr.stuba.xss.comm.mp;

/**
 * Types of network transfer messages.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public enum MessageType {

    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    DATA_SYSTEM_RUNTIME_INFO_REQUEST,
    DATA_SYSTEM_RUNTIME_UPDATE_REQUEST,
    DATA_SENSOR_LIST_REQUEST,
    DATA_RESPONSE,
    SENSOR_TRIGGER,
    SENSOR_TOGGLE_OPERATIONAL_STATUS,
    SENSOR_REMOVE,
    SENSOR_ADD
}
