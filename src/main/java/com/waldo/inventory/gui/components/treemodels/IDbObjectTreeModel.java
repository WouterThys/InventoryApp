package com.waldo.inventory.gui.components.treemodels;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.util.Enumeration;

import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IDbObjectTreeModel<E extends DbObject> extends DefaultTreeModel {

    public interface ParentListener<E> {
        DefaultMutableTreeNode onFindParent(DefaultMutableTreeNode rootNode, E child);
    }

    private ParentListener<E> parentListener;

    private DefaultMutableTreeNode rootNode;
    private ITree tree;

    public IDbObjectTreeModel(DefaultMutableTreeNode rootNode, ParentListener<E> parentListener) {
        super(rootNode, true);
        this.parentListener = parentListener;
        this.rootNode = rootNode;

        addTreeModelListener(new DbObjectTreeListener());
    }

    public void setTree(ITree tree) {
        this.tree = tree;
    }

    public void clear() {
        rootNode.removeAllChildren();
        reload();
    }

    public void expandNodes() {
        expandNodes(0, tree.getRowCount());
    }

    public void collapseNodes()  {
        int row = tree.getRowCount()-1;
        while (row >= 1) {
            tree.collapseRow(row);
            row--;
        }
    }

    public void expandNodes(int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandNodes(rowCount, tree.getRowCount());
        }
    }

    public void setSelectedObject(E object) {
        if (object != null) {
            DefaultMutableTreeNode node = findNode(object);
            if (node != null) {
                TreeNode[] nodes = getPathToRoot(node);
                TreePath path = new TreePath(nodes);
                tree.setExpandsSelectedPaths(true);
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
            }
        } else {
            tree.clearSelection();
        }
    }

    public void setSelectedNode(TreeNode node) {
        if (node != null) {
            TreeNode[] nodes = getPathToRoot(node);
            TreePath path = new TreePath(nodes);
            tree.setExpandsSelectedPaths(true);
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        } else  {
            tree.clearSelection();
        }
    }

    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());

            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                removeNodeFromParent(currentNode);
            }
        }
    }

    public void removeObject(E child) {
        try {
            SwingUtilities.invokeLater(() -> {
                tree.clearSelection();
                DefaultMutableTreeNode childNode = findNode(child);
                if (childNode != null) {
                    removeNodeFromParent(childNode);
                }
                reload();
                expandNodes(0, tree.getRowCount());
            });
        } catch (Exception e) {
            Status().setError("Error removing object " + child.getName(), e);
        }
    }

    public void updateObject(E newChild) {
        try {
            SwingUtilities.invokeLater(() -> {
                // TODO this was done with oldChild
                DefaultMutableTreeNode node = findNode(newChild);
                if (node != null) {
                    node.setUserObject(newChild);
                    nodeChanged(node);
                }
                reload();
                expandNodes(0, tree.getRowCount());
            });
        } catch (Exception e) {
            Status().setError("Error updating object " + newChild.getName(), e);
        }
    }

    public void addObject(E child) {
        try {
            SwingUtilities.invokeLater(() -> {
                addDbObject(findParent(child), child);
                expandNodes(0, tree.getRowCount());
                setSelectedObject(child);
            });
        } catch (Exception e) {
            Status().setError("Error adding object " + child.getName(), e);
        }
    }

    private void addDbObject(DefaultMutableTreeNode parent, E child) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

//        if (type == TYPE_ORDERS) {
//            childNode.setAllowsChildren(false);
//        } TODO check if this can be in comments..

        if (parent == null) {
            parent = rootNode;
        }

        try {
            insertNodeInto(childNode, parent, parent.getChildCount());
        } catch(Exception e){
            Status().setError("Error adding object. ", e);
        }

    }

    private DefaultMutableTreeNode findParent(E child) {
        if (parentListener != null) {
            return parentListener.onFindParent(rootNode, child);
        }
        return null;
    }


    public DefaultMutableTreeNode findNode(E object) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = rootNode.depthFirstEnumeration();

        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            DbObject nodeObject = (DbObject) node.getUserObject();
            if (nodeObject != null) {
                if (object.getId() == nodeObject.getId() && object.getName().equals(nodeObject.getName())){
                    return node;
                }
            }
        }
        return null;
    }



    class DbObjectTreeListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            int ndx = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(ndx));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            System.out.print("Tree nodes inserted: " + e);
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.print("Tree nodes removed: " + e);
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.print("Tree structure changed: " + e);
        }
    }
}
