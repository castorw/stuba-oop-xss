package ctr.stuba.xss.sensor;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.cause.SensorChangeCause;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.SensorConfigurationEvent;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.message.SensorMessage;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import ctr.stuba.xss.sensor.message.SensorMessageType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Sensor basic class. Must be extended to gain functionality.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
abstract public class Sensor extends ThreadedWorker implements SensorInterface, Serializable, Cloneable {

    /**
     * Sensor value. Might differ depending on sensor type.
     */
    protected Object value;
    /**
     * Value type. Determines sensor's value type.
     */
    protected Class valueClass;
    /**
     * Database session. Required for loading and reloading sensor.
     */
    protected final SessionFactory mysqlSessionFactory;
    /**
     * Message queue. Used to send sensor messages to XSS engine.
     */
    protected final SensorMessageQueueInterface messageQueue;
    /**
     * Configuration database image. This database object contains all data
     * required for sensor configuration.
     */
    protected XssSensor databaseRecord;
    /**
     * Operational status of sensor. Initialized to NOT_INITIALIZED state before
     * sensor initialization is performed.
     */
    protected OperationalStatus status = OperationalStatus.INITIALIZATION_REQUIRED;
    /**
     * Synchronization lock for status changes.
     */
    protected final Object statusLock = new Object();
    /**
     * Last status change cause. Used mostly to identify reload originator.
     */
    protected Cause lastChangeCause = SensorChangeCause.INITIALIZATION;

    /**
     * Constructor handling only basic existence of object.
     *
     * @param mysqlSessionFactory MySQL session for database
     */
    public Sensor(SessionFactory mysqlSessionFactory, SensorMessageQueueInterface messageQueue, XssSensor databaseRecord) {
        this.mysqlSessionFactory = mysqlSessionFactory;
        this.messageQueue = messageQueue;
        this.databaseRecord = databaseRecord;
    }

    /**
     * Internal sensor loop. Specific for every sensor type. Must be override.
     * Just sleep the thread for few seconds every run if no internal activity
     * is required in final sensor code.
     */
    abstract protected void loop();

    /**
     * Sensor initialization function. Must initialize sensor to valid status.
     *
     * @throws SensorAdministrativelyDownException If status can't be set
     */
    protected void initialize(Cause cause) throws SensorAdministrativelyDownException, EventAlreadyRecordedException {
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        mysqlSession.beginTransaction();
        mysqlSession.refresh(this.databaseRecord);
        mysqlSession.getTransaction().commit();
        try {
            new SensorConfigurationEvent(mysqlSession, this.databaseRecord, "Sensor #" + this.databaseRecord.getId() + " has been (re)loaded.", cause).write();
        } catch (EventAlreadyRecordedException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        }
        mysqlSession.disconnect();
        if (this.databaseRecord.getAdminStatus() != 1) {
            this.setStatus(OperationalStatus.ADMINISTRATIVELY_DOWN);
        } else {
            this.setStatus(OperationalStatus.UP);
        }
    }

