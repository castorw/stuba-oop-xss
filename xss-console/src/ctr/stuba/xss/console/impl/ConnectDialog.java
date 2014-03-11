package ctr.stuba.xss.console.impl;

import ctr.stuba.xss.comm.mp.ManagementProtocolConstants;
import ctr.stuba.xss.console.comm.LoginFailedException;
import ctr.stuba.xss.console.comm.ManagementClient;
import ctr.stuba.xss.console.comm.ManagementClientException;
import javax.swing.JOptionPane;

/**
 * Dialog used to give user ability to fill in log credentials for remote
 * server. Including server address, username, password. This class attempts to
 * connect to remote server. Handles any exceptions.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class ConnectDialog extends javax.swing.JDialog {

    protected ManagementClient mClient;

    public ConnectDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        customInitComponents();
    }

    private void customInitComponents() {
        this.mClient = ((MainFrame) this.getParent()).getManagementClient();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiConnectPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        guiHostnameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        guiUsernameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        guiPasswordField = new javax.swing.JPasswordField();
        guiInfoLabel = new javax.swing.JLabel();
        guiConnectButton = new javax.swing.JButton();
        guiCancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connect");
        setResizable(false);

        guiConnectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("XSS Server connection"));
        guiConnectPanel.setMaximumSize(new java.awt.Dimension(469, 236));
        guiConnectPanel.setNextFocusableComponent(guiPasswordField);

        jLabel1.setText("Hostname/IP Address:");

        guiHostnameField.setText("xss.stuba.ctrdn.net");
        guiHostnameField.setToolTipText("");

        jLabel2.setText("Username:");

        guiUsernameField.setText("castor");

        jLabel3.setText("Password:");

        guiInfoLabel.setText("<html>\n<p>\nPlease provide required connection information (server hostname, username, password) to connect to remote XSS server and manage it.\n</p>\n</html>");

        guiConnectButton.setText("Connect");
        guiConnectButton.setSelected(true);
        guiConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiConnectButtonActionPerformed(evt);
            }
        });

        guiCancelButton.setText("Cancel");
        guiCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiCancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout guiConnectPanelLayout = new org.jdesktop.layout.GroupLayout(guiConnectPanel);
        guiConnectPanel.setLayout(guiConnectPanelLayout);
        guiConnectPanelLayout.setHorizontalGroup(
            guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(guiConnectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(guiConnectPanelLayout.createSequentialGroup()
                        .add(guiInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 437, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 14, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, guiConnectPanelLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(guiHostnameField)
                                .add(guiPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(guiUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .add(guiConnectPanelLayout.createSequentialGroup()
                .add(145, 145, 145)
                .add(guiConnectButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiCancelButton)
                .addContainerGap())
        );
        guiConnectPanelLayout.setVerticalGroup(
            guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, guiConnectPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(guiInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(guiHostnameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(guiUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(guiPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guiConnectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(guiConnectButton)
                    .add(guiCancelButton))
                .add(26, 26, 26))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(guiConnectPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(guiConnectPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guiCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiCancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_guiCancelButtonActionPerformed

    private void guiConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiConnectButtonActionPerformed
        try {
            String serverHostname;
            int serverPort;
            String hostFieldSplit[] = guiHostnameField.getText().trim().split(":");
            if (hostFieldSplit.length > 1) {
                serverHostname = hostFieldSplit[0];
                serverPort = new Integer(hostFieldSplit[1]);
            } else {
                serverHostname = guiHostnameField.getText().trim();
                serverPort = ManagementProtocolConstants.DEFAULT_PORT;
            }
            this.mClient.connect(serverHostname, serverPort);
            this.mClient.login(this.guiUsernameField.getText(), new String(this.guiPasswordField.getPassword()));
            this.setVisible(false);
            ((MainFrame) this.getParent()).setConnected();
        } catch (LoginFailedException ex) {
            JOptionPane.showMessageDialog(this, "Login failed for the following reason: " + ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        } catch (ManagementClientException ex) {
            JOptionPane.showMessageDialog(this, ex, "Management Client Exception", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_guiConnectButtonActionPerformed

    public void showDialog() {
        this.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiCancelButton;
    private javax.swing.JButton guiConnectButton;
    private javax.swing.JPanel guiConnectPanel;
    private javax.swing.JTextField guiHostnameField;
    private javax.swing.JLabel guiInfoLabel;
    private javax.swing.JPasswordField guiPasswordField;
    private javax.swing.JTextField guiUsernameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
