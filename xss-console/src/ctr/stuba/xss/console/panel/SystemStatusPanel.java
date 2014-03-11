package ctr.stuba.xss.console.panel;

import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoPayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoUpdatePayload;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.console.impl.MainFrame;
import ctr.stuba.xss.engine.EngineStatus;
import ctr.stuba.xss.hbm.XssEvent;
import java.awt.Color;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/**
 * Main system status panel. Provides uptime information, server version
 * information and latest log events. Log events are updated live.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SystemStatusPanel extends DefaultPanel {

    protected class SystemUptimeCounter extends ThreadedWorker {

        protected SystemRuntimeInfoPayload payload;
        protected JLabel targetLabel;

        public SystemUptimeCounter(SystemRuntimeInfoPayload payload, JLabel targetLabel) {
            this.payload = payload;
            this.targetLabel = targetLabel;
        }

        public void updatePayload(SystemRuntimeInfoPayload payload) {
            this.payload = payload;
        }

        @Override
        public void run() {
            while (this.workerThread == Thread.currentThread()) {
                try {
                    if (this.payload != null && this.payload.getServerStartDate() != null) {
                        long uptime = new Date().getTime() - this.payload.getServerStartDate().getTime();
                        uptime /= 1000;
                        this.targetLabel.setText(String.format("%d days %02d:%02d:%02d", uptime / 86400, (uptime % 86400) / 3600, (uptime % 3600) / 60, (uptime % 60)));
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    protected class EngineUptimeCounter extends ThreadedWorker {

        protected SystemRuntimeInfoPayload payload;
        protected JLabel targetLabel;

        public EngineUptimeCounter(SystemRuntimeInfoPayload payload, JLabel targetLabel) {
            this.payload = payload;
            this.targetLabel = targetLabel;
        }

        public void updatePayload(SystemRuntimeInfoPayload payload) {
            this.payload = payload;
        }

        @Override
        public void run() {
            while (this.workerThread == Thread.currentThread()) {
                try {
                    if (this.payload != null && this.payload.getEngineStartDate() != null) {
                        long uptime = new Date().getTime() - this.payload.getEngineStartDate().getTime();
                        uptime /= 1000;
                        this.targetLabel.setText(String.format("%d days %02d:%02d:%02d", uptime / 86400, (uptime % 86400) / 3600, (uptime % 3600) / 60, (uptime % 60)));
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    protected class StatusTimeCounter extends ThreadedWorker {

        protected SystemRuntimeInfoUpdatePayload payload;
        protected JLabel targetLabel;

        public StatusTimeCounter(SystemRuntimeInfoUpdatePayload payload, JLabel targetLabel) {
            this.payload = payload;
            this.targetLabel = targetLabel;
        }

        public void updatePayload(SystemRuntimeInfoUpdatePayload payload) {
            this.payload = payload;
        }

        @Override
        public void run() {
            while (this.workerThread == Thread.currentThread()) {
                try {
                    if (this.payload != null && this.payload.getEngineLastStatusChangeDate() != null) {
                        long time = new Date().getTime() - this.payload.getEngineLastStatusChangeDate().getTime();
                        time /= 1000;
                        this.targetLabel.setText(String.format("%d days %02d:%02d:%02d", time / 86400, (time % 86400) / 3600, (time % 3600) / 60, (time % 60)));
                    }
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
    protected final MainFrame mainFrame;
    protected SystemUptimeCounter systemUptimeCounter;
    protected EngineUptimeCounter engineUptimeCounter;
    protected StatusTimeCounter statusTimeCounter;

    public SystemStatusPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
        this.customInitComponents();
        this.systemUptimeCounter = new SystemUptimeCounter(this.mainFrame.getPayloadSystemRuntimeInfo(), this.guiSystemUptimeLabel);
        this.engineUptimeCounter = new EngineUptimeCounter(this.mainFrame.getPayloadSystemRuntimeInfo(), this.guiEngineUptimeLabel);
        this.statusTimeCounter = new StatusTimeCounter(this.mainFrame.getPayloadSystemRuntimeUpdateInfo(), this.guiEngineStatusTimeLabel);
    }

    private void customInitComponents() {
        this.guiEventTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        this.guiEventTable.getColumnModel().getColumn(0).setMaxWidth(20);
        this.guiEventTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        this.guiEventTable.getColumnModel().getColumn(1).setMaxWidth(160);
        this.guiEventTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        this.guiEventTable.getColumnModel().getColumn(2).setMaxWidth(180);
        this.guiEventTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        this.guiEventTable.getColumnModel().getColumn(3).setMaxWidth(200);
        this.guiEventTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        this.guiEventTable.getColumnModel().getColumn(4).setMaxWidth(200);
    }

    @Override
    public Boolean deconfigurePanel() {
        this.engineUptimeCounter.interrupt();
        this.systemUptimeCounter.interrupt();
        this.statusTimeCounter.interrupt();
        return Boolean.TRUE;
    }

    @Override
    protected void initializeData() {
        this.guiInstanceNameLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerInstanceName());
        this.guiSystemHostnameLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerHostname());
        this.guiVersionLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerVersion());
        this.guiSystemStartTime.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerStartDate().toString());
        this.guiEngineSensorCountLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getEngineSensorCount().toString());
        this.guiEngineStartTimeLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getEngineStartDate().toString());
        this.systemUptimeCounter.updatePayload(this.mainFrame.getPayloadSystemRuntimeInfo());
        this.systemUptimeCounter.start();
        this.engineUptimeCounter.updatePayload(this.mainFrame.getPayloadSystemRuntimeInfo());
        this.engineUptimeCounter.start();
        this.statusTimeCounter.updatePayload(this.mainFrame.getPayloadSystemRuntimeUpdateInfo());
        this.statusTimeCounter.start();
        this.updateRuntimeData();
    }

    public void updateRuntimeData() {
        if (this.mainFrame.getPayloadSystemRuntimeUpdateInfo().getEngineRunning()) {
            this.statusTimeCounter.updatePayload(this.mainFrame.getPayloadSystemRuntimeUpdateInfo());
            this.guiEngineRunningLabel.setText("Yes");
            this.guiEngineRunningLabel.setForeground(Color.GREEN);
            this.guiEngineStatusLabel.setText(this.mainFrame.getPayloadSystemRuntimeUpdateInfo().getEngineStatus().toString());
            if (this.mainFrame.getPayloadSystemRuntimeUpdateInfo().getEngineStatus() == EngineStatus.ALARM) {
                this.guiEngineStatusLabel.setForeground(Color.RED);
            } else {
                this.guiEngineStatusLabel.setForeground(Color.GREEN);
            }
        } else {
            this.guiEngineRunningLabel.setText("No");
            this.guiEngineRunningLabel.setForeground(Color.RED);
            this.guiEngineStatusLabel.setText("N/A");
            this.guiEngineStatusLabel.setForeground(Color.BLACK);
            this.guiEngineSensorCountLabel.setText("N/A");
            this.guiEngineStartTimeLabel.setText("N/A");
        }
        if (this.mainFrame.getPayloadSystemRuntimeUpdateInfo().getLastEvents() != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/record24.png"));
            DefaultTableModel tableModel = (DefaultTableModel) this.guiEventTable.getModel();
            for (XssEvent event : this.mainFrame.getPayloadSystemRuntimeUpdateInfo().getLastEvents()) {
                Object data[] = new Object[6];
                data[0] = "";
                data[1] = event.getTime().toString();

                String[] eventSplit = event.getEvent().split("\\.");
                data[2] = eventSplit[eventSplit.length - 1];

                String[] causeSplit = event.getCause().split("\\.");
                data[3] = causeSplit[causeSplit.length - 1];

                if (event.getSensorId() == null) {
                    data[4] = "-";
                } else if (this.mainFrame.getSensorDataByDatabaseId(event.getSensorId()) != null) {
                    data[4] = this.mainFrame.getSensorDataByDatabaseId(event.getSensorId()).getDatabaseRecord().getName();
                }

                data[5] = event.getMessage().toString();
                tableModel.addRow(data);
            }
            this.guiEventTable.repaint();
            this.guiEventTable.scrollRectToVisible(this.guiEventTable.getCellRect(this.guiEventTable.getRowCount() - 1, 0, true));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        guiVersionLabel = new javax.swing.JLabel();
        guiEngineRunningLabel = new javax.swing.JLabel();
        guiInstanceNameLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        guiSystemStartTime = new javax.swing.JLabel();
        guiSystemUptimeLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        guiSystemHostnameLabel = new javax.swing.JLabel();
        guiEngineStatusLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        guiEngineSensorCountLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        guiEngineUptimeLabel = new javax.swing.JLabel();
        guiEngineStartTimeLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        guiEngineStatusTimeLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        guiEventTable = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createTitledBorder("System status"));
        setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Instance name:");

        guiVersionLabel.setText("jLabel5");

        guiEngineRunningLabel.setText("jLabel5");

        guiInstanceNameLabel.setText("jLabel5");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("System hostname:");

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("System version:");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("System start time:");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("System uptime:");

        guiSystemStartTime.setText("jLabel5");

        guiSystemUptimeLabel.setText("jLabel5");

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText("Engine running:");

        guiSystemHostnameLabel.setText("jLabel5");

        guiEngineStatusLabel.setText("-");

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText("Engine status:");

        guiEngineSensorCountLabel.setText("-");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel8.setText("Engine sensor count:");

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel9.setText("Engine start time:");

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel10.setText("Engine uptime:");

        guiEngineUptimeLabel.setText("-");

        guiEngineStartTimeLabel.setText("-");

        jLabel11.setText("for");

        guiEngineStatusTimeLabel.setText("-");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel12.setText("Recent events:");

        guiEventTable.setAutoCreateRowSorter(true);
        guiEventTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Date & Time", "Event", "Cause", "Sensor", "Message"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        guiEventTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        guiEventTable.setAutoscrolls(false);
        jScrollPane1.setViewportView(guiEventTable);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiSystemHostnameLabel))
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiVersionLabel))
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiInstanceNameLabel))
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiEngineRunningLabel))
                            .add(layout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiEngineStatusLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiEngineStatusTimeLabel))
                            .add(layout.createSequentialGroup()
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiEngineSensorCountLabel)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 46, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(guiSystemUptimeLabel)
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel10)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(guiEngineUptimeLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel9)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(guiEngineStartTimeLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel2)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(guiSystemStartTime)))
                                .add(0, 40, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel12)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(guiInstanceNameLabel)
                    .add(jLabel2)
                    .add(guiSystemStartTime))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(guiSystemUptimeLabel)
                    .add(guiSystemHostnameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(guiVersionLabel))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(guiEngineRunningLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(guiEngineStatusLabel)
                            .add(jLabel11)
                            .add(guiEngineStatusTimeLabel)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(guiEngineStartTimeLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(guiEngineUptimeLabel))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(guiEngineSensorCountLabel))
                .add(18, 18, 18)
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel guiEngineRunningLabel;
    private javax.swing.JLabel guiEngineSensorCountLabel;
    private javax.swing.JLabel guiEngineStartTimeLabel;
    private javax.swing.JLabel guiEngineStatusLabel;
    private javax.swing.JLabel guiEngineStatusTimeLabel;
    private javax.swing.JLabel guiEngineUptimeLabel;
    private javax.swing.JTable guiEventTable;
    private javax.swing.JLabel guiInstanceNameLabel;
    private javax.swing.JLabel guiSystemHostnameLabel;
    private javax.swing.JLabel guiSystemStartTime;
    private javax.swing.JLabel guiSystemUptimeLabel;
    private javax.swing.JLabel guiVersionLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
