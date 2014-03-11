package ctr.stuba.xss.server.engine;

import ctr.stuba.xss.comm.psp.PhysicalSensorCommands;
import ctr.stuba.xss.comm.psp.PhysicalSensorConstants;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.SystemEvent;
import ctr.stuba.xss.sensor.PhysicalSensor;
import ctr.stuba.xss.sensor.Sensor;
import ctr.stuba.xss.sensor.SensorAdministrativelyDownException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * TCP listener for connections from physical sensors.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class PhysicalSensorProtocolListener extends ThreadedWorker {

    protected final SessionFactory mysqlSessionFactory;
    protected final EngineWorker engine;
    protected final Properties config;
    protected ServerSocket serverSocket;

    public PhysicalSensorProtocolListener(SessionFactory mysqlSessionFactory, EngineWorker engine, Properties config) {
        this.mysqlSessionFactory = mysqlSessionFactory;
        this.engine = engine;
        this.config = config;
    }

    @Override
    public void run() {
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        try {
            this.serverSocket = new ServerSocket(new Integer(this.config.getProperty("xss.psp.listenport")), 0, Inet4Address.getByName(this.config.getProperty("xss.psp.listenaddress")));
            new SystemEvent(mysqlSession, "Physical sensor connection listener started.").write();
            while (true) {
                Socket client = this.serverSocket.accept();
                OutputStream outStream = client.getOutputStream();
                outStream.flush();
                InputStream inStream = client.getInputStream();
                int recvByte;

                // send get key command
                outStream.write(PhysicalSensorCommands.cmdGetKey);
                outStream.flush();

                // receive key
                String key = "";
                recvByte = inStream.read();
                if (recvByte == PhysicalSensorCommands.cmdSendKey) {
                    for (int i = 0; i < PhysicalSensorConstants.keyLength; i++) {
                        key += (char) inStream.read();
                    }
                    new SystemEvent(mysqlSession, "Physical sensor protocol connection from " + client.getInetAddress().getHostName() + "(" + client.getInetAddress().getHostAddress() + ":" + client.getPort() + ")").write();
                    Boolean sensorFound = Boolean.FALSE;
                    for (Sensor sensor : this.engine.getSensors()) {
                        if (sensor.getDatabaseRecord().getType().equals("physical") && sensor.getDatabaseRecord().getXsspsKey().equals(key)) {
                            PhysicalSensor physicalSensor = (PhysicalSensor) sensor;
                            physicalSensor.setSocket(client);
                            sensorFound = Boolean.TRUE;
                            break;
                        }
                    }
                    if (!sensorFound) {
                        new SystemEvent(mysqlSession, "No sensor found for key " + key + " from " + client.getInetAddress().getHostName() + "(" + client.getInetAddress().getHostAddress() + ":" + client.getPort() + ")").write();
                        outStream.write(PhysicalSensorCommands.cmdAuthFail);
                        outStream.flush();
                        client.close();
                    }
                } else {
                    new SystemEvent(mysqlSession, "Communication broken with " + client.getInetAddress().getHostName() + "(" + client.getInetAddress().getHostAddress() + ":" + client.getPort() + ")").write();
                    client.close();
                }
            }
        } catch (IOException | EventAlreadyRecordedException | SensorAdministrativelyDownException ex) {
            Logger.getLogger(PhysicalSensorProtocolListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            mysqlSession.disconnect();
        }
    }
}
