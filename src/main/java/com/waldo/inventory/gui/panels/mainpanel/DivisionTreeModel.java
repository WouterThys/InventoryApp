package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.*;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.sql.SQLException;
import java.util.Vector;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class DivisionTreeModel implements TreeModel {

    private DbObjectNode rootNode;
    private EventListenerList listenerList = new EventListenerList();

    DivisionTreeModel() {
       initializeTree();
    }

    private void initializeTree() {

        rootNode = new DbObjectNode(new Category("All"), false); // Virtual root

        for (Category category : dbInstance().getCategories()) {
            DbObjectNode cNode = new DbObjectNode(category, false);
            rootNode.getChildren().addElement(cNode);

            for (Product product : dbInstance().getProductListForCategory(category.getId())) {
                DbObjectNode pNode = new DbObjectNode(product, false);
                cNode.getChildren().addElement(pNode);

                for (Type type : dbInstance().getTypeListForProduct(product.getId())) {
                    DbObjectNode tNode = new DbObjectNode(type, false);
                    pNode.getChildren().add(tNode);
                }
            }
        }
    }


    //
    // Interface TreeModel
    //

    @Override
    public Object getRoot() {
        return rootNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((DbObjectNode)parent).getChildren().elementAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        if (!((DbObjectNode)parent).isLeaf()) {
            return ((DbObjectNode)parent).getChildren().size();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }


    /*
    * Notify all listeners that have registered interest for
    * notification on this event type.  The event instance
    * is lazily created using the parameters passed into
    * the fire method.
    * @see EventListenerList
    */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                         int[] childIndices,
                                         Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }


    class DbObjectNode {

        private DbObject dbObject;
        private boolean isLeaf;
        private Vector<DbObjectNode> children = new Vector<>();

        public DbObjectNode(DbObject dbObject, boolean isLeaf) {
            this.dbObject = dbObject;
            this.isLeaf = isLeaf;
        }

        public String toString() {
            return dbObject.toString();
        }

        public DbObject getDbObject() {
            return dbObject;
        }

        public Vector<DbObjectNode> getChildren() {
            return children;
        }

        public boolean isLeaf() {
            return isLeaf;
        }
    }

}
