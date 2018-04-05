package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IDivisionTree extends ITree<Division> implements TreeModelListener {

    private final ImageIcon categoryIcon = imageResource.readIcon("Items.Tree.Category");
    private final ImageIcon productIcon = imageResource.readIcon("Items.Tree.Product");
    private final ImageIcon typeIcon = imageResource.readIcon("Items.Tree.Type");
    private final ImageIcon itemIcon = imageResource.readIcon("Items.Tree.Item");

    public IDivisionTree(Division rootDivision, boolean showRoot) {
        super(rootDivision, showRoot, false);
        setRenderer();
    }

    @Override
    protected DefaultTreeModel createModel(Division rootDivision) {
        rootNode = new DefaultMutableTreeNode(rootDivision);

        for (Division subDivision : rootDivision.getSubDivisions()) {
            rootNode.add(createNode(subDivision));
        }

        return new DefaultTreeModel(rootNode);
    }

    private DefaultMutableTreeNode createNode(Division division) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(division);
        for (Division subDivision : division.getSubDivisions()) {
            node.add(createNode(subDivision));
        }
        return node;
    }

    public void removeDivision(Division division) {
        super.removeItem(division.getParentDivision(), division);
    }

    private void setRenderer() {
        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    Division division = (Division) ((DefaultMutableTreeNode) value).getUserObject();
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

    //
    // TreeModelListener
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        System.out.println("IDivsionTree: treeNodesChanged");
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        System.out.println("IDivsionTree: treeNodesInserted");
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        System.out.println("IDivsionTree: treeNodesRemoved");
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        System.out.println("IDivsionTree: treeStructureChanged");
    }
}
