package com.waldo.inventory.gui.components.treetable;

import com.waldo.inventory.classes.*;

import java.sql.SQLException;
import java.util.Vector;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class ItemTableModel extends AbstractTreeTableModel implements TreeTableModel {

    // Names of the columns
    static private String[] columnNames = {"Name", "Description", "Manufacturer", "Datasheet"};

    // Types of the columns
    static private Class[] columnTypes = {TreeTableModel.class, String.class, String.class, String.class};

    private static DbObjectNode rootNode;

    public ItemTableModel() throws SQLException {
        super(rootNode);
    }

    static {
        try {

            rootNode = new DbObjectNode(new DbObject("", "", ""), false); // Virtual root

            for (Category category : dbInstance().getCategories()) {
                DbObjectNode cNode = new DbObjectNode(category, false);
                rootNode.getChildren().addElement(cNode);

                for (Product product : dbInstance().getProductListForCategory(category.getId())) {
                    DbObjectNode pNode = new DbObjectNode(product, false);
                    cNode.getChildren().addElement(pNode);

                    for (Type type : dbInstance().getTypeListForProduct(product.getId())) {
                        DbObjectNode tNode = new DbObjectNode(type, false);
                        pNode.getChildren().add(tNode);

                        for (Item item : dbInstance().getItemListForType(type.getId())) {
                            DbObjectNode iNode = new DbObjectNode(item, true);
                            tNode.getChildren().add(iNode);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    //
//    // Handy stuff
//    //
//    protected DbObject getDbObject(Object node) {
//        DbObjectNode objectNode = ((DbObjectNode)node);
//        return objectNode.getDbObject();
//    }
//
//    protected Object[] getChildren(Object node) {
//        DbObjectNode objectNode = ((DbObjectNode)node);
//        try {
//            return objectNode.getChildren();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



    //
    // Table Node Interface
    //

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Class getColumnClass(int column) {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        try {
            Item item = null;
            switch (column) {
                case 0: // name
                    return node;
                case 1: // description
                    item = getItem(node);
                    if (item != null) {
                        return item.getDescription();
                    }
                    return "";

                case 2: // Manufacturer
                    item = getItem(node);
                    if (item != null) {
                        Manufacturer m = dbInstance().findManufacturerById(item.getManufacturerId());
                        if (m != null && m.getId() != DbObject.UNKNOWN_ID) {
                            return m.getName();
                        }
                    }
                    return "";

                case 3: // Datasheet
                    item = getItem(node);
                    if (item != null) {
                        return item.getLocalDataSheet();
                    }
                    return "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Item getItem(Object node) {
        try {
            DbObject object = ((DbObjectNode)node).getDbObject();
            return (Item)object;
        } catch (ClassCastException e) {
            return null;
        }
    }

    //
    // The TreeModel interface
    //
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

    static class DbObjectNode {

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

//switch (DbObject.getType(dbObject)) {
//        case DbObject.TYPE_ITEM: // Item is lowest, doesnt have children
//        children = new DbObjectNode[0];
//        break;
//
//        case DbObject.TYPE_CATEGORY: // Category has products as children
//        List<Product> productList = DbManager.dbInstance().getProductListForCategory(dbObject.getId());
//        children = new DbObjectNode[productList.size()];
//        for (int i = 0; i < productList.size(); i++) {
//        children[i] = new DbObjectNode(productList.get(i));
//        }
//        break;
//
//        case DbObject.TYPE_PRODUCT: // Product has types as children
//        List<Type> typeList = DbManager.dbInstance().getTypeListForProduct(dbObject.getId());
//        children = new DbObjectNode[typeList.size()];
//        for (int i = 0; i < typeList.size(); i++) {
//        children[i] = new DbObjectNode(typeList.get(i));
//        }
//        break;
//
//        case DbObject.TYPE_TYPE: // Type has items as children
//        List<Item> itemList = DbManager.dbInstance().getItemListForType(dbObject.getId());
//        children = new DbObjectNode[itemList.size()];
//        for (int i = 0; i < itemList.size(); i++) {
//        children[i] = new DbObjectNode(itemList.get(i));
//        }
//        break;
//        }


