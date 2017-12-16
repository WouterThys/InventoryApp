package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItemProjectLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemprojectlinks";

    // Variables
    private long pcbItemId; // Id of PcbItem
    private long projectPcbId; // Link to pcb in project

    private PcbItem pcbItem;
    private ProjectPcb projectPcb;

    private int usedCount; // Amount used = amount not available anymore in stock
    private boolean used;
    private boolean processed; // Helper variable for used dialog

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
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PcbItemProjectLink) {
            PcbItemProjectLink ref = (PcbItemProjectLink) obj;

            return ref.getPcbItemId() == getPcbItemId() &&
                    ref.getProjectPcbId() == getProjectPcbId() &&
                    ref.getUsedCount() == getUsedCount() &&
                    ref.getSheetName().equals(getSheetName());
        }
        return false;
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
        cpy.sheetName = (getSheetName());
        cpy.setUsed(isUsed());
        cpy.setProcessed(isProcessed());

        return cpy;
    }

    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<PcbItemProjectLink> list = cache().getPcbItemProjectLinks();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<PcbItemProjectLink> list = cache().getPcbItemProjectLinks();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
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

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processedUsed) {
        this.processed = processedUsed;
    }
}
