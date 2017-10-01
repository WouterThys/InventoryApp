package com.waldo.inventory.classes;

import com.waldo.inventory.classes.kicad.PcbItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PcbItemProjectLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemprojectlinks";

    // Variables
    private long pcbItemId; // Id of PcbItem
    private long projectPcbId; // Link to pcb in project

    private PcbItem pcbItem;
    private ProjectPcb projectPcb;

    public PcbItemProjectLink() {
        super(TABLE_NAME);
    }

    public PcbItemProjectLink(PcbItem pcbItem, ProjectPcb projectPcb) {
        super(TABLE_NAME);
        this.pcbItem = pcbItem;
        this.projectPcb = projectPcb;

        if (pcbItem != null) {
            pcbItemId = pcbItem.getId();
        }
        if (projectPcb != null) {
            projectPcbId = projectPcb.getId();
        }
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (getPcbItemId() < UNKNOWN_ID) {
            setPcbItemId(UNKNOWN_ID);
        }
        if (getProjectPcbId() < UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getPcbItemId());
        statement.setLong(ndx++, getProjectPcbId());

        return ndx;
    }

    @Override
    public PcbItemProjectLink createCopy() {
        return createCopy(new PcbItemProjectLink());
    }

    @Override
    public PcbItemProjectLink createCopy(DbObject copyInto) {
        PcbItemProjectLink cpy = (PcbItemProjectLink) copyInto;
        copyBaseFields(cpy);

        // Add variables
        cpy.setPcbItemId(getPcbItemId());
        cpy.setProjectPcbId(getProjectPcbId());

        return cpy;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<PcbItemProjectLink> list = db().getPcbItemProjectLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }

                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<PcbItemProjectLink> list = db().getPcbItemProjectLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onPcbItemProjectLinkChangedListenerList);
    }

    // Getters and setters

    public long getPcbItemId() {
        return pcbItemId;
    }

    public void setPcbItemId(long pcbItemId) {
        this.pcbItem = null;
        this.pcbItemId = pcbItemId;
    }

    public long getProjectPcbId() {
        return projectPcbId;
    }

    public void setProjectPcbId(long projectPcbId) {
        this.projectPcb = null;
        this.projectPcbId = projectPcbId;
    }

    public PcbItem getPcbItem() {
        if (pcbItem == null) {
            pcbItem = SearchManager.sm().findKcComponentById(pcbItemId);
        }
        return pcbItem;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }
}
