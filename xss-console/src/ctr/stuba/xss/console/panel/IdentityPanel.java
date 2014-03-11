package ctr.stuba.xss.console.panel;

import ctr.stuba.xss.console.impl.MainFrame;
import javax.swing.border.TitledBorder;

/**
 * Panel representing server identity.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class IdentityPanel extends DefaultPanel {

    protected final MainFrame mainFrame;

    public IdentityPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
    }

    @Override
    protected void initializeData() {
        ((TitledBorder) this.getBorder()).setTitle(this.mainFrame.getPayloadSystemRuntimeInfo().getServerHostname());
        this.guiInstanceNameLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerInstanceName());
        this.guiHostnameLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerHostname());
        this.guiVersionLabel.setText(this.mainFrame.getPayloadSystemRuntimeInfo().getServerVersion());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        guiInstanceNameLabel = new javax.swing.JLabel();
        guiHostnameLabel = new javax.swing.JLabel();
        guiVersionLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Not connected"));
        setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("System version:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Instance name:");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("System hostname:");

        guiInstanceNameLabel.setText("jLabel5");

        guiHostnameLabel.setText("jLabel5");

        guiVersionLabel.setText("jLabel5");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiVersionLabel))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiInstanceNameLabel))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(guiHostnameLabel)))
                .addContainerGap(269, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(guiInstanceNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(guiHostnameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(guiVersionLabel))
                .add(0, 172, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel guiHostnameLabel;
    private javax.swing.JLabel guiInstanceNameLabel;
    private javax.swing.JLabel guiVersionLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
