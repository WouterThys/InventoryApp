package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.treemodels.IDivisionTreeModel;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Enumeration;

import static com.waldo.inventory.gui.Application.imageResource;

public class IDivisionTree extends ITree {

    private final ImageIcon categoryIcon =imageResource.readImage("Items.Tree.Category");
    private final ImageIcon productIcon = imageResource.readImage("Items.Tree.Product");
    private final ImageIcon typeIcon = imageResource.readImage("Items.Tree.Type");
    private final ImageIcon itemIcon = imageResource.readImage("Items.Tree.Item");

    public IDivisionTree(Division rootDivision, boolean showRoot) {
        super(new IDivisionTreeModel(rootDivision));

        setRenderer();
        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public Division getSelectedDivision() {
        Division selected = null;
        TreePath path = getSelectionModel().getSelectionPath();
        if (path != null) {
            selected = (Division) path.getLastPathComponent();
        }
        return selected;
    }

    public void setSelectedSet(Division division) {
        if (division != null) {
            DefaultMutableTreeNode node = findNode(division);
            if (node != null) {
                TreeNode[] nodes = ((DefaultTreeModel)getModel()).getPathToRoot(node);
                TreePath path = new TreePath(nodes);

                setSelectionPath(path);
                scrollPathToVisible(path);
            }
        } else {
            clearSelection();
        }
    }

    private DefaultMutableTreeNode findNode(Division division) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(getModel().getRoot());
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Division nodeObject = (Division) node.getUserObject();
            if (nodeObject != null) {
                if (nodeObject.equals(division)) {
                    return node;
                }
            }
        }
        return null;
    }

    private void setRenderer() {

        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof Division) {
                    Division division = (Division) value;
                    if (division.canBeSaved()) {
                        switch (division.getLevel()) {
                            case 0: setIcon(categoryIcon); break;
                            case 1: setIcon(productIcon); break;
                            case 2: setIcon(typeIcon); break;
                            default: setIcon(itemIcon); break;
                        }
                    }
                }

                return c;
            }
        });

    }
}
