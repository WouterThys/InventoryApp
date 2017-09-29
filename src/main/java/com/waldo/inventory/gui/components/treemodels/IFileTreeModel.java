package com.waldo.inventory.gui.components.treemodels;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFileTreeModel implements TreeModel, Serializable, Cloneable {

    protected EventListenerList listeners;

    private static final Object LEAF = new Serializable() {
    };

    private Map map;
    private File root;

    public IFileTreeModel(File root) {
        this();
        setRoot(root);
    }

    public IFileTreeModel() {
        this.listeners = new EventListenerList();
        this.map = new HashMap();
    }

    public void setRoot(File root) {
        this.root = root;

        if (!root.isDirectory()) {
            map.put(root, LEAF);
        }
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return map.get(node) == LEAF;
    }

    public int getChildCount(Object node) {
        List children = children(node);

        if (children == null)
            return 0;

        return children.size();
    }

    public Object getChild(Object parent, int index) {
        return children(parent).get(index);
    }

    public int getIndexOfChild(Object parent, Object child) {
        return children(parent).indexOf(child);
    }

    protected List children(Object node) {
        File f = (File) node;

        Object value = map.get(f);

        if (value == LEAF)
            return null;

        List children = (List) value;

        if (children == null) {
            File[] c = f.listFiles();

            if (c != null) {
                children = new ArrayList(c.length);

                for (int len = c.length, i = 0; i < len; i++) {
                    children.add(c[i]);
                    if (!c[i].isDirectory())
                        map.put(c[i], LEAF);
                }
            } else
                children = new ArrayList(0);

            map.put(f, children);
        }

        return children;
    }

    public void valueForPathChanged(TreePath path, Object value) {
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }

    public Object clone() {
        try {
            IFileTreeModel clone = (IFileTreeModel) super.clone();

            clone.listeners = new EventListenerList();

            clone.map = new HashMap(map);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}