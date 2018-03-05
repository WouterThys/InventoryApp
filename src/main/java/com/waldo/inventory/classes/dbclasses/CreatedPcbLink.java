package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class CreatedPcbLink  extends DbObject {

    public static final String TABLE_NAME = "createdpcblinks";

    // Link between ProjectPcb and CreatedPcb
    private long projectPcbId;
    private ProjectPcb projectPcb;

    private long createdPcbId;
    private CreatedPcb createdPcb;

    // Pcb item
    private long pcbItemId;
    private PcbItem pcbItem;

    // Used item stuff
    private long usedItemId;
    private Item usedItem;
    private int usedAmount;


    public CreatedPcbLink() {
        super(TABLE_NAME);
    }

    public CreatedPcbLink(long projectPcbId, long createdPcbId, long pcbItemId, long usedItemId) {
        this();

        this.projectPcbId = projectPcbId;
        this.createdPcbId = createdPcbId;
        this.pcbItemId = pcbItemId;
        this.usedItemId = usedItemId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (projectPcbId < UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }
        if (createdPcbId < UNKNOWN_ID) {
            setCreatedPcbId(UNKNOWN_ID);
        }
        if (usedItemId < UNKNOWN_ID) {
            setUsedItemId(UNKNOWN_ID);
        }
        if (pcbItemId < UNKNOWN_ID) {
            setPcbItemId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getProjectPcbId());
        statement.setLong(ndx++, getCreatedPcbId());
        statement.setLong(ndx++, getPcbItemId());
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
    public CreatedPcb createCopy() {
        return createCopy(new CreatedPcb());
    }

    @Override
    public CreatedPcb createCopy(DbObject copyInto) {
        CreatedPcb cpy = new CreatedPcb();
        copyBaseFields(cpy);
        cpy.setProjectPcbId(getProjectPcbId());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CreatedPcb) {
            CreatedPcb ref = (CreatedPcb) obj;

            return ref.getProjectPcbId() == getProjectPcbId();
        }
        return false;
    }

    public PcbItemItemLink getPcbItemItemLink() {
        PcbItemItemLink linkedItem = null;
        if (pcbItemId > DbObject.UNKNOWN_ID && projectPcbId > DbObject.UNKNOWN_ID) {
            PcbItemProjectLink projectLink = SearchManager.sm().findPcbItemProjectLink(projectPcbId, pcbItemId);
            if (projectLink != null) {
                linkedItem = projectLink.getPcbItemItemLink(); // Can be null!
            }
        }
        return linkedItem;
    }



    public long getProjectPcbId() {
        return projectPcbId;
    }

    public void setProjectPcbId(long projectPcbId) {
        if (projectPcb != null && projectPcb.getId() != projectPcbId) {
            projectPcb = null;
        }
        this.projectPcbId = projectPcbId;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
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

    public long getPcbItemId() {
        return pcbItemId;
    }

    public void setPcbItemId(long pcbItemId) {
        if (pcbItem != null && pcbItem.getId() != pcbItemId) {
            pcbItem = null;
        }
        this.pcbItemId = pcbItemId;
    }

    public PcbItem getPcbItem() {
        if (pcbItem == null) {
            pcbItem = SearchManager.sm().findPcbItemById(pcbItemId);
        }
        return pcbItem;
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
