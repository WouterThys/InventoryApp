package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.util.Enumeration;

import static com.waldo.inventory.database.SearchManager.sm;

public class IDbObjectTreeModel extends DefaultTreeModel {

    public static final int TYPE_DIVISIONS = 0;
    public static final int TYPE_ORDERS = 1;

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

    public void expandNodes(int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandNodes(rowCount, tree.getRowCount());
        }
    }

    public void setSelectedObject(DbObject object) {
        DefaultMutableTreeNode node = findNode(object);
        if (node != null) {
            TreeNode[] nodes = getPathToRoot(node);
            TreePath path = new TreePath(nodes);
            tree.setExpandsSelectedPaths(true);
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);


//            TreePath path = new TreePath(node);
//            tree.setExpandsSelectedPaths(true);
//            tree.setSelectionPath(path);
//            tree.scrollPathToVisible(path);
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
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode childNode = findNode(child);
            if (childNode != null) {
                removeNodeFromParent(childNode);
            }
            reload();
        });
    }

    public void updateObject(DbObject newChild, DbObject oldChild) {
        //SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode node = findNode(oldChild);
            if (node != null) {
                node.setUserObject(newChild);
                nodeChanged(node);
            }
            reload();
        //});
    }

    public void addObject(DbObject child) {
        SwingUtilities.invokeLater(() -> addDbObject(findParent(child), child));
    }

    private DefaultMutableTreeNode addDbObject(DefaultMutableTreeNode parent, DbObject child) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (type == TYPE_ORDERS) {
            childNode.setAllowsChildren(false);
        }

        if (parent == null) {
            parent = rootNode;
        }

        insertNodeInto(childNode, parent, parent.getChildCount());

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

                default:
                    return null; // Error
            }
        }
        if (type == TYPE_ORDERS) {
            if(((Order)child).isOrdered()) {
                return (DefaultMutableTreeNode) rootNode.getChildAt(0); // Ordered
            } else {
                return (DefaultMutableTreeNode) rootNode.getChildAt(1); // Not ordered
            }
        }
        return null;
    }

    private DefaultMutableTreeNode findNode(DbObject object) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = rootNode.depthFirstEnumeration();

        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (object.equals(node.getUserObject())) {
                return node;
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
