package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.io.File;

import static com.waldo.inventory.gui.Application.imageResource;

public class ITree extends JTree {

    public ITree() {
        this(null);
    }

    public ITree (TreeModel treeModel) {
        super(treeModel);

        Dimension d = getPreferredSize();
        d.width = 200;
        setMinimumSize(d);
    }

    public static DefaultTreeCellRenderer getOrdersRenderer() {
        return new DefaultTreeCellRenderer() {
            private final ImageIcon receivedIcon = imageResource.readIcon("Orders.Tree.Received");
            private final ImageIcon orderedIcon = imageResource.readIcon("Orders.Tree.Ordered");
            private final ImageIcon plannedIcon = imageResource.readIcon("Orders.Tree.Planned");

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
                    //setNameTxt(());
                }

                return c;
            }
        };
    }

    public static DefaultTreeCellRenderer getProjectsRenderer() {
        return new DefaultTreeCellRenderer() {
            private final ImageIcon codeIcon = imageResource.readIcon("Projects.Tree.Code");
            private final ImageIcon pcbIcon = imageResource.readIcon("Projects.Tree.Pcb");
            private final ImageIcon otherIcon = imageResource.readIcon("Projects.Tree.Other");

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
