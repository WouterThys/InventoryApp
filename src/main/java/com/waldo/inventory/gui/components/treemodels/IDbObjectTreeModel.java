package com.waldo.inventory.gui.components.treemodels;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

public class IDbObjectTreeModel extends DefaultTreeModel {

    public static final int TYPE_DIVISIONS = 0;
    public static final int TYPE_ORDERS = 1;
    public static final int TYPE_PROJECTS = 2;

    private DefaultMutableTreeNode rootNode;
    private ITree tree;
    private int type;

    public IDbObjectTreeModel(DefaultMutableTreeNode rootNode, int type) {
        super(rootNode, true);
        this.rootNode = rootNode;
        this.type = type;

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

    public void expandNodes(int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandNodes(rowCount, tree.getRowCount());
        }
    }

    public void setSelectedObject(DbObject object) {
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

    public void removeObject(DbObject child) {
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

    public void updateObject(DbObject newChild) {
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

    public void addObject(DbObject child) {
        try {
            SwingUtilities.invokeLater(() -> {
                addDbObject(findParent(child), child);
                expandNodes(0, tree.getRowCount());
                setSelectedObject(child);
            });
        } catch (Exception e) {
            Status().setError("Error adding object " + child.getName(), e);
        }
        //addDbObject(findParent(child), child);
    }

    private DefaultMutableTreeNode addDbObject(DefaultMutableTreeNode parent, DbObject child) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (type == TYPE_ORDERS) {
            childNode.setAllowsChildren(false);
        }

        if (parent == null) {
            parent = rootNode;
        }

        try {
            insertNodeInto(childNode, parent, parent.getChildCount());
        } catch(Exception e){
            Status().setError("Error adding object. ", e);
        }

        return childNode;
    }

    private DefaultMutableTreeNode findParent(DbObject child) {
        if (type == TYPE_DIVISIONS) {
            switch (DbObject.getType(child)) {
                case DbObject.TYPE_CATEGORY:
                    return rootNode;

                case DbObject.TYPE_PRODUCT: {
                    Product p = (Product) child;
                    Category c = sm().findCategoryById(p.getCategoryId()); // The parent object
                    return findNode(c);
                }

                case DbObject.TYPE_TYPE: {
                    Type t = (Type) child;
                    Product p = sm().findProductById(t.getProductId()); // The parent object
                    return findNode(p);
                }
            }
        }
        if (type == TYPE_ORDERS) {
            if(((Order)child).isOrdered()) {
                return (DefaultMutableTreeNode) rootNode.getChildAt(0); // Ordered
            } else {
                return (DefaultMutableTreeNode) rootNode.getChildAt(1); // Not ordered
            }
        }
        if (type == TYPE_PROJECTS) {
            if (DbObject.getType(child) == DbObject.TYPE_PROJECT_TYPE) { // Types are children of projects, types have no children
                DefaultMutableTreeNode node = findNode(child);
                if (node != null) {
                    return (DefaultMutableTreeNode) node.getParent();
                }
            }
        }
        return null;
    }

    private DefaultMutableTreeNode findNode(DbObject object) {
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
