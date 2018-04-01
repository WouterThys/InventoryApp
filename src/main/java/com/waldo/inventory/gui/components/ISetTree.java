package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.treemodels.ISetTreeModel;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Enumeration;

import static com.waldo.inventory.gui.Application.imageResource;

public class ISetTree extends ITree {

    private final ImageIcon setIcon = imageResource.readImage("Items.Tree.Set");

    public ISetTree(Set rootSet, boolean showRoot) {
        super(new ISetTreeModel(rootSet));

        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(setIcon);
        renderer.setClosedIcon(setIcon);
        renderer.setOpenIcon(setIcon);
        setCellRenderer(renderer);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public Set getRootSet() {
        return (Set) getModel().getRoot();
    }

    public void setChanged(Set changedSet) {
        if (changedSet == null) {
            changedSet = (Set) getModel().getRoot();
        }
        ((ISetTreeModel) getModel()).fireTreeStructureChanged(changedSet);
    }

    public Set getSelectedSet() {
        Set selected = null;
        TreePath path = getSelectionModel().getSelectionPath();
        if (path != null) {
            selected = (Set) path.getLastPathComponent();
        }
        return selected;
    }

    public void setSelectedSet(Set set) {
        if (set != null) {
            DefaultMutableTreeNode node = findNode(set);
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

    private DefaultMutableTreeNode findNode(Set set) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(getModel().getRoot());
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Set nodeObject = (Set) node.getUserObject();
            if (nodeObject != null) {
                if (nodeObject.equals(set)) {
                    return node;
                }
            }
        }
        return null;
    }
}
