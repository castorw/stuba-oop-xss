package ctr.stuba.xss.comm.mp.payload;

import java.util.Date;

/**
 * Runtime info update request payload.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SystemRuntimeInfoUpdateRequestPayload extends Payload {

    protected Date lastUpdate;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
