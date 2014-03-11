package ctr.stuba.xss.server.engine;

import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.engine.EngineStatus;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.SensorAlarmEvent;
import ctr.stuba.xss.event.SensorConnectionEvent;
import ctr.stuba.xss.event.SystemEvent;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.Sensor;
import ctr.stuba.xss.sensor.message.SensorMessage;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import ctr.stuba.xss.sensor.message.SensorMessageType;
import ctr.stuba.xss.server.impl.Server;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

/**
 * Engine of whole system. Handles sensors, sensor operations, etc.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class EngineWorker extends ThreadedWorker {

    protected final Server server;
    protected final SessionFactory mysqlSessionFactory;
    protected final Properties config;
    private final SensorMessageQueue messageQueue;
    protected RandomEventGenerator randomEventGenerator;
    protected PhysicalSensorProtocolListener physicalListener;
    protected List<Sensor> sensorList = Collections.synchronizedList(new ArrayList());
    protected EngineStatus status = EngineStatus.IDLE;
    protected final Object statusLock = new Object();
    protected Date statusEntryDate = new Date();
    protected Session mysqlSession;
    protected Date startDate;

    public EngineWorker(Server server, SessionFactory mysqlSessionFactory, Properties config) {
        this.server = server;
        this.mysqlSessionFactory = mysqlSessionFactory;
        this.config = config;
        this.messageQueue = new SensorMessageQueue(this);
    }

    @Override
    public void run() {
        // create local database session
        this.mysqlSession = this.mysqlSessionFactory.openSession();
        try {
            this.startDate = new Date();
            // start sensor message queue
            this.getMessageQueue().start();

            // load sensors from database
            mysqlSession.beginTransaction();
            List<XssSensor> sensorRecords = mysqlSession.createCriteria(XssSensor.class).addOrder(Order.asc("name")).list();
            mysqlSession.getTransaction().commit();

            Class[] constructorPrototype = new Class[3];
            constructorPrototype[0] = SessionFactory.class;
            constructorPrototype[1] = SensorMessageQueueInterface.class;
            constructorPrototype[2] = XssSensor.class;

            Object[] constructorParams = new Object[3];
            constructorParams[0] = this.mysqlSessionFactory;
            constructorParams[1] = this.getMessageQueue();

            for (XssSensor sensorRecord : sensorRecords) {
                constructorParams[2] = sensorRecord;
                Class sensorClass = Class.forName(sensorRecord.getTriggerType());
                Constructor sensorClassConstructor = sensorClass.getConstructor(constructorPrototype);
                Sensor sensorObject = (Sensor) sensorClassConstructor.newInstance(constructorParams);
                this.sensorList.add(sensorObject);
            }

            // spawn sensor worker
            for (Sensor sensor : this.sensorList) {
                sensor.start();
            }

            // start random event generator
            if (config.getProperty("xss.reg.enabled").equals("yes")) {
                this.randomEventGenerator = new RandomEventGenerator(this, new Integer(config.getProperty("xss.reg.tickdelay")));
                this.randomEventGenerator.start();
                new SystemEvent(mysqlSession, "Random event generator started up.").write();
            }

            // start physical sensor protocol listener
            this.physicalListener = new PhysicalSensorProtocolListener(mysqlSessionFactory, this, this.config);
            this.physicalListener.start();

            // announce startup
            new SystemEvent(mysqlSession, "XSS Engine started up").write();

            // watch status
            while (true) {
                if (this.status == EngineStatus.ALARM) {
                    Date currentDate = new Date();
                    if (currentDate.getTime() - this.statusEntryDate.getTime() >= new Integer(this.config.getProperty("xss.alarm.duration"))) {
                        this.setStatus(EngineStatus.IDLE);
                    }
                    Thread.sleep(100);
                } else {
                    synchronized (this.statusLock) {
                        this.statusLock.wait();
                    }
                }
            }

        } catch (HibernateException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | EventAlreadyRecordedException | InterruptedException ex) {
            this.getLogger().log(Level.SEVERE, null, ex);
        } finally {
            for (Sensor sensor : this.sensorList) {
                sensor.interrupt();
            }
            this.mysqlSession.disconnect();
        }
    }

    protected void setStatus(EngineStatus status) throws EventAlreadyRecordedException {
        Session dbSession = this.mysqlSessionFactory.openSession();
        synchronized (this.statusLock) {
            if (this.status != status) {
                Date currentDate = new Date();
                long timeDiff = currentDate.getTime() - this.statusEntryDate.getTime();
                new SystemEvent(dbSession, "System status has changed to " + status.toString() + " after " + (timeDiff / 1000) + " seconds").write();

                this.status = status;
                this.statusEntryDate = new Date();
                this.statusLock.notifyAll();
            }
        }
        dbSession.disconnect();
    }

    public EngineStatus getStatus() {
        return this.status;
    }

    public void parseSensorMessage(SensorMessage message) throws EventAlreadyRecordedException {
        Session dbSession = this.mysqlSessionFactory.openSession();
        if (message.getType() == SensorMessageType.ALARM) {
            new SensorAlarmEvent(dbSession, message.getSourceSensor().getDatabaseRecord(), "Sensor " + message.getSourceSensor().getDatabaseRecord().getName() + " triggered alarm ", message.getCause()).write();
            this.setStatus(EngineStatus.ALARM);
        } else if (message.getType() == SensorMessageType.CONNECTION_STATUS) {
            new SensorConnectionEvent(dbSession, message.getSourceSensor().getDatabaseRecord(), "Sensor " + message.getSourceSensor().getDatabaseRecord().getName() + " triggered alarm ", message.getCause()).write();
        }
        dbSession.disconnect();
    }

    public List<Sensor> getSensors() {
        return this.sensorList;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Server getServer() {
        return this.server;
    }

    public Date getLastStatusChangeDate() {
        return this.statusEntryDate;
    }

    public SensorMessageQueue getMessageQueue() {
        return messageQueue;
    }
}
