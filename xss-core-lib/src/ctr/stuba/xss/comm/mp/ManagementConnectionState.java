package ctr.stuba.xss.comm.mp;

/**
 * Enumeration explains management connection status.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public enum ManagementConnectionState {

    /**
     * Client disconnected.
     */
    DISCONNECTED,
    /**
     * Awaiting for client login.
     */
    AWAITING_LOGIN,
    /**
     * Client connected and established.
     */
    CONNECTED
}
