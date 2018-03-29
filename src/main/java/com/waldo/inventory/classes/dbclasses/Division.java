package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class Division extends DbObject {

    public static final String TABLE_NAME = "divisions";

    private boolean canHaveValue;
    private Statics.IconDisplayType displayType;
    private List<Item> itemList;

    // Composite design pattern
    private long parentDivisionId;
    private Division parentDivision;
    private List<Division> subDivisions;

    public Division() {
        this("");
    }

    public Division(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);
        statement.setLong(ndx++, getParentDivisionId());
        statement.setBoolean(ndx++, isCanHaveValue());
        statement.setInt(ndx++, getDisplayType().getIntValue());
        return ndx;
    }

    @Override
    public Division createCopy(DbObject copyInto) {
        Division cpy = (Division) copyInto;
        copyBaseFields(cpy);
        cpy.setParentDivisionId(getParentDivisionId());
        cpy.setCanHaveValue(isCanHaveValue());
        cpy.setDisplayType(getDisplayType());
        return cpy;
    }

    @Override
    public Division createCopy() {
        return createCopy(new Division());
    }

    public static Division createDummyDivision(String name) {
        Division dummy =  new Division(name);
        dummy.setId(-1);
        dummy.setCanBeSaved(false);
        dummy.setDisplayType(Statics.IconDisplayType.Icon);
        dummy.setCanHaveValue(false);
        return dummy;
    }

    public static void linkDivisions(Division parentDivision, List<Division> subDivisions) {
        if (subDivisions != null) {
            for (Division sub : subDivisions) {
                if (parentDivision != null) {
                    parentDivision.getSubDivisions().add(sub);
                    sub.setParentDivisionId(parentDivision.getId());
                }
            }
        }
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }


    //
    // Getters and setters
    //
    public List<Item> getItemList() {
        if (itemList == null) {
            itemList = SearchManager.sm().findItemsForDivision(this);
        }
        return itemList;
    }

    public void updateItemList() {
        itemList = null;
    }

    public List<Division> getSubDivisions() {
        if (subDivisions == null) {
            subDivisions = new ArrayList<>();
        }
        return subDivisions;
    }

    public int getSubDivisionCount() {
        return getSubDivisions().size();
    }

    public Division getSubDivisionAt(int index) {
        return getSubDivisions().get(index);
    }

    public int getIndexOfSubDivision(Division subDivision) {
        return getSubDivisions().indexOf(subDivision);
    }

    public void setParentDivisionId(long parentDivisionId) {
        if (parentDivision != null && parentDivision.getId() != parentDivisionId) {
            parentDivision = null;
        }
        this.parentDivisionId = parentDivisionId;
    }

    public long getParentDivisionId() {
        if (parentDivisionId < UNKNOWN_ID) {
            parentDivisionId = UNKNOWN_ID;
        }
        return parentDivisionId;
    }

    public Division getParentDivision() {
        if (parentDivision == null && getParentDivisionId() > UNKNOWN_ID) {
            parentDivision = SearchManager.sm().findDivisionById(parentDivisionId);
        }
        return parentDivision;
    }

    public boolean isCanHaveValue() {
        return canHaveValue;
    }

    public void setCanHaveValue(boolean canHaveValue) {
        this.canHaveValue = canHaveValue;
    }

    public Statics.IconDisplayType getDisplayType() {
        if (displayType == null) {
            displayType = Statics.IconDisplayType.Icon;
        }
        return displayType;
    }

    public void setDisplayType(Statics.IconDisplayType displayType) {
        this.displayType = displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = Statics.IconDisplayType.fromInt(displayType);
    }
}
