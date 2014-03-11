package ctr.stuba.xss.comm.mp.payload;

import ctr.stuba.xss.engine.EngineStatus;
import java.util.Date;

/**
 * Runtime info payload.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SystemRuntimeInfoPayload extends Payload {

    protected String serverHostname;
    protected String serverInstanceName;
    protected Date serverStartDate;
    protected String serverVersion;
    protected Boolean engineRunning;
    protected EngineStatus engineStatus;
    protected Date engineLastStatusChangeDate;
    protected Integer engineSensorCount;
    protected Date engineStartDate;

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public String getServerInstanceName() {
        return serverInstanceName;
    }

    public void setServerInstanceName(String serverInstanceName) {
        this.serverInstanceName = serverInstanceName;
    }

    public Date getServerStartDate() {
        return serverStartDate;
    }

    public void setServerStartDate(Date serverStartDate) {
        this.serverStartDate = serverStartDate;
    }

    public Boolean getEngineRunning() {
        return engineRunning;
    }

    public void setEngineRunning(Boolean engineRunning) {
        this.engineRunning = engineRunning;
    }

    public EngineStatus getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(EngineStatus engineStatus) {
        this.engineStatus = engineStatus;
    }

    public Date getEngineLastStatusChangeDate() {
        return engineLastStatusChangeDate;
    }

    public void setEngineLastStatusChangeDate(Date engineLastStatusChangeDate) {
        this.engineLastStatusChangeDate = engineLastStatusChangeDate;
    }

    public Integer getEngineSensorCount() {
        return engineSensorCount;
    }

    public void setEngineSensorCount(Integer engineSensorCount) {
        this.engineSensorCount = engineSensorCount;
    }

    public Date getEngineStartDate() {
        return engineStartDate;
    }

    public void setEngineStartDate(Date engineStartDate) {
        this.engineStartDate = engineStartDate;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
}
