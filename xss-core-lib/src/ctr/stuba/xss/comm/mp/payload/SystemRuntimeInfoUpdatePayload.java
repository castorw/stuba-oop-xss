package ctr.stuba.xss.comm.mp.payload;

import ctr.stuba.xss.engine.EngineStatus;
import ctr.stuba.xss.hbm.XssEvent;
import java.util.Date;
import java.util.List;

/**
 * Runtime info update payload.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SystemRuntimeInfoUpdatePayload extends Payload {

    protected Boolean engineRunning;
    protected EngineStatus engineStatus;
    protected Date engineLastStatusChangeDate;
    protected List<XssEvent> lastEvents;

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

    public List<XssEvent> getLastEvents() {
        return lastEvents;
    }

    public void setLastEvents(List<XssEvent> lastEvents) {
        this.lastEvents = lastEvents;
    }
}
