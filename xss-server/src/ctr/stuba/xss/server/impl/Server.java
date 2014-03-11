package ctr.stuba.xss.server.impl;

import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.event.SystemEvent;
import ctr.stuba.xss.exception.GenericException;
import ctr.stuba.xss.server.comm.ManagementProtocolListener;
import ctr.stuba.xss.server.engine.EngineWorker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Server implementation. Starts engine, protocol listeners.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class Server {

    protected Properties config;
    protected ResourceBundle versionInfoBundle;
    protected final SessionFactory mysqlSessionFactory;
    protected final EngineWorker engineWorker;
    protected final ManagementProtocolListener mpListener;
    protected final Date startDate = new Date();

    public Server() {
        this.reloadConfig();
        // configure logger
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");

        // access versioning information
        this.versionInfoBundle = ResourceBundle.getBundle("version");

        // configure hibernate session factory
        Configuration hbConfig = new Configuration();
        hbConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hbConfig.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        hbConfig.setProperty("hibernate.connection.url", "jdbc:mysql://" + config.getProperty("xss.mysql.host") + ":" + config.getProperty("xss.mysql.port") + "/" + config.getProperty("xss.mysql.database"));
        hbConfig.setProperty("hibernate.connection.username", config.getProperty("xss.mysql.username"));
        hbConfig.setProperty("hibernate.connection.password", config.getProperty("xss.mysql.password"));
        hbConfig.setProperty("hibernate.connection.autoReconnect", "true");

        // add hibernate mappings
        hbConfig.addJar(new File("lib/xss-hbm-lib.jar"));

        // spawn hibernate session factory
        ServiceRegistryBuilder hbServiceRegistryBuilder = new ServiceRegistryBuilder().applySettings(hbConfig.getProperties());
        this.mysqlSessionFactory = hbConfig.buildSessionFactory(hbServiceRegistryBuilder.buildServiceRegistry());

        // create engine worker
        this.engineWorker = new EngineWorker(this, this.mysqlSessionFactory, this.config);

        this.mpListener = new ManagementProtocolListener(this.mysqlSessionFactory, this.engineWorker, this.config);

    }

    /**
     * Reload configuration command.
     */
    private void reloadConfig() {
        try {
            InputStream propsInputStream = new FileInputStream("xss-server.properties");
            Properties props = new Properties();
            props.load(propsInputStream);
            this.config = props;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stops engine worker.
     */
    private void stopServices() {
        this.engineWorker.interrupt();
    }

    /**
     * Starts engine worker and protocol listeners.
     */
    private void startServices() {
        if (!this.mpListener.isRunning()) {
            this.mpListener.start();
        }
        if (this.config.getProperty("xss.engine.enabled").equals("yes") && !this.engineWorker.isRunning()) {
            this.engineWorker.start();
        }
    }

    /**
     * Reloads configuration and restarts services.
     */
    protected void reload() {
        try {
            Session mysqlSession = this.mysqlSessionFactory.openSession();
            new SystemEvent(mysqlSession, "Reloading configuration and restarting services").write();
            this.reloadConfig();
            this.stopServices();
            this.startServices();
            mysqlSession.disconnect();
        } catch (EventAlreadyRecordedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts execution of server instance.
     */
    public void start() {

        try {
            Session mysqlSession = this.mysqlSessionFactory.openSession();
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "XSS Server v{0} build {1}\nLubomir Kaplan <castor@castor.sk>, PKSS13, LS 2012/2013, FITT STU BA", new Object[]{this.versionInfoBundle.getString("VERSION"), this.versionInfoBundle.getString("BUILD")});
            new SystemEvent(mysqlSession, "XSS Server " + this.getVersionString() + " starting up").write();
            mysqlSession.disconnect();
            this.startServices();
        } catch (GenericException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets server start date.
     *
     * @return
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * Gets configuration.
     *
     * @return
     */
    public Properties getConfig() {
        return this.config;
    }

    /**
     * Gets version string.
     *
     * @return
     */
    public String getVersionString() {
        return this.versionInfoBundle.getString("VERSION") + " build " + this.versionInfoBundle.getString("BUILD");
    }
}
