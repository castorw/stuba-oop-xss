package ctr.stuba.xss.console.impl;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import ctr.stuba.xss.comm.mp.ManagementConnectionState;
import ctr.stuba.xss.comm.mp.payload.SensorListPayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoPayload;
import ctr.stuba.xss.comm.mp.payload.SystemRuntimeInfoUpdatePayload;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.console.comm.ManagementClient;
import ctr.stuba.xss.console.comm.ManagementClientException;
import ctr.stuba.xss.console.panel.DefaultPanel;
import ctr.stuba.xss.console.panel.DisconnectedPanel;
import ctr.stuba.xss.console.panel.IdentityPanel;
import ctr.stuba.xss.console.panel.SensorListPanel;
import ctr.stuba.xss.console.panel.SensorPanel;
import ctr.stuba.xss.console.panel.SystemStatusPanel;
import ctr.stuba.xss.sensor.SensorData;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Main application GUI container. Contains internal undocumented functions.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class MainFrame extends javax.swing.JFrame {

    public SystemRuntimeInfoUpdatePayload getPayloadSystemRuntimeUpdateInfo() {
        return payloadSystemRuntimeUpdateInfo;
    }

    public void setPayloadSystemRuntimeUpdateInfo(SystemRuntimeInfoUpdatePayload payloadSystemRuntimeUpdateInfo) {
        this.payloadSystemRuntimeUpdateInfo = payloadSystemRuntimeUpdateInfo;
    }

    public SensorListPayload getPayloadSensorList() {
        return payloadSensorList;
    }

    public void setPayloadSensorList(SensorListPayload payloadSensorList) {
        this.payloadSensorList = payloadSensorList;
    }

    public Map<Class, DefaultPanel> getPanelMap() {
        return panelMap;
    }

    protected class SystemRuntimeInfoUpdater extends ThreadedWorker {

        protected final MainFrame mainFrame;

        public SystemRuntimeInfoUpdater(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        if (this.mainFrame.getManagementClient().getState() == ManagementConnectionState.CONNECTED) {
                            this.mainFrame.setPayloadSystemRuntimeUpdateInfo(this.mainFrame.getManagementClient().getSystemRuntimeInfoUpdate());
                            this.mainFrame.setPayloadSensorList(this.mainFrame.getManagementClient().getSensorList());
                            this.mainFrame.updateSystemStatusPanel();
                            this.mainFrame.updateSensorPanel();
                        }
                    } catch (ManagementClientException ex) {
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException ex) {
            }
        }
    }
    protected ManagementClient mClient;
    protected ConnectDialog connectDialog;
    protected SystemRuntimeInfoPayload payloadSystemRuntimeInfo;
    protected SensorListPayload payloadSensorList;
    protected SystemRuntimeInfoUpdatePayload payloadSystemRuntimeUpdateInfo;
    protected SystemRuntimeInfoUpdater systemRuntimeInfoUpdater;
    private Map<Class, DefaultPanel> panelMap = new HashMap<>();
    DefaultMutableTreeNode nodeSensorList;

    /**
     * Creates instance of main GUI frame.
     */
    public MainFrame() {
        this.mClient = new ManagementClient(this);
        this.connectDialog = new ConnectDialog(this, true);
        this.initComponents();
        this.customInitComponents();
        this.initPanels();
        this.systemRuntimeInfoUpdater = new SystemRuntimeInfoUpdater(this);
    }

    public ManagementClient getManagementClient() {
        return this.mClient;
    }

    /**
     * Handle system exception.
     *
     * @param ex
     */
    public void handleException(Exception ex) {
        JOptionPane.showMessageDialog(this, ex, "Exception", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Finds sensor in list by specified ID.
     *
     * @param id
     * @return
     */
    public SensorData getSensorDataByDatabaseId(int id) {
        for (SensorData sdata : this.getPayloadSensorList().getSensorList()) {
            if (sdata.getDatabaseRecord().getId() == id) {
                return sdata;
            }
        }
        return null;
    }

    protected void reloadSystemRuntimeInfo() throws DataReloadFailedException {
        try {
            if (this.mClient.getState() == ManagementConnectionState.CONNECTED) {
                this.payloadSystemRuntimeInfo = this.mClient.getSystemRuntimeInfo();
            } else {
                throw new DataReloadFailedException("Client is not connected");
            }
        } catch (ManagementClientException ex) {
            DataReloadFailedException ex2 = new DataReloadFailedException("Failed to reload data");
            ex2.addSuppressed(ex);
            throw ex2;
        }
    }

    /**
     * Sets GUI to connected state.
     */
    public void setConnected() {
        try {
            this.guiMainMenuTree.setEnabled(true);
            this.reloadSystemRuntimeInfo();
            this.systemRuntimeInfoUpdater.start();
            this.setTitle("XSS Console (" + this.getPayloadSystemRuntimeInfo().getServerInstanceName() + " @ " + this.getPayloadSystemRuntimeInfo().getServerHostname() + ")");
            this.guiConnectButton.setEnabled(false);
            this.guiDisconnectButton.setEnabled(true);

            this.redrawMenu();
        } catch (DataReloadFailedException ex) {
            this.handleException(ex);
        }
    }

    /**
     * Sets GUI to disconnected state.
     */
    public void setDisconnected() {
        if (this.systemRuntimeInfoUpdater.isRunning()) {
            this.systemRuntimeInfoUpdater.interrupt();
        }
        this.guiMainMenuTree.setEnabled(false);
        this.setTitle("XSS Console (not connected)");

        DefaultMutableTreeNode disconnectedRootNode = new DefaultMutableTreeNode();
        disconnectedRootNode.setUserObject(new MainMenuTreeTargetingObject("(not connected)", this.getPanelMap().get(DisconnectedPanel.class), 0));
        DefaultTreeModel disconnectedTreeModel = new DefaultTreeModel(disconnectedRootNode);
        this.guiMainMenuTree.setModel(disconnectedTreeModel);
        this.guiMainMenuTree.setCellRenderer(new MainMenuTreeCellRenderer());
        this.setMainPanel(this.getPanelMap().get(DisconnectedPanel.class), null);
        this.guiConnectButton.setEnabled(true);
        this.guiDisconnectButton.setEnabled(false);
    }

    public void redrawMenu() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject(this.payloadSystemRuntimeInfo.getServerHostname(), this.getPanelMap().get(IdentityPanel.class), 0));
        DefaultMutableTreeNode nodeSystemInformation = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject("System Status", this.getPanelMap().get(SystemStatusPanel.class), 0));
        rootNode.add(nodeSystemInformation);

        nodeSensorList = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject("Sensors", this.getPanelMap().get(SensorListPanel.class), 0));

        rootNode.add(nodeSensorList);

        /*
         DefaultMutableTreeNode nodeSystemConfiguration = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject("System Configuration", null, 0));
         DefaultMutableTreeNode nodeConfigEditor = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject("Configuration Editor", this.panelMap.get(DisconnectedPanel.class), 0));
         nodeSystemConfiguration.add(nodeConfigEditor);
         DefaultMutableTreeNode nodeUserManager = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject("User Manager", this.panelMap.get(DisconnectedPanel.class), 0));
         nodeSystemConfiguration.add(nodeUserManager);

         rootNode.add(nodeSystemConfiguration);
         */

        this.guiMainMenuTree.setModel(new DefaultTreeModel(rootNode));
        this.guiMainMenuTree.setSelectionPath(new TreePath(rootNode));
    }

    /**
     * Changes main panel.
     *
     * @param panel Panel to show
     * @param target Targeting object
     */
    public void setMainPanel(DefaultPanel panel, MainMenuTreeTargetingObject target) {
        this.guiSplitPane.getRightComponent().setVisible(false);
        panel.configurePanel(target);
        panel.setVisible(true);
        this.guiSplitPane.setRightComponent(panel);
    }

    private void initPanels() {
        this.getPanelMap().put(DisconnectedPanel.class, new DisconnectedPanel());
        this.getPanelMap().put(IdentityPanel.class, new IdentityPanel(this));
        this.getPanelMap().put(SystemStatusPanel.class, new SystemStatusPanel(this));
        this.getPanelMap().put(SensorPanel.class, new SensorPanel(this));
        this.getPanelMap().put(SensorListPanel.class, new SensorListPanel(this));
    }

    private void customInitComponents() {
    }

    public void updateSystemStatusPanel() {
        ((SystemStatusPanel) this.getPanelMap().get(SystemStatusPanel.class)).updateRuntimeData();
    }

    public void updateSensorPanel() {
        nodeSensorList.removeAllChildren();
        for (SensorData sensor : this.getPayloadSensorList().getSensorList()) {
            DefaultMutableTreeNode sensorTreeNode = new DefaultMutableTreeNode(new MainMenuTreeTargetingObject(sensor.getDatabaseRecord().getName(), this.getPanelMap().get(SensorPanel.class), sensor.getDatabaseRecord().getId()));
            nodeSensorList.add(sensorTreeNode);
        }

        this.guiMainMenuTree.revalidate();
        ((SensorPanel) this.getPanelMap().get(SensorPanel.class)).updateData();
    }

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new AcrylLookAndFeel());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setDisconnected();
                frame.setVisible(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiSplitPane = new javax.swing.JSplitPane();
        guiMainMenuScrollPane = new javax.swing.JScrollPane();
        guiMainMenuTree = new javax.swing.JTree();
        guiTopMenuToolBar = new javax.swing.JToolBar();
        guiConnectButton = new javax.swing.JButton();
        guiDisconnectButton = new javax.swing.JButton();
        guiTopMenuSeparator = new javax.swing.JToolBar.Separator();
        guiExitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("XSS Console (not connected)");
        setLocation(new java.awt.Point(100, 100));
        setLocationByPlatform(true);

        guiMainMenuScrollPane.setMinimumSize(new java.awt.Dimension(250, 500));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        guiMainMenuTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        guiMainMenuTree.setMaximumSize(new java.awt.Dimension(0, 0));
        guiMainMenuTree.setMinimumSize(new java.awt.Dimension(250, 500));
        guiMainMenuTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                guiMainMenuTreeValueChanged(evt);
            }
        });
        guiMainMenuScrollPane.setViewportView(guiMainMenuTree);

        guiSplitPane.setLeftComponent(guiMainMenuScrollPane);

        guiTopMenuToolBar.setFloatable(false);
        guiTopMenuToolBar.setRollover(true);

        guiConnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/link24.png"))); // NOI18N
        guiConnectButton.setText("Connect");
        guiConnectButton.setFocusable(false);
        guiConnectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guiConnectButton.setMinimumSize(new java.awt.Dimension(100, 46));
        guiConnectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guiConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiConnectButtonActionPerformed(evt);
            }
        });
        guiTopMenuToolBar.add(guiConnectButton);

        guiDisconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/shut_down24.png"))); // NOI18N
        guiDisconnectButton.setText("Disconnect");
        guiDisconnectButton.setEnabled(false);
        guiDisconnectButton.setFocusable(false);
        guiDisconnectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guiDisconnectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guiDisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiDisconnectButtonActionPerformed(evt);
            }
        });
        guiTopMenuToolBar.add(guiDisconnectButton);
        guiTopMenuToolBar.add(guiTopMenuSeparator);

        guiExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/delete24.png"))); // NOI18N
        guiExitButton.setText("Exit console");
        guiExitButton.setFocusable(false);
        guiExitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guiExitButton.setMinimumSize(new java.awt.Dimension(100, 46));
        guiExitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guiExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiExitButtonActionPerformed(evt);
            }
        });
        guiTopMenuToolBar.add(guiExitButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(guiTopMenuToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, guiSplitPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(guiTopMenuToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiSplitPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 455, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guiExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiExitButtonActionPerformed
        if (this.mClient.getState() == ManagementConnectionState.CONNECTED) {
            if (JOptionPane.showConfirmDialog(this, "You are connected to server. Terminating the console will disconnect you from server. Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_guiExitButtonActionPerformed

    private void guiConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiConnectButtonActionPerformed
        this.connectDialog.setLocationRelativeTo(this);
        this.connectDialog.showDialog();
    }//GEN-LAST:event_guiConnectButtonActionPerformed
    private void guiMainMenuTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_guiMainMenuTreeValueChanged
        if (!this.guiMainMenuTree.isSelectionEmpty()) {
            TreePath path = guiMainMenuTree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            MainMenuTreeTargetingObject target = (MainMenuTreeTargetingObject) node.getUserObject();
            if (target != null && target.getTargetPanel() != null) {
                this.setMainPanel(target.getTargetPanel(), target);
            }
        }
    }//GEN-LAST:event_guiMainMenuTreeValueChanged

    private void guiDisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiDisconnectButtonActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to disconnect from " + this.payloadSystemRuntimeInfo.getServerHostname() + "?", "Disconnect from server", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.mClient.breakConnection();
        }
    }//GEN-LAST:event_guiDisconnectButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiConnectButton;
    private javax.swing.JButton guiDisconnectButton;
    private javax.swing.JButton guiExitButton;
    private javax.swing.JScrollPane guiMainMenuScrollPane;
    private javax.swing.JTree guiMainMenuTree;
    private javax.swing.JSplitPane guiSplitPane;
    private javax.swing.JToolBar.Separator guiTopMenuSeparator;
    private javax.swing.JToolBar guiTopMenuToolBar;
    // End of variables declaration//GEN-END:variables

    public SystemRuntimeInfoPayload getPayloadSystemRuntimeInfo() {
        return this.payloadSystemRuntimeInfo;
    }
}
