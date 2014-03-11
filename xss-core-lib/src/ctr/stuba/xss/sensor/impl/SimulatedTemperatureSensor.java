package ctr.stuba.xss.sensor.impl;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.cause.SensorAlarmCause;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.Sensor;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import java.util.Date;
import java.util.Random;
import org.hibernate.SessionFactory;

public class SimulatedTemperatureSensor extends Sensor {

    protected Date alarmStart = null;
    private boolean thresholded = true;

    public SimulatedTemperatureSensor(SessionFactory mysqlSessionFactory, SensorMessageQueueInterface messageQueue, XssSensor databaseRecord) {
        super(mysqlSessionFactory, messageQueue, databaseRecord);
        this.valueClass = Float.class;
        this.value = new Float(22);
    }

    @Override
    protected void loop() {
        Random rand = new Random(new Date().getTime());
        boolean action = rand.nextBoolean();
        float stepDelta = ((float) rand.nextInt(100) / 10000F);
        Float f = (Float) this.value;
        if (action) {
            f += stepDelta;
        } else {
            f -= stepDelta;
        }
        this.value = f;
        if (f > 40.0f) {
            if (!this.thresholded) {
                this.triggerAlarm(SensorAlarmCause.SIMULATED_RANDOM_EVENT);
            }
            this.thresholded = true;
        } else {
            this.thresholded = false;
        }
    }

    @Override
    public void emulateAlarm(Cause cause) {
        this.setValue((float) this.getValue() + 20);
        this.triggerAlarm(SensorAlarmCause.SIMULATED_RANDOM_EVENT);
    }
}
