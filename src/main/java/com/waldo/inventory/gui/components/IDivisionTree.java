package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.Division;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Enumeration;

import static com.waldo.inventory.gui.Application.imageResource;

public class IDivisionTree extends ITree implements TreeModelListener {

    private final ImageIcon categoryIcon =imageResource.readImage("Items.Tree.Category");
    private final ImageIcon productIcon = imageResource.readImage("Items.Tree.Product");
    private final ImageIcon typeIcon = imageResource.readImage("Items.Tree.Type");
    private final ImageIcon itemIcon = imageResource.readImage("Items.Tree.Item");

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public IDivisionTree(Division rootDivision, boolean showRoot) {
        super();

        treeModel = createModel(rootDivision);
        setModel(treeModel);

        setRenderer();
        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public IDivisionTree(DefaultTreeModel treeModel, boolean showRoot) {
        super(treeModel);
        this.treeModel = treeModel;

        setRenderer();
        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private DefaultTreeModel createModel(Division rootDivision) {
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

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public Division getRootDivision() {
        return (Division) rootNode.getUserObject();
    }

    public Division getSelectedDivision() {
        Division selected = null;
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null) {
            selected = (Division) selectedNode.getUserObject();
        }
        return selected;
    }

    public DefaultMutableTreeNode getSelectedNode() {
        DefaultMutableTreeNode selected = null;
        TreePath path = getSelectionModel().getSelectionPath();
        if (path != null) {
            selected = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        return selected;
    }

    public void setSelectedDivision(Division division) {
        if (division != null) {
            DefaultMutableTreeNode node = findNodeFromDivision(division);
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

    public void updateTree() {
        treeModel.reload();
    }

    public void updateDivision(Division division) {
        if (division != null) {
            DefaultMutableTreeNode node = findNodeFromDivision(division);
            if (node != null) {
                treeModel.nodeChanged(node);
            }
        }
    }

    public void addDivision(Division division) {
        if (division != null) {
            DefaultMutableTreeNode parentNode = getSelectedNode();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(division);

            if (parentNode == null) {
                parentNode = rootNode;
            }

            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void removeDivision(Division division) {
        if (division != null) {
            MutableTreeNode divisionNode = findNodeFromDivision(division);
            MutableTreeNode parentNode = findNodeFromDivision(division.getParentDivision());
            if (parentNode != null && divisionNode != null) {
                treeModel.removeNodeFromParent(divisionNode);
            }
        }
    }

    private DefaultMutableTreeNode findNodeFromDivision(Division division) {
        Enumeration e = rootNode.depthFirstEnumeration();
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
