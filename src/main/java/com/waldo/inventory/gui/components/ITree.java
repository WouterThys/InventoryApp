package com.waldo.inventory.gui.components;

import com.waldo.inventory.classes.dbclasses.DbObject;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;

public abstract class ITree<T extends DbObject> extends JTree {

    protected T root;
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;

    public ITree() {
        this(null);
    }

    public ITree (TreeModel treeModel) {
        super(treeModel);

        Dimension d = getPreferredSize();
        d.width = 200;
        setMinimumSize(d);
    }

    public ITree(T root, boolean showRoot, boolean allowMultiSelect) {
        super();

        this.root = root;
        treeModel = createModel(root);
        setModel(treeModel);

        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        Dimension d = getPreferredSize();
        d.width = 200;
        setMinimumSize(d);

        if (allowMultiSelect) {
            getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        } else {
            getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
    }

    protected abstract DefaultTreeModel createModel(T root);


    public void collapseAll() {

    }

    public void expandAll() {

    }


    public T getSelectedItem() {
        T selected = null;
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null) {
            selected = (T) selectedNode.getUserObject();
        }
        return selected;
    }

    private DefaultMutableTreeNode getSelectedNode() {
        DefaultMutableTreeNode selected = null;
        TreePath path = getSelectionModel().getSelectionPath();
        if (path != null) {
            selected = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        return selected;
    }

    public void setSelectedItem(T item) {
        if (item != null) {
            setSelectedNode(findNode(item));
        } else {
            clearSelection();
        }
    }

    public void setSelectedNode(TreeNode node) {
        if (node != null) {
            TreeNode[] nodes = ((DefaultTreeModel)getModel()).getPathToRoot(node);
            TreePath path = new TreePath(nodes);

            setSelectionPath(path);
            scrollPathToVisible(path);
        }
    }

    public void updateTree() {
        treeModel.reload();
    }

    public void updateItem(T item) {
        if (item != null) {
            DefaultMutableTreeNode node = findNode(item);
            if (node != null) {
                treeModel.nodeChanged(node);
            }
        }
    }

    public void addItem(T item) {
        if (item != null) {
            DefaultMutableTreeNode parentNode = getSelectedNode();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(item);

            if (parentNode == null) {
                parentNode = rootNode;
            }

            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    public void removeItem(T parent, T item) {
        if (item != null) {
            MutableTreeNode divisionNode = findNode(item);
            MutableTreeNode parentNode = findNode(parent);
            if (parentNode != null && divisionNode != null) {
                treeModel.removeNodeFromParent(divisionNode);
            }
        }
    }

    public DefaultMutableTreeNode findNode(T item) {
        Enumeration e = rootNode.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            T nodeObject = (T) node.getUserObject();
            if (nodeObject != null) {
                if (nodeObject.equals(item)) {
                    return node;
                }
            }
        }
        return null;
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


}
