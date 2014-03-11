package ctr.stuba.xss.server.comm;

import ctr.stuba.xss.cause.SensorAlarmCause;
import ctr.stuba.xss.cause.SensorChangeCause;
import ctr.stuba.xss.comm.mp.ManagementConnectionState;
import ctr.stuba.xss.comm.mp.ManagementProtocolConstants;
import ctr.stuba.xss.comm.mp.Message;
import ctr.stuba.xss.comm.mp.MessageType;
import ctr.stuba.xss.comm.mp.payload.LoginRequestPayload;
import ctr.stuba.xss.comm.mp.payload.SensorAddPayload;
import ctr.stuba.xss.comm.mp.payload.SensorIdPayload;
import ctr.stuba.xss.comm.mp.payload.SensorListPayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoPayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoUpdatePayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoUpdateRequestPayload;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.ManagementEvent;
import ctr.stuba.xss.hbm.XssEvent;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.hbm.XssUser;
import ctr.stuba.xss.sensor.OperationalStatus;
import ctr.stuba.xss.sensor.Sensor;
import ctr.stuba.xss.sensor.SensorData;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import ctr.stuba.xss.server.engine.EngineWorker;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Handles client communication.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class ManagementProtocolClientHandler extends ThreadedWorker {

    protected final SessionFactory mysqlSessionFactory;
    protected final ManagementProtocolListener mpListener;
    protected final EngineWorker engineWorker;
    protected final Socket clientSocket;
    protected ObjectInputStream clientInputStream;
    protected ObjectOutputStream clientOutputStream;
    protected ManagementConnectionState state = ManagementConnectionState.AWAITING_LOGIN;
    protected XssUser userRecord;

    public ManagementProtocolClientHandler(SessionFactory mysqlSessionFactory, ManagementProtocolListener mpListener, EngineWorker engineWorker, Socket clientSocket) {
        this.mysqlSessionFactory = mysqlSessionFactory;
        this.mpListener = mpListener;
        this.engineWorker = engineWorker;
        this.clientSocket = clientSocket;
    }

    protected void setState(ManagementConnectionState state) {
        this.state = state;
    }

    protected void sendMessage(Message message) throws IOException {
        this.clientOutputStream.reset();
        this.clientOutputStream.writeObject(message);
        this.clientOutputStream.flush();
    }

    protected Message receiveMessage() throws IOException, ClassNotFoundException {
        return ((Message) this.clientInputStream.readObject());
    }

    protected String getClientHostPortString() {
        return this.clientSocket.getInetAddress().getHostName() + "(" + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + ")";
    }

    private void waitForLogin() throws IOException, ClassNotFoundException, ManagementLoginFailedException, EventAlreadyRecordedException {
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        try {
            this.clientSocket.setSoTimeout(ManagementProtocolConstants.SERVER_LOGIN_READ_TIMEOUT);
            Message loginMessage = this.receiveMessage();
            if (loginMessage.getType() == MessageType.LOGIN_REQUEST) {
                LoginRequestPayload payload = (LoginRequestPayload) loginMessage.getRequestPayload();
                mysqlSession.beginTransaction();
                XssUser loginUserRecord = (XssUser) mysqlSession.createQuery("from XssUser where username='" + payload.getUsername() + "'").uniqueResult();
                mysqlSession.getTransaction().commit();
                if (loginUserRecord == null) {
                    throw new ManagementLoginFailedException("User '" + payload.getUsername() + "' not found");
                } else {
                    if (loginUserRecord.getEnabled() == 1) {
                        if (loginUserRecord.getPassword().equals(payload.getPassword())) {
                            Message loginResponseMessage = new Message(MessageType.LOGIN_RESPONSE, null, new Integer(1));
                            this.sendMessage(loginResponseMessage);
                            this.setState(ManagementConnectionState.CONNECTED);
                            this.userRecord = loginUserRecord;
                            mysqlSession.beginTransaction();
                            this.userRecord.setLastLoginTime(new Date());
                            mysqlSession.getTransaction().commit();
                            new ManagementEvent(mysqlSession, this.getClientHostPortString() + " - User '" + this.userRecord.getUsername() + "' logged in").write();
                        } else {
                            throw new ManagementLoginFailedException("Invalid password entered for '" + loginUserRecord.getUsername() + "'");
                        }
                    } else {
                        throw new ManagementLoginFailedException("Account is disabled for user '" + loginUserRecord.getUsername() + "'");
                    }
                }
            } else {
                throw new ManagementLoginFailedException("Invalid login request");
            }
        } catch (SocketTimeoutException ex) {
            throw new ManagementLoginFailedException("Login timed out");
        } finally {
            mysqlSession.disconnect();
        }
    }

    protected void parseReceivedMessage(Message message) throws IOException {
        Message responseMessage = null;
        switch (message.getType()) {
            case DATA_SYSTEM_RUNTIME_INFO_REQUEST: {
                SystemRuntimeInfoPayload systemInfoPayload = new SystemRuntimeInfoPayload();
                systemInfoPayload.setServerHostname(this.engineWorker.getServer().getConfig().getProperty("xss.server.hostname"));
                systemInfoPayload.setServerInstanceName(this.engineWorker.getServer().getConfig().getProperty("xss.server.instancename"));
                systemInfoPayload.setServerVersion(this.engineWorker.getServer().getVersionString());
                systemInfoPayload.setServerStartDate(this.engineWorker.getServer().getStartDate());
                systemInfoPayload.setEngineRunning(this.engineWorker.isRunning());
                if (systemInfoPayload.getEngineRunning()) {
                    systemInfoPayload.setEngineStatus(this.engineWorker.getStatus());
                    systemInfoPayload.setEngineSensorCount(this.engineWorker.getSensors().size());
                    systemInfoPayload.setEngineLastStatusChangeDate(this.engineWorker.getLastStatusChangeDate());
                    systemInfoPayload.setEngineStartDate(this.engineWorker.getStartDate());
                }
                responseMessage = new Message(MessageType.DATA_RESPONSE, null, systemInfoPayload);
                break;
            }
            case DATA_SYSTEM_RUNTIME_UPDATE_REQUEST: {
                SystemRuntimeInfoUpdatePayload payload = new SystemRuntimeInfoUpdatePayload();
                payload.setEngineRunning(this.engineWorker.isRunning());
                if (payload.getEngineRunning()) {
                    payload.setEngineStatus(this.engineWorker.getStatus());
                    payload.setEngineLastStatusChangeDate(this.engineWorker.getLastStatusChangeDate());
                }
                SystemRuntimeInfoUpdateRequestPayload requestPayload = (SystemRuntimeInfoUpdateRequestPayload) message.getRequestPayload();
                Session mysqlSession = this.mysqlSessionFactory.openSession();
                mysqlSession.beginTransaction();
                List<XssEvent> events;
                Criteria criteria = mysqlSession.createCriteria(XssEvent.class);
                criteria.addOrder(Order.asc("time"));
                if (requestPayload.getLastUpdate() == null) {
                    criteria.add(Restrictions.gt("time", new Date(new Date().getTime() - 3600000)));
                    events = criteria.list();
                } else {
                    criteria.add(Restrictions.gt("time", requestPayload.getLastUpdate()));
                    events = criteria.list();
                }
                payload.setLastEvents(events);
                mysqlSession.getTransaction().commit();
                mysqlSession.disconnect();
                responseMessage = new Message(MessageType.DATA_RESPONSE, null, payload);
                break;
            }
            case DATA_SENSOR_LIST_REQUEST: {
                SensorListPayload responsePayload = new SensorListPayload();
                List<SensorData> sensorDataList = new ArrayList<>();
                for (Sensor sensor : this.engineWorker.getSensors()) {
                    sensorDataList.add(sensor.getSensorData());
                }
                responsePayload.setSensorList(sensorDataList);
                responseMessage = new Message(MessageType.DATA_RESPONSE, null, responsePayload);
                break;
            }
            case SENSOR_TRIGGER: {
                SensorIdPayload payload = (SensorIdPayload) message.getRequestPayload();
                for (Sensor sensor : this.engineWorker.getSensors()) {
                    if (sensor.getDatabaseRecord().getId() == payload.getSensorId()) {
                        sensor.emulateAlarm(SensorAlarmCause.USER_REQUEST);
                    }
                }
                break;
            }
            case SENSOR_TOGGLE_OPERATIONAL_STATUS: {
                SensorIdPayload payload = (SensorIdPayload) message.getRequestPayload();
                for (Sensor sensor : this.engineWorker.getSensors()) {
                    if (sensor.getDatabaseRecord().getId() == payload.getSensorId()) {
                        if (sensor.getStatus() == OperationalStatus.UP) {
                            sensor.disable(SensorChangeCause.USER_REQUEST);
                        } else if (sensor.getStatus() == OperationalStatus.OPERATIONALLY_DOWN) {
                            sensor.enable(SensorChangeCause.USER_REQUEST);
                        }
                    }
                }
                break;
            }
            case SENSOR_REMOVE: {
                SensorIdPayload payload = (SensorIdPayload) message.getRequestPayload();
                Sensor s = null;
                for (Sensor sensor : this.engineWorker.getSensors()) {
                    if (sensor.getDatabaseRecord().getId() == payload.getSensorId()) {
                        Session mysqlSession = this.mysqlSessionFactory.openSession();
                        mysqlSession.beginTransaction();
                        mysqlSession.delete(sensor.getDatabaseRecord());
                        mysqlSession.getTransaction().commit();
                        mysqlSession.disconnect();
                        s = sensor;
                    }
                }
                this.engineWorker.getSensors().remove(s);
                break;
            }
            case SENSOR_ADD: {
                SensorAddPayload payload = (SensorAddPayload) message.getRequestPayload();
                XssSensor sensorRecord = new XssSensor();
                sensorRecord.setName(payload.getSensorName());
                sensorRecord.setDescription(payload.getDescription());
                sensorRecord.setType("simulated");
                sensorRecord.setTriggerType(payload.getSensorClass());
                sensorRecord.setAdminStatus((payload.isAdminStatus()) ? 1 : 0);
                sensorRecord.setAutoToggle((payload.isAutoToggleStatus()) ? 1 : 0);
                sensorRecord.setAutoEnableTime(payload.getAutoToggleStart());
                sensorRecord.setAutoDisableTime(payload.getAutoToggleEnd());
                sensorRecord.setTriggerRegProbability(new BigDecimal(payload.getRegProbability()).divide(new BigDecimal(10000)));
                Session mysqlSession = this.mysqlSessionFactory.openSession();
                mysqlSession.beginTransaction();
                mysqlSession.save(sensorRecord);
                mysqlSession.getTransaction().commit();
                mysqlSession.disconnect();

                Class[] constructorPrototype = new Class[3];
                constructorPrototype[0] = SessionFactory.class;
                constructorPrototype[1] = SensorMessageQueueInterface.class;
                constructorPrototype[2] = XssSensor.class;

                Object[] constructorParams = new Object[3];
                constructorParams[0] = this.mysqlSessionFactory;
                constructorParams[1] = this.engineWorker.getMessageQueue();
                constructorParams[2] = sensorRecord;
                try {
                    Class sensorClass = Class.forName(sensorRecord.getTriggerType());
                    Constructor sensorClassConstructor = sensorClass.getConstructor(constructorPrototype);
                    Sensor sensorObject = (Sensor) sensorClassConstructor.newInstance(constructorParams);
                    this.engineWorker.getSensors().add(sensorObject);
                    sensorObject.start();
                } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(ManagementProtocolClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        if (responseMessage != null) {
            this.sendMessage(responseMessage);
        }
    }

    @Override
    public void run() {
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        try {
            this.clientOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.clientOutputStream.flush();
            this.clientInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            new ManagementEvent(mysqlSession, this.getClientHostPortString() + " - Connected").write();
            this.waitForLogin();
            if (this.state == ManagementConnectionState.CONNECTED) {
                this.clientSocket.setSoTimeout(ManagementProtocolConstants.SERVER_DEFAULT_READ_TIMEOUT);
                while (true) {
                    try {
                        Message recvMessage = this.receiveMessage();
                        this.parseReceivedMessage(recvMessage);
                    } catch (SocketTimeoutException ex) {
                    }
                }
            }
        } catch (ClassNotFoundException | EventAlreadyRecordedException ex) {
            Logger.getLogger(ManagementProtocolClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            try {
                new ManagementEvent(mysqlSession, this.getClientHostPortString() + " - " + ex.getMessage()).write();
            } catch (EventAlreadyRecordedException ex2) {
                Logger.getLogger(ManagementProtocolClientHandler.class.getName()).log(Level.SEVERE, null, ex2);
            }
        } catch (ManagementLoginFailedException ex) {
            try {
                Message loginResponseMessage = new Message(MessageType.LOGIN_RESPONSE, null, new Integer(0));
                this.sendMessage(loginResponseMessage);
                new ManagementEvent(mysqlSession, this.getClientHostPortString() + " - " + ex.getMessage()).write();
                mysqlSession.disconnect();
            } catch (IOException | EventAlreadyRecordedException ex1) {
                Logger.getLogger(ManagementProtocolClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (this.clientSocket.isConnected()) {
                try {
                    this.clientSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ManagementProtocolClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            mysqlSession.disconnect();
            this.mpListener.removeClientHandler(this);
        }
    }
}
