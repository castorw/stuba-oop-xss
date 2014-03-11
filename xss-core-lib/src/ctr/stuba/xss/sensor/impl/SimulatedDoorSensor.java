package ctr.stuba.xss.sensor.impl;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.Sensor;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import java.util.Date;
import org.hibernate.SessionFactory;

public class SimulatedDoorSensor extends Sensor {

    /**
     * Date used to mark alarm start date and determine 15 seconds.
     */
    protected Date alarmStart = null;

    /**
     * Constructor by basic model from Sensor class. Extended on valueClass
     * definition, which is required and sets default idle value.
     *
     * @param mysqlSessionFactory Hibernate MySQL session factory used to spawn
     * database connection
     * @param messageQueue Reference to message queue responsible for delivery
     * of alarm and other messages from sensor to engine
     * @param databaseRecord Database record of sensor used to configure sensor
     */
    public SimulatedDoorSensor(SessionFactory mysqlSessionFactory, SensorMessageQueueInterface messageQueue, XssSensor databaseRecord) {
        super(mysqlSessionFactory, messageQueue, databaseRecord);
        this.valueClass = Integer.class;
        this.value = new Integer(0);
    }

    /**
     * Internal sensor loop. If needed cuts sensor value back to zero if alarm
     * state's 15 seconds pass.
     */
    @Override
    protected void loop() {
        Date currentDate = new Date();
        if ((Integer) this.getValue() == 1 && currentDate.getTime() - this.alarmStart.getTime() > 15000) {
            this.setValue(new Integer(0));
        }
    }

    /**
     * Alarm emulation method. Emulates an alarm value and notifies engine using
     * SensorMessage offer to sensor message queue.
     *
     * @param cause Cause of alarm emulation
     */
    @Override
    public void emulateAlarm(Cause cause) {
        this.setValue(new Integer(1));
        this.alarmStart = new Date();
        this.triggerAlarm(cause);
    }
}
