package ctr.stuba.xss.console.panel;

/**
 * Default offline informational panel.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class DisconnectedPanel extends DefaultPanel {

    public DisconnectedPanel() {
        this.initComponents();
    }

    @Override
    protected void initializeData() {
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Not connected"));
        setToolTipText("");

        jLabel2.setText("<html>\n<strong>You are not connected to XSS server.</strong><br/>\n<p>Connect to server to get access to:</p>\n<ul>\n\t<li>event viewer</li>\n\t<li>system configuration</li>\n\t<li>sensor configuration</li>\n\t<li>sensor event simulation</li>\n\t<li>etc...</li>\n</ul>\n</html>");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 275, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 146, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
