package ctr.stuba.xss.comm.mp.payload;

/**
 * Sensor ID payload.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorIdPayload extends Payload {

    protected int sensorId;

    public SensorIdPayload(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getSensorId() {
        return sensorId;
    }
}
