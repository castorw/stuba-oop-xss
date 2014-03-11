package ctr.stuba.xss.comm.mp;

import java.io.Serializable;

/**
 * Network transportation unit.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class Message implements Serializable {

    protected final MessageType type;
    protected Object requestPayload;
    protected Object responsePayload;

    public Message(MessageType type, Object requestPayload, Object responsePayload) {
        this.type = type;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
    }

    public MessageType getType() {
        return this.type;
    }

    public Object getRequestPayload() {
        return this.requestPayload;
    }

    public Object getResponsePayload() {
        return this.responsePayload;
    }
}
