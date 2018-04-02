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
    private int level = -1;

    public Division() {
        this("");
    }

    public Division(String name) {
        this(name, null);
    }

    public Division(Division parentDivision) {
        this("", parentDivision);
    }

    public Division(String name, Division parentDivision) {
        super(TABLE_NAME);
        setName(name);

         this.parentDivision = parentDivision;
        if (parentDivision != null) {
            parentDivisionId = parentDivision.getId();
        }
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

    public static Division createDummyDivision(String name, List<Division> subDivisions) {
        Division dummy =  new Division(name);
        dummy.setId(-1);
        dummy.setCanBeSaved(false);
        dummy.setDisplayType(Statics.IconDisplayType.Icon);
        dummy.setCanHaveValue(false);
        dummy.subDivisions = subDivisions;
        return dummy;
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

    public List<Division> getParentDivisions() {
        List<Division> parentDivisionList = new ArrayList<>();

        Division parent = getParentDivision();
        while(parent != null) {
            parentDivisionList.add(parent);
            parent = parent.getParentDivision();
        }

        return parentDivisionList;
    }


    public List<Division> getSubDivisions() {
        if (subDivisions == null) {
            subDivisions = SearchManager.sm().findDivisionsWithParent(getId());
        }
        return subDivisions;
    }

    public int getLevel() {
        if (level < 0) {
            level = 0;
            Division parent = getParentDivision();
            while (parent != null && !parent.isUnknown()) {
                level++;
                parent = parent.getParentDivision();
            }
        }
        return level;
    }

    public void setParentDivisionId(long parentDivisionId) {
        if (parentDivision != null && parentDivision.getId() != parentDivisionId) {
            parentDivision = null;
            level = -1;
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
