package com.waldo.inventory.gui.components.trees;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.components.ITree;

import javax.swing.tree.*;
import java.util.Enumeration;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

public class ISetTree extends ITree {

    private Set rootSet;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public ISetTree(boolean showRoot) {
        super();

        treeModel = createModel();
        setModel(treeModel);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(imageResource.readIcon("Items.Tree.Set"));
        setCellRenderer(renderer);
        setRootVisible(showRoot);
        setExpandsSelectedPaths(true);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private DefaultTreeModel createModel() {
        rootSet = Set.createDummySet("Dummy", cache().getSets());
        rootNode = new DefaultMutableTreeNode(rootSet);

        for(Item item : rootSet.getSetItems()) {
            rootNode.add(new DefaultMutableTreeNode(item, false));
        }

        return new DefaultTreeModel(rootNode);
    }

    public Set getRootSet() {
        return rootSet;
    }

    public Set getSelectedSet() {
        Set selected = null;
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode != null) {
            selected = (Set) selectedNode.getUserObject();
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

    public void setSelectedSet(Set set) {
        if (set != null) {
            DefaultMutableTreeNode node = findNodeFromSet(set);
            if (node != null) {
                TreeNode[] nodes = ((DefaultTreeModel) getModel()).getPathToRoot(node);
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

    public void updateSet(Set set) {
        if (set != null) {
            DefaultMutableTreeNode node = findNodeFromSet(set);
            if (node != null) {
                treeModel.nodeChanged(node);
            }
        }
    }

    public void addSet(Set set) {
        if (set != null) {
            rootSet.getSetItems().add(set);

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(set);
            treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
            scrollPathToVisible(new TreePath(node.getPath()));
        }
    }

    public void removeSet(Set set) {
        if (set != null) {
            DefaultMutableTreeNode node = findNodeFromSet(set);
            if (node != null) {
                treeModel.removeNodeFromParent(node);
            }
            rootSet.getSetItems().remove(set);
        }
    }

    private DefaultMutableTreeNode findNodeFromSet(Set set) {
        Enumeration e = rootNode.depthFirstEnumeration();
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
