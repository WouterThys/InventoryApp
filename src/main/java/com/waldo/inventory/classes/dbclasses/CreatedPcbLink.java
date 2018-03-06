package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class CreatedPcbLink  extends DbObject {

    public static final String TABLE_NAME = "createdpcblinks";

    // Link between ProjectPcb and CreatedPcb
    private long pcbItemProjectLinkId;
    private PcbItemProjectLink pcbItemProjectLink;

    private long createdPcbId;
    private CreatedPcb createdPcb;

    // Used item stuff
    private long usedItemId;
    private Item usedItem;
    private int usedAmount;

    // State
    private CreatedPcbLinkState state = CreatedPcbLinkState.NotSaved;


    public CreatedPcbLink() {
        super(TABLE_NAME);
    }

    public CreatedPcbLink(long pcbItemProjectLinkId, long createdPcbId, long usedItemId) {
        this();

        this.pcbItemProjectLinkId = pcbItemProjectLinkId;
        this.createdPcbId = createdPcbId;
        this.usedItemId = usedItemId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (pcbItemProjectLinkId < UNKNOWN_ID) {
            setPcbItemProjectLinkId(UNKNOWN_ID);
        }
        if (createdPcbId < UNKNOWN_ID) {
            setCreatedPcbId(UNKNOWN_ID);
        }
        if (usedItemId < UNKNOWN_ID) {
            setUsedItemId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getPcbItemProjectLinkId());
        statement.setLong(ndx++, getCreatedPcbId());
        statement.setLong(ndx++, getUsedItemId());
        statement.setInt(ndx++, getUsedAmount());

        return ndx;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<CreatedPcbLink> list = cache().getCreatedPcbLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<CreatedPcbLink> list = cache().getCreatedPcbLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    @Override
    public CreatedPcbLink createCopy() {
        return createCopy(new CreatedPcbLink());
    }

    @Override
    public CreatedPcbLink createCopy(DbObject copyInto) {
        CreatedPcbLink cpy = new CreatedPcbLink();
        copyBaseFields(cpy);
        cpy.setPcbItemProjectLinkId(getPcbItemProjectLinkId());
        cpy.setCreatedPcbId(getCreatedPcbId());
        cpy.setUsedItemId(getUsedItemId());
        cpy.setUsedAmount(getUsedAmount());
        return cpy;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj != null && obj instanceof CreatedPcb) {
//            CreatedPcb ref = (CreatedPcb) obj;
//
//            return ref.getProjectPcbId() == getProjectPcbId();
//        }
//        return false;
//    }


    public CreatedPcbLinkState getState() {
        if (getId() <= DbObject.UNKNOWN_ID) {
            state = CreatedPcbLinkState.NotSaved;
        } else {
            state = CreatedPcbLinkState.Ok;
            if (pcbItemProjectLinkId <= DbObject.UNKNOWN_ID) {
                state = CreatedPcbLinkState.Error;
                state.addMessage("No project found..");
            }
            if (usedItemId <= DbObject.UNKNOWN_ID) {
                state = CreatedPcbLinkState.Warning;
                state.addMessage("No used item");
            }
            // TODO..
        }
        return state;
    }

    public PcbItemItemLink getPcbItemItemLink() {
        PcbItemItemLink linkedItem = null;
        if (pcbItemProjectLinkId > DbObject.UNKNOWN_ID ) {
            if (getPcbItemProjectLink().getPcbItemId() > DbObject.UNKNOWN_ID) {
                linkedItem = getPcbItemProjectLink().getPcbItemItemLink();
            }
        }
        return linkedItem;
    }



    public long getPcbItemProjectLinkId() {
        return pcbItemProjectLinkId;
    }

    public void setPcbItemProjectLinkId(long pcbItemProjectLinkId) {
        if (pcbItemProjectLink != null && pcbItemProjectLink.getId() != pcbItemProjectLinkId) {
            pcbItemProjectLink = null;
        }
        this.pcbItemProjectLinkId = pcbItemProjectLinkId;
    }

    public PcbItemProjectLink getPcbItemProjectLink() {
        if (pcbItemProjectLink == null) {
            pcbItemProjectLink = SearchManager.sm().findPcbItemProjectLinkById(pcbItemProjectLinkId);
        }
        return pcbItemProjectLink;
    }


    public long getCreatedPcbId() {
        return createdPcbId;
    }

    public void setCreatedPcbId(long createdPcbId) {
        if (createdPcb != null && createdPcb.getId() != createdPcbId) {
            createdPcb = null;
        }
        this.createdPcbId = createdPcbId;
    }

    public CreatedPcb getCreatedPcb() {
        if (createdPcb == null) {
            createdPcb = SearchManager.sm().findCreatedPcbById(createdPcbId);
        }
        return createdPcb;
    }

    public long getUsedItemId() {
        return usedItemId;
    }

    public void setUsedItemId(long usedItemId) {
        if (usedItem != null && usedItem.getId() != usedItemId) {
            usedItem = null;
        }
        this.usedItemId = usedItemId;
    }

    public Item getUsedItem() {
        if (usedItem == null) {
            usedItem = SearchManager.sm().findItemById(usedItemId);
        }
        return usedItem;
    }

    public int getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(int usedAmount) {
        this.usedAmount = usedAmount;
    }
}
