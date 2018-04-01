package com.waldo.inventory.gui.components.treemodels;

import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class ISetTreeModel implements TreeModel {

    private Vector<TreeModelListener> treeModelListeners = new Vector<>();
    private Set rootSet;

    public ISetTreeModel(Set rootSet) {
        this.rootSet = rootSet;
    }

    public void fireTreeStructureChanged(Set changedSet) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[] {changedSet});
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }

    //
    // TreeModel implementations
    //
    @Override
    public Set getRoot() {
        return rootSet;
    }

    @Override
    public Item getChild(Object parent, int index) {
        try {
            if (parent instanceof Set) {
                Set s = (Set) parent;
                return s.getSetItems().get(index);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        try {
            if (parent instanceof Set) {
                Set s = (Set) parent;
                return s.getSetItems().size();
            }
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
        System.out.println("ISetTreeModel: value for path changed, path=" + path + ", new value=" + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        try {
            if (parent instanceof Set) {
                Set s = (Set) parent;
                Item c = (Item) child;
                return s.getSetItems().indexOf(c);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
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
