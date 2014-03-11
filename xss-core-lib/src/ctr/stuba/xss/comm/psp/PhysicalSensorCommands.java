package ctr.stuba.xss.comm.psp;

/**
 * Commands for physical sensor protocol.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public interface PhysicalSensorCommands {

    public static final int cmdGetKey = 0xa0;
    public static final int cmdSendKey = 0xa1;
    public static final int cmdAuthOk = 0xa2;
    public static final int cmdAuthFail = 0xa3;
    public static final int cmdKeepalive = 0xa4;
    public static final int cmdAlarm = 0xa5;
    public static final int cmdClose = 0xa6;
}
