package com.waldo.inventory.gui.components.treemodels;

import com.waldo.inventory.classes.dbclasses.Division;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class IDivisionTreeModel implements TreeModel {

    private Vector<TreeModelListener> treeModelListeners = new Vector<>();
    private Division rootDivision;

    public IDivisionTreeModel(Division rootDivision) {
        this.rootDivision = rootDivision;
    }

    protected void fireTreeStructureChanged(Division oldRoot) {
        int length = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[] {oldRoot});
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }

    //
    // TreeModel implementations
    //
    @Override
    public Division getRoot() {
        return rootDivision;
    }

    @Override
    public Division getChild(Object parent, int index) {
        try {
            Division d = (Division) parent;
            if (d != null) return d.getSubDivisionAt(index);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        try {
            Division d = (Division) parent;
            if (d != null) return d.getSubDivisionCount();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Division d = (Division) parent;
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        if (!treeModelListeners.contains(treeModelListener)) {
            treeModelListeners.addElement(treeModelListener);
        }
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }
}
