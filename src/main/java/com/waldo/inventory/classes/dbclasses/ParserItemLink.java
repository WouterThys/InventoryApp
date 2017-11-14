package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ParserItemLink extends DbObject {

    public static final String TABLE_NAME = "parseritemlinks";

    // Variables
    private String parserName;
    private String pcbItemName; // "C" or "R" or ...

    private long categoryId;
    private Category category;

    private long productId;
    private Product product;

    private long typeId;
    private Type type;

    public ParserItemLink() {
        super(TABLE_NAME);
    }

    public ParserItemLink(String parserName) {
        super(TABLE_NAME);
        this.parserName = parserName;
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        // Add parameters
        statement.setString(ndx++, getParserName());
        statement.setString(ndx++, getPcbItemName());

        long cId = getCategoryId();
        if (cId < DbObject.UNKNOWN_ID) {
            cId = DbObject.UNKNOWN_ID;
        }
        long pId = getProductId();
        if (pId < DbObject.UNKNOWN_ID) {
            pId = DbObject.UNKNOWN_ID;
        }
        long tId = getTypeId();
        if (tId < DbObject.UNKNOWN_ID) {
            tId = DbObject.UNKNOWN_ID;
        }
        statement.setLong(ndx++, cId);
        statement.setLong(ndx++, pId);
        statement.setLong(ndx++, tId);

        return ndx;
    }

    @Override
    public ParserItemLink createCopy(DbObject copyInto) {
        ParserItemLink cpy = (ParserItemLink) copyInto;

        // Add variables
        cpy.setParserName(getParserName());
        cpy.setPcbItemName(getPcbItemName());
        cpy.setCategoryId(getCategoryId());
        cpy.setProductId(getProductId());
        cpy.setTypeId(getTypeId());

        return cpy;
    }

    @Override
    public ParserItemLink createCopy() {
        return createCopy(new ParserItemLink());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<ParserItemLink> list = db().getParserItemLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<ParserItemLink> list = db().getParserItemLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onParserItemLinkChangedListenerList);
    }

    public static ParserItemLink getUnknownParserItemLink() {
        ParserItemLink u = new ParserItemLink();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    public boolean hasCategory() {
        return categoryId > UNKNOWN_ID;
    }

    public boolean hasProduct() {
        return productId > UNKNOWN_ID;
    }

    public boolean hasType() {
        return typeId > UNKNOWN_ID;
    }

    // Getters and setters

    public String getParserName() {
        if (parserName == null) {
            parserName = "";
        }
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public String getPcbItemName() {
        if (pcbItemName == null) {
            pcbItemName = "";
        }
        return pcbItemName;
    }

    public void setPcbItemName(String pcbItemName) {
        this.pcbItemName = pcbItemName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        category = null;
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        if (category == null) {
            category = SearchManager.sm().findCategoryById(categoryId);
        }
        return category;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        product = null;
        this.productId = productId;
    }

    public Product getProduct() {
        if (product == null) {
            product = SearchManager.sm().findProductById(productId);
        }
        return product;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        type = null;
        this.typeId = typeId;
    }

    public Type getType() {
        if (type == null) {
            type = SearchManager.sm().findTypeById(typeId);
        }
        return type;
    }
}