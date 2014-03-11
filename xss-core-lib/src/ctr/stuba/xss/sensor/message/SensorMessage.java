package ctr.stuba.xss.sensor.message;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.sensor.Sensor;

/**
 * Unit to transfer message from sensor to engine.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorMessage {

    protected final Sensor sourceSensor;
    protected final SensorMessageType type;
    protected Cause cause;

    public SensorMessage(Sensor sourceSensor, SensorMessageType type, Cause cause) {
        this.sourceSensor = sourceSensor;
        this.type = type;
        this.cause = cause;
    }

    public SensorMessage(Sensor sourceSensor, SensorMessageType type) {
        this.sourceSensor = sourceSensor;
        this.type = type;
    }

    public Sensor getSourceSensor() {
        return this.sourceSensor;
    }

    public SensorMessageType getType() {
        return this.type;
    }

    public Cause getCause() {
        return this.cause;
    }
}