package ctr.stuba.xss.hbm;
// Generated Mar 16, 2013 2:55:43 AM by Hibernate Tools 3.2.1.GA

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XssSensor generated by hbm2java
 */
public class XssSensor implements java.io.Serializable, Cloneable {

    private Integer id;
    private String name;
    private String description;
    private String type;
    private String xsspsKey;
    private String triggerType;
    private BigDecimal triggerRegProbability;
    private int adminStatus;
    private int autoToggle;
    private Integer autoEnableTime;
    private Integer autoDisableTime;
    private String lastStatus;
    private Date lastStatusChangeTime;
    private Date lastTriggerTime;
    private Date lastTamperTime;
    private Date lastModifyTime;
    private Date createdTime;

    public XssSensor() {
    }

    public XssSensor(String name, String type, String triggerType, BigDecimal triggerRegProbability, int adminStatus, int autoToggle, Date createdTime) {
        this.name = name;
        this.type = type;
        this.triggerType = triggerType;
        this.triggerRegProbability = triggerRegProbability;
        this.adminStatus = adminStatus;
        this.autoToggle = autoToggle;
        this.createdTime = createdTime;
    }

    public XssSensor(String name, String description, String type, String xsspsKey, String triggerType, BigDecimal triggerRegProbability, int adminStatus, int autoToggle, Integer autoEnableTime, Integer autoDisableTime, String lastStatus, Date lastStatusChangeTime, Date lastTriggerTime, Date lastTamperTime, Date lastModifyTime, Date createdTime) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.xsspsKey = xsspsKey;
        this.triggerType = triggerType;
        this.triggerRegProbability = triggerRegProbability;
        this.adminStatus = adminStatus;
        this.autoToggle = autoToggle;
        this.autoEnableTime = autoEnableTime;
        this.autoDisableTime = autoDisableTime;
        this.lastStatus = lastStatus;
        this.lastStatusChangeTime = lastStatusChangeTime;
        this.lastTriggerTime = lastTriggerTime;
        this.lastTamperTime = lastTamperTime;
        this.lastModifyTime = lastModifyTime;
        this.createdTime = createdTime;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXsspsKey() {
        return this.xsspsKey;
    }

    public void setXsspsKey(String xsspsKey) {
        this.xsspsKey = xsspsKey;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public BigDecimal getTriggerRegProbability() {
        return this.triggerRegProbability;
    }

    public void setTriggerRegProbability(BigDecimal triggerRegProbability) {
        this.triggerRegProbability = triggerRegProbability;
    }

    public int getAdminStatus() {
        return this.adminStatus;
    }

    public void setAdminStatus(int adminStatus) {
        this.adminStatus = adminStatus;
    }

    public int getAutoToggle() {
        return this.autoToggle;
    }

    public void setAutoToggle(int autoToggle) {
        this.autoToggle = autoToggle;
    }

    public Integer getAutoEnableTime() {
        return this.autoEnableTime;
    }

    public void setAutoEnableTime(Integer autoEnableTime) {
        this.autoEnableTime = autoEnableTime;
    }

    public Integer getAutoDisableTime() {
        return this.autoDisableTime;
    }

    public void setAutoDisableTime(Integer autoDisableTime) {
        this.autoDisableTime = autoDisableTime;
    }

    public String getLastStatus() {
        return this.lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Date getLastStatusChangeTime() {
        return this.lastStatusChangeTime;
    }

    public void setLastStatusChangeTime(Date lastStatusChangeTime) {
        this.lastStatusChangeTime = lastStatusChangeTime;
    }

    public Date getLastTriggerTime() {
        return this.lastTriggerTime;
    }

    public void setLastTriggerTime(Date lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public Date getLastTamperTime() {
        return this.lastTamperTime;
    }

    public void setLastTamperTime(Date lastTamperTime) {
        this.lastTamperTime = lastTamperTime;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Date getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
