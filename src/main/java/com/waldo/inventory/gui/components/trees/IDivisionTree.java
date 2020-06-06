package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.Division;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static com.waldo.inventory.gui.Application.imageResource;

public class IDivisionTree extends ITree<Division> {

    private final ImageIcon categoryIcon = imageResource.readIcon("Component.Green.SS");
    private final ImageIcon productIcon = imageResource.readIcon("Component.Yellow.SS");
    private final ImageIcon typeIcon = imageResource.readIcon("Component.Red.SS");
    private final ImageIcon itemIcon = imageResource.readIcon("Component.SS");

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

    public Division getRootDivision() {
        return root;
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
}
