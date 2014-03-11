package ctr.stuba.xss.console.comm;

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
import ctr.stuba.xss.common.DigestUtils;
import ctr.stuba.xss.console.impl.MainFrame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class provides connection to remote XSS server. Handles all
 * connection-related activity.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class ManagementClient {

    protected final MainFrame mainFrame;
    protected Socket clientSocket;
    protected ObjectOutputStream clientOutputStream;
    protected ObjectInputStream clientInputStream;
    protected ManagementConnectionState state = ManagementConnectionState.DISCONNECTED;
    protected Date lastRuntimeDataUpdate = null;

    /**
     * Creates instance of ManagementClient.
     *
     * @param mainFrame
     */
    public ManagementClient(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Sets management client state.
     *
     * @param state
     */
    protected void setState(ManagementConnectionState state) {
        this.state = state;
    }

    /**
     * Send message over management connection.
     *
     * @param message Message to be sent
     * @throws IOException If output stream error occurs
     */
    protected void sendMessage(Message message) throws IOException {
        this.clientOutputStream.writeObject(message);
        this.clientOutputStream.flush();
    }

    /**
     * Establishes TCP connection with remote XSS management server. This method
     * doesn't perform login.
     *
     * @param hostname Host name or IP address of remote server
     * @param port TCP Port where XSS Management Server resides
     * @throws ManagementClientException If any error occurs
     */
    public void connect(String hostname, int port) throws ManagementClientException {
        try {
            this.clientSocket = new Socket(hostname, port);
            this.clientSocket.setSoTimeout(ManagementProtocolConstants.DEFAULT_TIMEOUT);
            this.clientOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.clientOutputStream.flush();
            this.clientInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            this.setState(ManagementConnectionState.AWAITING_LOGIN);
        } catch (IOException ex) {
            ManagementClientException ex2 = new ManagementClientException("I/O error occured");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Logs user into remote server. "Securely" using SHA-1 hash.
     *
     * @param username username
     * @param password Password
     * @throws ManagementClientException If login error occurs
     */
    public void login(String username, String password) throws ManagementClientException {
        try {
            this.clientSocket.setSoTimeout(ManagementProtocolConstants.CLIENT_LOGIN_READ_TIMEOUT);
            String passwordHashed = DigestUtils.fromBytes("SHA1", password.getBytes());
            LoginRequestPayload loginPayload = new LoginRequestPayload(username, passwordHashed);
            Message loginRequest = new Message(MessageType.LOGIN_REQUEST, loginPayload, null);
            this.sendMessage(loginRequest);
            Message loginResponse = (Message) this.clientInputStream.readObject();
            if (loginResponse.getType() != MessageType.LOGIN_RESPONSE) {
                throw new ManagementClientException("Invalid login response from server");
            } else {
                Integer responseValue = (Integer) loginResponse.getResponsePayload();
                if (responseValue.intValue() == 1) {
                    this.setState(ManagementConnectionState.CONNECTED);
                    this.clientSocket.setSoTimeout(ManagementProtocolConstants.CLIENT_DEFAULT_READ_TIMEOUT);
                } else {
                    throw new LoginFailedException("Invalid username or password");
                }
            }
        } catch (ClassNotFoundException | IOException | NoSuchAlgorithmException ex) {
            ManagementClientException ex2 = new ManagementClientException("Login failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Breaks down connection to remote server.
     */
    public void breakConnection() {
        if (this.clientSocket.isConnected()) {
            try {
                this.setState(ManagementConnectionState.DISCONNECTED);
                this.clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.mainFrame.setDisconnected();
    }

    /**
     * Reloads system runtime information.
     *
     * @return SystemRuntimeInfoPayload representing system status and basic
     * info
     * @throws ManagementClientException In case of any transit error
     */
    public SystemRuntimeInfoPayload getSystemRuntimeInfo() throws ManagementClientException {
        SystemRuntimeInfoPayload result = null;
        try {
            Message request = new Message(MessageType.DATA_SYSTEM_RUNTIME_INFO_REQUEST, null, null);
            this.sendMessage(request);
            Message response = (Message) this.clientInputStream.readObject();
            if (response.getType() == MessageType.DATA_RESPONSE) {
                result = (SystemRuntimeInfoPayload) response.getResponsePayload();
            }
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
        return result;
    }

    /**
     * Gets system info update. This is a smaller portion of the previous
     * payload.
     *
     * @return SystemRuntimeInfoUpdatePayload
     * @throws ManagementClientException If any error occurs
     */
    public SystemRuntimeInfoUpdatePayload getSystemRuntimeInfoUpdate() throws ManagementClientException {
        SystemRuntimeInfoUpdatePayload result = null;
        try {
            SystemRuntimeInfoUpdateRequestPayload requestPayload = new SystemRuntimeInfoUpdateRequestPayload();
            requestPayload.setLastUpdate(this.lastRuntimeDataUpdate);
            Message request = new Message(MessageType.DATA_SYSTEM_RUNTIME_UPDATE_REQUEST, requestPayload, null);
            this.sendMessage(request);

            Message response = (Message) this.clientInputStream.readObject();
            if (response.getType() == MessageType.DATA_RESPONSE) {
                result = (SystemRuntimeInfoUpdatePayload) response.getResponsePayload();
                if (result.getLastEvents().size() > 0) {
                    this.lastRuntimeDataUpdate = new Date(result.getLastEvents().get(result.getLastEvents().size() - 1).getTime().getTime());
                }
            }
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
        return result;
    }

    /**
     * Reloads sensor list from remote server. Returns payload containing the
     * server list.
     *
     * @return Payload containing server list
     * @throws ManagementClientException In case of any error
     */
    public SensorListPayload getSensorList() throws ManagementClientException {
        SensorListPayload result = null;
        try {
            Message request = new Message(MessageType.DATA_SENSOR_LIST_REQUEST, null, null);
            this.sendMessage(request);

            Message response = (Message) this.clientInputStream.readObject();
            if (response.getType() == MessageType.DATA_RESPONSE) {
                result = (SensorListPayload) response.getResponsePayload();
            }
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
        return result;
    }

    /**
     * Forces sensor to trigger an alarm.
     *
     * @param sensorId ID of target sensor.
     * @throws ManagementClientException If any error occurs
     */
    public void triggerSensorAlarm(int sensorId) throws ManagementClientException {
        try {
            Message request = new Message(MessageType.SENSOR_TRIGGER, new SensorIdPayload(sensorId), null);
            this.sendMessage(request);
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Toggles sensor operational state.
     *
     * @param sensorId Target sensor ID
     * @throws ManagementClientException Any error
     */
    public void toggleSensor(int sensorId) throws ManagementClientException {
        try {
            Message request = new Message(MessageType.SENSOR_TOGGLE_OPERATIONAL_STATUS, new SensorIdPayload(sensorId), null);
            this.sendMessage(request);
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Remove sensor from system.
     *
     * @param sensorId Target sensor ID
     * @throws ManagementClientException Any errors
     */
    public void removeSensor(int sensorId) throws ManagementClientException {
        try {
            Message request = new Message(MessageType.SENSOR_REMOVE, new SensorIdPayload(sensorId), null);
            this.sendMessage(request);
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Data acquire failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Add sensor to system. Prepared payload is required to add sensor.
     *
     * @param payload New sensor payload.
     * @throws ManagementClientException Any errors.
     */
    public void addSensor(SensorAddPayload payload) throws ManagementClientException {
        try {
            Message request = new Message(MessageType.SENSOR_ADD, payload, null);
            this.sendMessage(request);
        } catch (IOException ex) {
            this.breakConnection();
            Logger.getLogger(ManagementClient.class.getName()).log(Level.SEVERE, null, ex);
            ManagementClientException ex2 = new ManagementClientException("Sensor creation failed");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Returns current state of management client.
     *
     * @return State
     */
    public ManagementConnectionState getState() {
        return this.state;
    }
}
