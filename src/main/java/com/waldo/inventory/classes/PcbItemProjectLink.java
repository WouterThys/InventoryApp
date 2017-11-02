package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PcbItemProjectLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemprojectlinks";

    // Variables
    private long pcbItemId; // Id of PcbItem
    private long projectPcbId; // Link to pcb in project

    private PcbItem pcbItem;
    private ProjectPcb projectPcb;

    private int usedCount; // Amount used = amount not available anymore in stock
    private boolean used;

    // Needed because multiple links can point to same pcbItem
    private String sheetName;

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

        String references = "";
        String sheetName = "";

        if (getPcbItemId() < UNKNOWN_ID) {
            setPcbItemId(UNKNOWN_ID);
        } else {
            references = getPcbItem().getReferenceString();
            sheetName = getPcbItem().getSheetName();
        }
        if (getProjectPcbId() < UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getPcbItemId());
        statement.setLong(ndx++, getProjectPcbId());
        statement.setInt(ndx++, getUsedCount());

        if (getUsedCount() > 0) {
            setUsed(true);
        }

        // Pcb item variables relevant for project
        statement.setString(ndx++, references);
        statement.setString(ndx++, sheetName);

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
        cpy.setUsedCount(getUsedCount());

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

    public void setPcbItemReferences(String references) {
        if (getPcbItemId() > UNKNOWN_ID) {
            String[] split = references.split(",");
            List<String> refs = new ArrayList<>();
            for (String ref : split) {
                refs.add(ref.trim());
            }
            if (refs.size() > 0) {
                getPcbItem().setRef(refs.get(0));
            }
            getPcbItem().setReferences(refs);
        }
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
        if (getPcbItemId() > UNKNOWN_ID) {
            getPcbItem().setSheetName(sheetName);
        }
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
            PcbItem foundItem = SearchManager.sm().findPcbItemById(pcbItemId);
            pcbItem = foundItem.createCopy();
        }
        return pcbItem;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }

    public String getSheetName() {
        if (sheetName == null) {
            sheetName = "";
        }
        return sheetName;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
