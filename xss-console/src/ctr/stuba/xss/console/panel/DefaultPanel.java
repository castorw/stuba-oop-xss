package ctr.stuba.xss.console.panel;

import ctr.stuba.xss.console.impl.MainMenuTreeTargetingObject;

/**
 * Default panel implementation. Prepares base for main panels.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
abstract public class DefaultPanel extends javax.swing.JPanel {

    protected MainMenuTreeTargetingObject targetingObject = null;

    /**
     * Called when the panel is going to be shown.
     *
     * @param target
     */
    public void configurePanel(MainMenuTreeTargetingObject target) {
        this.targetingObject = target;
        this.initializeData();
    }

    /**
     * Called when the panel is going to be hidden.
     *
     * @return
     */
    public Boolean deconfigurePanel() {
        return Boolean.TRUE;
    }

    /**
     * Initialize data.
     */
    abstract protected void initializeData();

    public MainMenuTreeTargetingObject getTargetingObject() {
        return this.targetingObject;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 396, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 296, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
