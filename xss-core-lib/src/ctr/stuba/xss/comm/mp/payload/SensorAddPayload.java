package ctr.stuba.xss.comm.mp.payload;

/**
 * New sensor data payload.
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorAddPayload extends Payload {

    private String sensorName;
    private String sensorClass;
    private int regProbability;
    private boolean adminStatus;
    private boolean autoToggleStatus;
    private int autoToggleStart;
    private int autoToggleEnd;
    private String description;

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorClass() {
        return sensorClass;
    }

    public void setSensorClass(String sensorClass) {
        this.sensorClass = sensorClass;
    }

    public int getRegProbability() {
        return regProbability;
    }

    public void setRegProbability(int regProbability) {
        this.regProbability = regProbability;
    }

    public boolean isAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(boolean adminStatus) {
        this.adminStatus = adminStatus;
    }

    public boolean isAutoToggleStatus() {
        return autoToggleStatus;
    }

    public void setAutoToggleStatus(boolean autoToggleStatus) {
        this.autoToggleStatus = autoToggleStatus;
    }

    public int getAutoToggleStart() {
        return autoToggleStart;
    }

    public void setAutoToggleStart(int autoToggleStart) {
        this.autoToggleStart = autoToggleStart;
    }

    public int getAutoToggleEnd() {
        return autoToggleEnd;
    }

    public void setAutoToggleEnd(int autoToggleEnd) {
        this.autoToggleEnd = autoToggleEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
