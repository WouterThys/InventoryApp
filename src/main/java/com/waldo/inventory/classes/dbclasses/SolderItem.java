package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.SolderItemState;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.FileUtils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

import static com.waldo.inventory.managers.CacheManager.cache;

public class SolderItem extends DbObject {

    public static final String TABLE_NAME = "solderitems";

    // Name = Reference !!

    // Link between ProjectPcb and CreatedPcb
    private long createdPcbLinkId;
    private CreatedPcbLink createdPcbLink;

    // Used item stuff
    private long usedItemId;
    private Item usedItem;

    // State
    private SolderItemState state;
    private int numTimesSoldered;
    private int numTimesDesoldered;
    private Date solderDate;
    private Date desolderDate;

    // Remarks
    private String remarksFile;

    // Use only to read from DB
    public SolderItem() {
        super(TABLE_NAME);
    }

    public SolderItem(String reference, CreatedPcbLink createdPcbLink) {
        this();

        setName(reference);
        this.createdPcbLink = createdPcbLink;
        if (createdPcbLink != null) {
            createdPcbLinkId = createdPcbLink.getId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (super.equals(o)) {
            if (!(o instanceof SolderItem)) return false;
            if (!super.equals(o)) return false;
            SolderItem that = (SolderItem) o;
            return getCreatedPcbLinkId() == that.getCreatedPcbLinkId() &&
                    getUsedItemId() == that.getUsedItemId() &&
                    getNumTimesSoldered() == that.getNumTimesSoldered() &&
                    getNumTimesDesoldered() == that.getNumTimesDesoldered() &&
                    getState() == that.getState() &&
                    Objects.equals(getSolderDate(), that.getSolderDate()) &&
                    Objects.equals(getDesolderDate(), that.getDesolderDate());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedPcbLinkId(), getUsedItemId(), getState(), getNumTimesSoldered(), getNumTimesDesoldered(), getSolderDate(), getDesolderDate());
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setString(ndx++, getName());
        statement.setLong(ndx++, getCreatedPcbLinkId());
        statement.setLong(ndx++, getUsedItemId());

        statement.setInt(ndx++, getState().getIntValue());
        statement.setInt(ndx++, getNumTimesSoldered());
        statement.setInt(ndx++, getNumTimesDesoldered());

        if (getSolderDate() != null) {
            statement.setTimestamp(ndx++, new Timestamp(solderDate.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        if (getDesolderDate() != null) {
            statement.setTimestamp(ndx++, new Timestamp(desolderDate.getTime()));
        } else {
            statement.setDate(ndx++, null);
        }

        SerialBlob blob = FileUtils.fileToBlob(getRemarksFile());
        if (blob != null) {
            statement.setBlob(ndx++, blob);
        } else {
            statement.setString(ndx++, null);
        }

        return ndx;
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert:
                cache().add(this);
                break;
            case Delete:
                cache().remove(this);
                break;
        }
    }

    @Override
    public SolderItem createCopy() {
        return createCopy(new SolderItem());
    }

    @Override
    public SolderItem createCopy(DbObject copyInto) {
        SolderItem cpy = new SolderItem();
        copyBaseFields(cpy);
        cpy.setCreatedPcbLinkId(getCreatedPcbLinkId());
        cpy.setUsedItemId(getUsedItemId());
        cpy.setState(getState());
        cpy.setNumTimesSoldered(getNumTimesSoldered());
        cpy.setNumTimesDesoldered(getNumTimesDesoldered());
        cpy.setSolderDate(getSolderDate());
        cpy.setDesolderDate(getDesolderDate());
        cpy.setRemarksFile(getRemarksFile());
        return cpy;
    }




    public long getCreatedPcbLinkId() {
        if (createdPcbLinkId < UNKNOWN_ID) {
            createdPcbLinkId = UNKNOWN_ID;
        }
        return createdPcbLinkId;
    }

    public void setCreatedPcbLinkId(long createdPcbLinkId) {
        if (createdPcbLink != null && createdPcbLink.getId() != createdPcbLinkId) {
            createdPcbLink = null;
        }
        this.createdPcbLinkId = createdPcbLinkId;
    }

    public CreatedPcbLink getCreatedPcbLink() {
        if (createdPcbLink == null && getCreatedPcbLinkId() > UNKNOWN_ID) {
            createdPcbLink = SearchManager.sm().findCreatedPcbLinkById(createdPcbLinkId);
        }
        return createdPcbLink;
    }


    public long getUsedItemId() {
        if (usedItemId < UNKNOWN_ID) {
            usedItemId = UNKNOWN_ID;
        }
        return usedItemId;
    }

    public void setUsedItemId(long usedItemId) {
        if (usedItem != null && usedItem.getId() != usedItemId) {
            usedItem = null;
        }
        this.usedItemId = usedItemId;
    }

    public Item getUsedItem() {
        if (usedItem == null && usedItemId > UNKNOWN_ID) {
            usedItem = SearchManager.sm().findItemById(usedItemId);
        }
        return usedItem;
    }


    public SolderItemState getState() {
        if (state == null) {
            state = SolderItemState.None;
        }
        return state;
    }

    public void setState(SolderItemState state) {
        this.state = state;
    }

    public void setState(int state) {
        this.state = SolderItemState.fromInt(state);
    }

    public int getNumTimesSoldered() {
        return numTimesSoldered;
    }

    public void setNumTimesSoldered(int numTimesSoldered) {
        this.numTimesSoldered = numTimesSoldered;
    }

    public int getNumTimesDesoldered() {
        return numTimesDesoldered;
    }

    public void setNumTimesDesoldered(int numTimesDesoldered) {
        this.numTimesDesoldered = numTimesDesoldered;
    }

    public Date getSolderDate() {
        return solderDate;
    }

    public void setSolderDate(Date solderDate) {
        this.solderDate = solderDate;
    }

    public void setSolderDate(Timestamp solderDate) {
        if (solderDate != null) {
            this.solderDate = new Date(solderDate.getTime());
        }
    }

    public Date getDesolderDate() {
        return desolderDate;
    }

    public void setDesolderDate(Date desolderDate) {
        this.desolderDate = desolderDate;
    }

    public void setDesolderDate(Timestamp desolderDate) {
        if (desolderDate != null) {
            this.desolderDate = new Date(desolderDate.getTime());
        }
    }

    public String createRemarksFileName() {
        return getId() + "_SolderedItem_";
    }

    public File getRemarksFile() {
        if (remarksFile != null && !remarksFile.isEmpty()) {
            return new File(remarksFile);
        }
        return null;
    }

    String getRemarksFileName() {
        if (remarksFile == null) {
            remarksFile = "";
        }
        return remarksFile;
    }

    public void setRemarksFile(File remarksFile) {
        if (remarksFile != null && remarksFile.exists()) {
            this.remarksFile = remarksFile.getAbsolutePath();
        }
    }
}

