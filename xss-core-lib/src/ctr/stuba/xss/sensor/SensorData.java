package ctr.stuba.xss.sensor;

import ctr.stuba.xss.hbm.XssSensor;
import java.io.Serializable;

/**
 * Serializable version of sensor data.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorData implements Serializable {

    protected Object value;
    protected Class valueClass;
    protected OperationalStatus operationalStatus;
    protected XssSensor databaseRecord;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    public OperationalStatus getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(OperationalStatus operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public XssSensor getDatabaseRecord() {
        return databaseRecord;
    }

    public void setDatabaseRecord(XssSensor databaseRecord) {
        this.databaseRecord = databaseRecord;
    }
}
