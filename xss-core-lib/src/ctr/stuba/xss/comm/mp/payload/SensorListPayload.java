package ctr.stuba.xss.comm.mp.payload;

import ctr.stuba.xss.sensor.SensorData;
import java.util.List;

/**
 * Payload containing list of sensors.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorListPayload extends Payload {

    protected List<SensorData> sensorList;

    public List<SensorData> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<SensorData> sensorList) {
        this.sensorList = sensorList;
    }
}
