package ctr.stuba.xss.server.comm;

import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.server.engine.EngineWorker;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SessionFactory;

/**
 * Listens for TCP connections on management interface.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class ManagementProtocolListener extends ThreadedWorker {

    protected final SessionFactory mysqlSessionFactory;
    protected final EngineWorker engine;
    protected final Properties config;
    protected ServerSocket serverSocket;
    protected List<ManagementProtocolClientHandler> clientHandlers = new ArrayList<>();

    public ManagementProtocolListener(SessionFactory mysqlSessionFactory, EngineWorker engine, Properties config) {
        this.mysqlSessionFactory = mysqlSessionFactory;
        this.engine = engine;
        this.config = config;
    }

    public void removeClientHandler(ManagementProtocolClientHandler handler) {
        handler.interrupt();
        this.clientHandlers.remove(handler);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(new Integer(this.config.getProperty("xss.mp.listenport")), 0, Inet4Address.getByName(this.config.getProperty("xss.mp.listenaddress")));
            while (true) {
                Socket clientSocket = this.serverSocket.accept();
                ManagementProtocolClientHandler clientHandler = new ManagementProtocolClientHandler(this.mysqlSessionFactory, this, this.engine, clientSocket);
                this.clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ManagementProtocolListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            for (ManagementProtocolClientHandler handler : this.clientHandlers) {
                handler.interrupt();
            }
        }

    }
}
