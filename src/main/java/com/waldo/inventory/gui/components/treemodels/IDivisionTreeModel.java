package com.waldo.inventory.gui.components.treemodels;

import com.waldo.inventory.classes.dbclasses.Division;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class IDivisionTreeModel implements TreeModel {

    private Vector<TreeModelListener> treeModelListeners = new Vector<>();
    private Division rootDivision;

    public IDivisionTreeModel(Division rootDivision) {
        this.rootDivision = rootDivision;
    }

    public void fireTreeStructureChanged(Division changedDivision) {
        if (changedDivision != null) {
            List<Division> affectedDivisions = new ArrayList<>();
            affectedDivisions.add(changedDivision);
            Division parent = changedDivision.getParentDivision();
            while (parent != null) {
                affectedDivisions.add(0, parent);
                parent = parent.getParentDivision();
            }

            TreeModelEvent e = new TreeModelEvent(this, affectedDivisions.toArray());
            for (TreeModelListener tml : treeModelListeners) {
                tml.treeStructureChanged(e);
            }
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
            if (d != null) return d.getSubDivisions().get(index);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        try {
            Division d = (Division) parent;
            if (d != null) return d.getSubDivisions().size();
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
        System.out.println("IDivisionsTreeModel: value for path changed, path=" + path + ", new value=" + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        try {
            Division p = (Division) parent;
            Division c = (Division) child;
            if (p != null && c != null) return p.getSubDivisions().indexOf(c);
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
