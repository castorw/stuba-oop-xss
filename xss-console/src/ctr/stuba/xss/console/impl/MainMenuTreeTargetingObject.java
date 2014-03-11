package ctr.stuba.xss.console.impl;

import ctr.stuba.xss.console.panel.DefaultPanel;

/**
 * Targeting object allows menu item to specify target. For example sensor.
 * Contains obvious data.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class MainMenuTreeTargetingObject {

    protected final DefaultPanel targetPanel;
    protected final int targetObjectId;
    protected final String label;

    public MainMenuTreeTargetingObject(String label, DefaultPanel targetPanel, int targetObjectId) {
        this.targetPanel = targetPanel;
        this.targetObjectId = targetObjectId;
        this.label = label;
    }

    public DefaultPanel getTargetPanel() {
        return this.targetPanel;
    }

    public int getTargetObejctId() {
        return this.targetObjectId;
    }

    public String getLabel() {
        return this.label;
    }
}
