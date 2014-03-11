package ctr.stuba.xss.sensor.message;

/**
 * Interface representing message queue for sensor events.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public interface SensorMessageQueueInterface {

    public void offerMessage(SensorMessage message);
}
