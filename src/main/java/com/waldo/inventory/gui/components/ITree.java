package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.DbObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITree extends JTree {

    public ITree (TreeModel treeModel) {
        super(treeModel);

        Dimension d = getPreferredSize();
        d.width = 200;
        setMinimumSize(d);
    }

    public static DefaultTreeCellRenderer getOrdersRenderer() {
        return new DefaultTreeCellRenderer() {
            private ImageIcon orderIcon = imageResource.readImage("Common.Order", 16);
            private ImageIcon receivedIcon = imageResource.readImage("OrderFlow.Received", 16);
            private ImageIcon orderedIcon = imageResource.readImage("OrderFlow.Ordered", 16);
            private ImageIcon plannedIcon = imageResource.readImage("OrderFlow.Planned", 16);

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    DbObject object = (DbObject) ((DefaultMutableTreeNode) value).getUserObject();
                    if (!object.canBeSaved()) {
                        switch (object.getName()) {
                            case "Planned":
                                setIcon(plannedIcon);
                                break;
                            case "Ordered":
                                setIcon(orderedIcon);
                                break;
                            case "Received":
                                setIcon(receivedIcon);
                                break;
                            default:
                                break;
                        }
                    } else {
                        setIcon(null);
                    }
                }

                return c;
            }
        };
    }

    public static DefaultTreeCellRenderer getFilesRenderer() {
        return new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof File) {
                    setText(((File)value).getName());
                    //setName(());
                }

                return c;
            }
        };
    }


    public static DefaultTreeCellRenderer getProjectsRenderer() {
        return new DefaultTreeCellRenderer() {
            private ImageIcon codeIcon = imageResource.readImage("Common.Code", 16);
            private ImageIcon pcbIcon = imageResource.readImage("Common.Pcb", 16);
            private ImageIcon otherIcon = imageResource.readImage("Common.Other", 16);

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    DbObject object = (DbObject) ((DefaultMutableTreeNode) value).getUserObject();
                    if (!object.canBeSaved()) {
                        switch (DbObject.getType(object)) {
                            case DbObject.TYPE_PROJECT_CODE:
                                setIcon(codeIcon);
                                break;
                            case DbObject.TYPE_PROJECT_PCB:
                                setIcon(pcbIcon);
                                break;
                            case DbObject.TYPE_PROJECT_OTHER:
                                setIcon(otherIcon);
                                break;
                            default:
                                break;
                        }
                    }
                }

                return c;
            }
        };
    }
}