    /**
     * Main thread function. Handles sensor startup and basic functionality in
     * spite of status handling.
     */
    @Override
    public void run() {
        try {
            while (true) {
                if (this.status == OperationalStatus.INITIALIZATION_REQUIRED) {
                    this.initialize(this.lastChangeCause);
                }
                while (this.status == OperationalStatus.UP) {
                    Calendar currentTime = Calendar.getInstance();
                    int secondsInDay = currentTime.get(Calendar.HOUR_OF_DAY) * 3600;
                    secondsInDay += currentTime.get(Calendar.MINUTE) * 60;
                    secondsInDay += currentTime.get(Calendar.SECOND);
                    if (this.databaseRecord.getAutoToggle() == 1 && (secondsInDay < this.databaseRecord.getAutoEnableTime() || secondsInDay > this.databaseRecord.getAutoDisableTime())) {
                        this.disable(SensorChangeCause.SYSTEM_ROUTINE);
                    }
                    this.loop();
                    Thread.sleep(100);
                }
                while (this.status != OperationalStatus.UP && this.status != OperationalStatus.INITIALIZATION_REQUIRED) {
                    Calendar currentTime = Calendar.getInstance();
                    int secondsInDay = currentTime.get(Calendar.HOUR_OF_DAY) * 3600;
                    secondsInDay += currentTime.get(Calendar.MINUTE) * 60;
                    secondsInDay += currentTime.get(Calendar.SECOND);
                    if (this.lastChangeCause != SensorChangeCause.USER_REQUEST && this.databaseRecord.getAutoToggle() == 1 && (secondsInDay >= this.databaseRecord.getAutoEnableTime() && secondsInDay <= this.databaseRecord.getAutoDisableTime())) {
                        this.enable(SensorChangeCause.SYSTEM_ROUTINE);
                    }
                    synchronized (this.statusLock) {
                        this.statusLock.wait(1000);
                    }
                }
            }
        } catch (InterruptedException ex) {
            this.getLogger().log(Level.SEVERE, "Thread has been interrupted.", ex);
        } catch (SensorAdministrativelyDownException ex) {
            this.getLogger().log(Level.SEVERE, "Tried to bring up administratively disabled sensor.", ex);
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Triggers system alarm. System alarm is triggered and last alarm time is
     * updated in database record for sensor.
     *
     * @param cause Alarm cause
     */
    protected void triggerAlarm(Cause cause) {
        this.messageQueue.offerMessage(new SensorMessage(this, SensorMessageType.ALARM, cause));
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        mysqlSession.beginTransaction();
        this.databaseRecord.setLastTriggerTime(new Date());
        mysqlSession.update(this.databaseRecord);
        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();
    }

    /**
     * Sets sensor status. Attempts to set sensor status if it's possible.
     * Throws an exception if status change is not available.
     *
     * @param status Target status
     * @throws SensorAdministrativelyDownException Thrown if illegal status
     * change is requested
     */
    protected void setStatus(OperationalStatus status) throws SensorAdministrativelyDownException {
        switch (this.status) {
            case ADMINISTRATIVELY_DOWN: {
                if (status != OperationalStatus.INITIALIZATION_REQUIRED) {
                    throw new SensorAdministrativelyDownException("Sensor is administratively down. Its status can't be changed.");
                }
            }
            default: {
                synchronized (this.statusLock) {
                    this.status = status;
                    Session mysqlSession = this.mysqlSessionFactory.openSession();
                    try {
                        new SensorConfigurationEvent(mysqlSession, this.databaseRecord, "Sensor # " + this.databaseRecord.getId() + " status has been changed to " + this.status.toString(), this.lastChangeCause).write();
                    } catch (EventAlreadyRecordedException ex) {
                        Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    mysqlSession.beginTransaction();
                    this.databaseRecord.setLastStatus(this.status.getClass().getName() + "." + this.status.toString());
                    this.databaseRecord.setLastStatusChangeTime(new Date());
                    mysqlSession.update(this.databaseRecord);
                    mysqlSession.getTransaction().commit();
                    mysqlSession.disconnect();
                    this.statusLock.notifyAll();
                }
                break;
            }
        }
    }

    /**
     * Returns sensor status. Returns current sensor operational status.
     *
     * @return Operational status
     */
    public OperationalStatus getStatus() {
        return this.status;
    }

    /**
     * Requests sensor configuration reload. Set status to reload sensor
     * configuration.
     *
     * @param cause Cause of sensor reload
     */
    @Override
    public void reload(Cause cause) {
        try {
            this.lastChangeCause = cause;
            this.setStatus(OperationalStatus.INITIALIZATION_REQUIRED);
        } catch (SensorAdministrativelyDownException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Attempts to bring sensor up. Can be canceled if sensor is
     * administratively down.
     *
     * @param cause Cause of sensor bring up attempt.
     */
    @Override
    public void enable(Cause cause) {
        try {
            this.lastChangeCause = cause;
            this.setStatus(OperationalStatus.UP);
        } catch (SensorAdministrativelyDownException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Attempts to operationally shutdown sensor. Can be canceled if sensor is
     * administratively down, what's a case when sensor can't be shut down.
     *
     * @param cause Cause of sensor shutdown attempt.
     */
    @Override
    public void disable(Cause cause) {
        try {
            this.lastChangeCause = cause;
            this.setStatus(OperationalStatus.OPERATIONALLY_DOWN);
        } catch (SensorAdministrativelyDownException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method gets the database record of type XssSensor by which is currently
     * sensor running and returns it.
     *
     * @return XssSensor object containing running configuration of sensor
     */
    @Override
    public XssSensor getDatabaseRecord() {
        return this.databaseRecord;
    }

    /**
     * Sets simple integer value of sensor.
     *
     * @param value Value to set
     */
    protected void setValue(Object value) {
        this.value = value;
    }

    /**
     * Returns value of sensor if it's representable by integer.
     *
     * @return Value of sensor
     * @throws ValueUninterpretableException If can't be interpreted as integer
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns class that represents sensor value.
     *
     * @return Class
     */
    public Class getValueClass() {
        return this.valueClass;
    }

    /**
     * Returns sensor data record. Can be used for transferring sensor data.
     *
     * @return SensorData representing sensor
     */
    public SensorData getSensorData() {
        SensorData data = new SensorData();
        data.setDatabaseRecord(databaseRecord);
        data.setOperationalStatus(status);
        data.setValue(value);
        data.setValueClass(valueClass);
        return data;
    }
}
