package ctr.stuba.xss.sensor;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.cause.SensorChangeCause;
import ctr.stuba.xss.comm.psp.PhysicalSensorCommands;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.SensorConfigurationEvent;
import ctr.stuba.xss.event.SensorConnectionEvent;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.message.SensorMessage;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import ctr.stuba.xss.sensor.message.SensorMessageType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Generic class for physical sensors.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
abstract public class PhysicalSensor extends Sensor {

    protected Socket deviceSocket = null;
    protected InputStream deviceInputStream;
    protected OutputStream deviceOutputStream;

    public PhysicalSensor(SessionFactory mysqlSessionFactory, SensorMessageQueueInterface messageQueue, XssSensor databaseRecord) {
        super(mysqlSessionFactory, messageQueue, databaseRecord);
    }

    @Override
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
        if (this.databaseRecord.getAdminStatus() != 1) {
            this.setStatus(OperationalStatus.ADMINISTRATIVELY_DOWN);
        } else {
            if (this.deviceSocket == null) {
                new SensorConnectionEvent(mysqlSession, this.databaseRecord, "Physical sensor is not connected.", SensorChangeCause.CONNECTION_NOT_AVAILABLE).write();
                this.setStatus(OperationalStatus.OPERATIONALLY_DOWN);
            } else if (this.deviceSocket.isConnected()) {
                this.setStatus(OperationalStatus.UP);
            } else {
                new SensorConnectionEvent(mysqlSession, this.databaseRecord, "Connection to physical sensor has been lost.", SensorChangeCause.CONNECTION_LOST).write();
                this.deviceSocket = null;
                this.setStatus(OperationalStatus.OPERATIONALLY_DOWN);
            }
        }
        mysqlSession.disconnect();
    }

    public void setSocket(Socket deviceSocket) throws SensorAdministrativelyDownException {
        try {
            this.deviceSocket = deviceSocket;
            this.deviceOutputStream = this.deviceSocket.getOutputStream();
            this.deviceOutputStream.flush();
            this.deviceInputStream = this.deviceSocket.getInputStream();

            if (this.status != OperationalStatus.ADMINISTRATIVELY_DOWN) {
                this.deviceOutputStream.write(PhysicalSensorCommands.cmdAuthOk);
                this.deviceOutputStream.flush();
                this.setStatus(OperationalStatus.INITIALIZATION_REQUIRED);
            } else {
                this.deviceOutputStream.write(PhysicalSensorCommands.cmdAuthFail);
                this.deviceOutputStream.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicalSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void triggerTamper(Cause cause) {
        this.messageQueue.offerMessage(new SensorMessage(this, SensorMessageType.ALARM, cause));
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        mysqlSession.beginTransaction();
        this.databaseRecord.setLastTamperTime(new Date());
        mysqlSession.update(this.databaseRecord);
        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();
    }
}
