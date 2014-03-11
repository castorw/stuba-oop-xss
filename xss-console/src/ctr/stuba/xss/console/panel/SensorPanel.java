package ctr.stuba.xss.console.panel;

import ctr.stuba.xss.console.comm.ManagementClientException;
import ctr.stuba.xss.console.impl.MainFrame;
import ctr.stuba.xss.sensor.SensorData;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

/**
 * Panel representing single sensor, giving ability to monitor it's status.
 * Basic controls are also available.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorPanel extends DefaultPanel {

    protected final MainFrame mainFrame;
    protected SensorData currentSensor;

    public SensorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    @Override
    protected void initializeData() {
        this.updateData(true);
    }

    @Override
    public Boolean deconfigurePanel() {
        return Boolean.TRUE;
    }

    public void updateData() {
        this.updateData(false);
    }

    public void updateData(boolean fullupdate) {
        if (this.getTargetingObject() == null) {
            return;
        }
        SensorData targetSensor = this.mainFrame.getSensorDataByDatabaseId(this.targetingObject.getTargetObejctId());

        this.currentSensor = targetSensor;

        if (targetSensor != null) {

            this.guiNameLabel.setText(targetSensor.getDatabaseRecord().getName());
            String[] classSplit = targetSensor.getDatabaseRecord().getTriggerType().split("\\.");
            this.guiSensorClassLabel.setText(classSplit[classSplit.length - 1]);
            this.guiTypeLabel.setText(targetSensor.getDatabaseRecord().getType());
            this.guiPssKeyLabel.setText((targetSensor.getDatabaseRecord().getXsspsKey() == null || targetSensor.getDatabaseRecord().getXsspsKey().trim().equals("-")) ? "-" : targetSensor.getDatabaseRecord().getXsspsKey());
            this.guiLastTriggerTimeLabel.setText((targetSensor.getDatabaseRecord().getLastTriggerTime() == null) ? "-" : targetSensor.getDatabaseRecord().getLastTriggerTime().toString());
            this.guiLastTamperTimeLabel.setText((targetSensor.getDatabaseRecord().getLastTamperTime() == null) ? "-" : targetSensor.getDatabaseRecord().getLastTamperTime().toString());
            this.guiCreateTimeLabel.setText((targetSensor.getDatabaseRecord().getCreatedTime() == null) ? "-" : targetSensor.getDatabaseRecord().getCreatedTime().toString());
            this.guiLastModifyTimeLabel.setText((targetSensor.getDatabaseRecord().getLastModifyTime() == null) ? "-" : targetSensor.getDatabaseRecord().getLastModifyTime().toString());
            this.guiCurrentValue.setText(targetSensor.getValue().toString());

            this.guiDescription.setText(targetSensor.getDatabaseRecord().getDescription());

            if (targetSensor.getDatabaseRecord().getAutoToggle() == 1) {
                String timeString = new Integer(targetSensor.getDatabaseRecord().getAutoEnableTime() / 3600).toString();
                timeString += ":" + new Integer((targetSensor.getDatabaseRecord().getAutoEnableTime() % 3600) / 60).toString();
                timeString += ":" + new Integer((targetSensor.getDatabaseRecord().getAutoEnableTime() % 3600) % 60).toString();
                timeString += " - ";
                timeString += new Integer(targetSensor.getDatabaseRecord().getAutoDisableTime() / 3600).toString();
                timeString += ":" + new Integer((targetSensor.getDatabaseRecord().getAutoDisableTime() % 3600) / 60).toString();
                timeString += ":" + new Integer((targetSensor.getDatabaseRecord().getAutoDisableTime() % 3600) % 60).toString();
                this.guiAutoToggleLabel.setText("Enabled (" + timeString + ")");
            } else {
                this.guiAutoToggleLabel.setText("Disabled");
            }

            if (targetSensor.getDatabaseRecord().getAdminStatus() == 1) {
                this.guiAdminStatusLabel.setText("Enabled");
                this.guiAdminStatusLabel.setForeground(Color.GREEN);
            } else {
                this.guiAdminStatusLabel.setText("Disabled");
                this.guiAdminStatusLabel.setForeground(Color.RED);
            }

            this.guiOperationalStatusLabel.setText(targetSensor.getOperationalStatus().toString());
            switch (targetSensor.getOperationalStatus()) {
                case UP: {
                    this.guiButtonUserAlarm.setEnabled(true);
                    this.guiButtonToggleSensor.setText("Disable Sensor");
                    this.guiButtonToggleSensor.setEnabled(true);
                    this.guiOperationalStatusLabel.setForeground(Color.GREEN);
                    break;
                }
                case INITIALIZATION_REQUIRED: {
                    this.guiButtonUserAlarm.setEnabled(false);
                    this.guiButtonToggleSensor.setEnabled(false);
                    this.guiOperationalStatusLabel.setForeground(Color.BLUE);
                    break;
                }
                case ADMINISTRATIVELY_DOWN: {
                    this.guiButtonUserAlarm.setEnabled(false);
                    this.guiButtonToggleSensor.setText("Enable Sensor");
                    this.guiButtonToggleSensor.setEnabled(false);
                    this.guiOperationalStatusLabel.setForeground(Color.RED);
                    break;
                }
                case OPERATIONALLY_DOWN: {
                    this.guiButtonUserAlarm.setEnabled(false);
                    this.guiButtonToggleSensor.setText("Enable Sensor");
                    this.guiButtonToggleSensor.setEnabled(true);
                    this.guiOperationalStatusLabel.setForeground(Color.RED);
                    break;
                }
                default: {
                    this.guiButtonUserAlarm.setEnabled(false);
                    this.guiButtonToggleSensor.setEnabled(false);
                    this.guiOperationalStatusLabel.setForeground(Color.RED);
                    break;
                }
            }

            this.guiRegProbabilityLabel.setText((targetSensor.getDatabaseRecord().getTriggerRegProbability().multiply(new BigDecimal(100))).toString() + " %");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        guiNameLabel = new javax.swing.JLabel();
        guiTypeLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        guiSensorClassLabel = new javax.swing.JLabel();
        guiAdminStatusLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        guiAutoToggleLabel = new javax.swing.JLabel();
        guiRegProbabilityLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        guiPssKeyLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        guiLastModifyTimeLabel = new javax.swing.JLabel();
        guiCreateTimeLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        guiCurrentValue = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        guiLastTriggerTimeLabel = new javax.swing.JLabel();
        guiLastTamperTimeLabel = new javax.swing.JLabel();
        guiOperationalStatusLabel = new javax.swing.JLabel();
        guiButtonUserAlarm = new javax.swing.JButton();
        guiButtonToggleSensor = new javax.swing.JButton();
        guiDescription = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        guiButtonRemoveSensor = new javax.swing.JButton();

        guiNameLabel.setText("jLabel13");

        guiTypeLabel.setText("jLabel13");

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText("Auto-toggle:");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel8.setText("REG Probability:");

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText("Physical sensor key:");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("Type:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Sensor class:");

        guiSensorClassLabel.setText("jLabel13");

        guiAdminStatusLabel.setText("jLabel13");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("Administrative status:");

        guiAutoToggleLabel.setText("jLabel13");

        guiRegProbabilityLabel.setText("jLabel13");

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("Name:");

        guiPssKeyLabel.setText("jLabel13");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiAutoToggleLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiPssKeyLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiRegProbabilityLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiAdminStatusLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiSensorClassLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiNameLabel))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiTypeLabel)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(guiNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(guiTypeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(guiSensorClassLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(guiPssKeyLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(guiRegProbabilityLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(guiAdminStatusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(guiAutoToggleLabel))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        guiLastModifyTimeLabel.setText("jLabel13");

        guiCreateTimeLabel.setText("jLabel13");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Operational status:");

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel13.setText("Current value:");

        guiCurrentValue.setText("jLabel13");

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel10.setText("Last tampered:");

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel9.setText("Last triggered:");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel12.setText("Created:");

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel11.setText("Last modified:");

        guiLastTriggerTimeLabel.setText("jLabel13");

        guiLastTamperTimeLabel.setText("jLabel13");

        guiOperationalStatusLabel.setText("jLabel13");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiOperationalStatusLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiLastModifyTimeLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiCreateTimeLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiLastTriggerTimeLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiLastTamperTimeLabel))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiCurrentValue)))
                .addContainerGap(376, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(guiCurrentValue))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(guiCreateTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(guiLastModifyTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(guiLastTriggerTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(guiLastTamperTimeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(guiOperationalStatusLabel))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        guiButtonUserAlarm.setText("Force Emulate Alarm");
        guiButtonUserAlarm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiButtonUserAlarmActionPerformed(evt);
            }
        });

        guiButtonToggleSensor.setText("Enable Sensor");
        guiButtonToggleSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiButtonToggleSensorActionPerformed(evt);
            }
        });

        guiDescription.setText("jLabel13");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel14.setText("Description:");

        guiButtonRemoveSensor.setText("Remove Sensor");
        guiButtonRemoveSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiButtonRemoveSensorActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(29, 29, 29)
                                .add(guiDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(guiButtonUserAlarm)
                                    .add(jLabel14)
                                    .add(guiButtonToggleSensor)
                                    .add(guiButtonRemoveSensor))
                                .add(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel14)
                .add(3, 3, 3)
                .add(guiDescription)
                .add(15, 15, 15)
                .add(guiButtonUserAlarm)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiButtonToggleSensor)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiButtonRemoveSensor)
                .add(0, 84, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Detail & Status", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void guiButtonToggleSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiButtonToggleSensorActionPerformed
        this.guiButtonUserAlarm.setEnabled(false);
        this.guiButtonToggleSensor.setEnabled(false);
        try {
            this.mainFrame.getManagementClient().toggleSensor(currentSensor.getDatabaseRecord().getId());
        } catch (ManagementClientException ex) {
            this.mainFrame.handleException(ex);
        }
    }//GEN-LAST:event_guiButtonToggleSensorActionPerformed

    private void guiButtonUserAlarmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiButtonUserAlarmActionPerformed
        this.guiButtonUserAlarm.setEnabled(false);
        this.guiButtonToggleSensor.setEnabled(false);
        try {
            this.mainFrame.getManagementClient().triggerSensorAlarm(currentSensor.getDatabaseRecord().getId());
        } catch (ManagementClientException ex) {
            this.mainFrame.handleException(ex);
        }
    }//GEN-LAST:event_guiButtonUserAlarmActionPerformed

    private void guiButtonRemoveSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiButtonRemoveSensorActionPerformed
        if (JOptionPane.showConfirmDialog(this.mainFrame, "Are you sure you want to remove sensor?", "Sensor removal confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                this.mainFrame.getManagementClient().removeSensor(currentSensor.getDatabaseRecord().getId());
                this.mainFrame.setMainPanel(this.mainFrame.getPanelMap().get(SensorListPanel.class), null);
                this.mainFrame.redrawMenu();
            } catch (ManagementClientException ex) {
                this.mainFrame.handleException(ex);
            }
        }
    }//GEN-LAST:event_guiButtonRemoveSensorActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel guiAdminStatusLabel;
    private javax.swing.JLabel guiAutoToggleLabel;
    private javax.swing.JButton guiButtonRemoveSensor;
    private javax.swing.JButton guiButtonToggleSensor;
    private javax.swing.JButton guiButtonUserAlarm;
    private javax.swing.JLabel guiCreateTimeLabel;
    private javax.swing.JLabel guiCurrentValue;
    private javax.swing.JLabel guiDescription;
    private javax.swing.JLabel guiLastModifyTimeLabel;
    private javax.swing.JLabel guiLastTamperTimeLabel;
    private javax.swing.JLabel guiLastTriggerTimeLabel;
    private javax.swing.JLabel guiNameLabel;
    private javax.swing.JLabel guiOperationalStatusLabel;
    private javax.swing.JLabel guiPssKeyLabel;
    private javax.swing.JLabel guiRegProbabilityLabel;
    private javax.swing.JLabel guiSensorClassLabel;
    private javax.swing.JLabel guiTypeLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
