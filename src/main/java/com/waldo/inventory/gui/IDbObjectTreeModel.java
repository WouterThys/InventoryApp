package com.waldo.inventory.gui;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class IDbObjectTreeModel extends DefaultTreeModel {

    private DefaultMutableTreeNode rootNode;
    private ITree tree;

    public IDbObjectTreeModel(DefaultMutableTreeNode rootNode) {
        super(rootNode, true);
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
        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeNode node = findNode(oldChild);
            if (node != null) {
                node.setUserObject(newChild);
                nodeChanged(node);
            }
            reload();
        });
    }

    public void addObject(DbObject child) {
        SwingUtilities.invokeLater(() -> addDbObject(findParent(child), child, true));
    }

    private DefaultMutableTreeNode addDbObject(DefaultMutableTreeNode parent, DbObject child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        insertNodeInto(childNode, parent, parent.getChildCount());

        if (shouldBeVisible)  {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    private DefaultMutableTreeNode findParent(DbObject child) {
        switch (DbObject.getType(child)) {
            case DbObject.TYPE_CATEGORY:
                return rootNode;

            case DbObject.TYPE_PRODUCT: {
                Product p = (Product) child;
                Category c = DbManager.db().findCategoryById(p.getCategoryId()); // The parent object
                return findNode(c);
            }

            case DbObject.TYPE_TYPE: {
                Type t = (Type) child;
                Product p = DbManager.db().findProductById(t.getProductId()); // The parent object
                return findNode(p);
            }

            default: return null; // Error
        }
    }

    private DefaultMutableTreeNode findNode(DbObject object) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = rootNode.depthFirstEnumeration();

        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.getUserObject().equals(object)) {
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
