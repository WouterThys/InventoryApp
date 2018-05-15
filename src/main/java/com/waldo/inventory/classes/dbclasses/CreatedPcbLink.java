package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.CreatedPcbLinkState;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.FileUtils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class CreatedPcbLink extends DbObject {

    public static final String TABLE_NAME = "createdpcblinks";
    public static final int NOT_USED = -2;

    // Link between ProjectPcb and CreatedPcb
    private long pcbItemProjectLinkId;
    private PcbItemProjectLink pcbItemProjectLink;

    private long createdPcbId;
    private CreatedPcb createdPcb;

    // Remarks
    private String remarksFile;

    // Items
    private List<SolderItem> solderItems;

    public CreatedPcbLink() {
        super(TABLE_NAME);
    }

    public CreatedPcbLink(long pcbItemProjectLinkId, long createdPcbId) {
        this();

        this.pcbItemProjectLinkId = pcbItemProjectLinkId;
        this.createdPcbId = createdPcbId;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        statement.setLong(ndx++, getPcbItemProjectLinkId());
        statement.setLong(ndx++, getCreatedPcbId());
        if (getPcbItemProjectLink() != null) {
            statement.setLong(ndx++, getPcbItemProjectLink().getPcbItemId());
        } else {
            statement.setLong(ndx++, UNKNOWN_ID);
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
        return cpy;
    }


    public CreatedPcbLinkState getState() {
//        CreatedPcbLinkState state;
//        if (getId() < DbObject.UNKNOWN_ID) {
//            state = CreatedPcbLinkState.NotSaved;
//        } else {
//            if (getUsedAmount() == NOT_USED) {
//                state = CreatedPcbLinkState.NotUsed;
//            } else {
//                state = CreatedPcbLinkState.Ok;
//                if (pcbItemProjectLinkId <= DbObject.UNKNOWN_ID) {
//                    state = CreatedPcbLinkState.Error;
//                    state.clearMessages();
//                    state.addMessage("No project found..");
//                } else {
//                    if (getPcbItemProjectLink().getPcbItemId() <= DbObject.UNKNOWN_ID) {
//                        state = CreatedPcbLinkState.Warning;
//                        state.addMessage("No PCB item..");
//                    }
//                    if (getPcbItemItemLink() == null) {
//                        if (state != CreatedPcbLinkState.Warning) {
//                            state = CreatedPcbLinkState.Warning;
//                            state.clearMessages();
//                        }
//                        state.addMessage("No linked item..");
//                    }
//                }
//            }
//            // TODO..
//        }
//        return state;
        return CreatedPcbLinkState.Ok;
    }

    public List<SolderItem> getSolderItems() {
        if (solderItems == null) {
            solderItems = SearchManager.sm().findSolderItemsForCreatedPcbLinkId(getId());
        }
        return solderItems;
    }

    public void updateSolderItems() {
        solderItems = null;
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
        if (pcbItemProjectLinkId < UNKNOWN_ID) {
            pcbItemProjectLinkId = UNKNOWN_ID;
        }
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

    public String createRemarksFileName() {
        return getId() + "_CreadPcbLink_";
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
