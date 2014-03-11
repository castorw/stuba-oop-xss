package ctr.stuba.xss.console.impl;

import ctr.stuba.xss.console.panel.DisconnectedPanel;
import ctr.stuba.xss.console.panel.IdentityPanel;
import ctr.stuba.xss.console.panel.SensorListPanel;
import ctr.stuba.xss.console.panel.SensorPanel;
import ctr.stuba.xss.console.panel.SystemStatusPanel;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer for main menu. Gives system ability to change default icons inside
 * tree menu.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class MainMenuTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        MainMenuTreeTargetingObject target = (MainMenuTreeTargetingObject) node.getUserObject();
        this.setText(target.getLabel());
        if (target.getTargetPanel() != null) {
            if (target.getTargetPanel().getClass() == DisconnectedPanel.class) {
                this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/block24.png")));
                this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/block24.png")));
            } else if (target.getTargetPanel().getClass() == IdentityPanel.class) {
                this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/network24.png")));
                this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/network24.png")));
            } else if (target.getTargetPanel().getClass() == SystemStatusPanel.class) {
                this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/info24.png")));
                this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/info24.png")));
            } else if (target.getTargetPanel().getClass() == SensorPanel.class) {
                this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/photo_camera24.png")));
                this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/photo_camera24.png")));
            } else if (target.getTargetPanel().getClass() == SensorListPanel.class) {
                this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/address_book24.png")));
                this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/address_book24.png")));
            }
        } else {
            switch (target.getTargetObejctId()) {
                case 1: {
                    this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/address_book24.png")));
                    this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/address_book24.png")));
                    break;
                }
                default: {
                    this.setIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/record24.png")));
                    this.setLeafIcon(new ImageIcon(getClass().getResource("/ctr/stuba/xss/console/resource/record24.png")));
                    break;
                }
            }

        }
        return this;
    }
}
