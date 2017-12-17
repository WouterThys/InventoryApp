package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItemProjectLink extends DbObject {

    public static final String TABLE_NAME = "pcbitemprojectlinks";

    // Link
    private long pcbItemId; // Id of PcbItem
    private long projectPcbId; // Link to pcb in project

    private PcbItem pcbItem;
    private ProjectPcb projectPcb;

    // Specific for this PCB
    private String value;
    private String pcbSheetName;
    private List<String> references;

    private int usedCount; // Amount used = amount not available anymore in stock
    private boolean used;






    private boolean processed; // Helper variable for used dialog


    public PcbItemProjectLink() {
        super(TABLE_NAME);
    }

    public PcbItemProjectLink(PcbItem pcbItem, ProjectPcb projectPcb) {
        super(TABLE_NAME);
        this.pcbItem = pcbItem;
        this.projectPcb = projectPcb;

        if (pcbItem != null) {
            pcbItemId = pcbItem.getId();
            value = pcbItem.getValue();
            pcbSheetName = pcbItem.getSheetName();
            references = pcbItem.getReferences();
        }

        if (projectPcb != null) {
            projectPcbId = projectPcb.getId();
        }
    }

    @Override
    public String toString() {
        String result = "";
        if (getProjectPcb() != null) {
            result = projectPcb.toString();
        }
        if (getPcbItem() != null) {
            result += " <-> " + pcbItem.toString();
        }
        return result;
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        if (getPcbItemId() < UNKNOWN_ID) {
            if (pcbItem != null) {
                setPcbItemId(pcbItem.getId());
            } else {
                setPcbItemId(UNKNOWN_ID);
            }
        }
        if (getProjectPcbId() < UNKNOWN_ID) {
            setProjectPcbId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getPcbItemId());
        statement.setLong(ndx++, getProjectPcbId());
        statement.setString(ndx++, getValue());
        statement.setInt(ndx++, getUsedCount());

        if (getUsedCount() > 0) {
            setUsed(true);
        }

        // Pcb item variables relevant for project
        statement.setString(ndx++, getReferenceString());
        statement.setString(ndx++, getPcbSheetName());

        return ndx;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PcbItemProjectLink) {
            PcbItemProjectLink ref = (PcbItemProjectLink) obj;

            return ref.getPcbItemId() == getPcbItemId() &&
                    ref.getProjectPcbId() == getProjectPcbId() &&
                    ref.getValue().equals(getValue()) &&
                    ref.getUsedCount() == getUsedCount() &&
                    ref.getReferenceString().equals(getReferenceString()) &&
                    ref.getPcbSheetName().equals(getPcbSheetName());
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
        cpy.setValue(getValue());
        cpy.setUsedCount(getUsedCount());
        cpy.setPcbSheetName(getPcbSheetName());
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

            setReferences(refs);
        }
    }

    public void setPcbSheetName(String pcbSheetName) {
        this.pcbSheetName = pcbSheetName;
    }

    public String getPrettyName() {
        if (pcbItemId > UNKNOWN_ID) {
            String part = getPcbItem().getPartName();
            String val = getValue();
            if (part.equals(value)) {
                return part;
            } else {
                return (part + " - " + val);
            }
        }
        return "";
    }

    public int getNumberOfItems() {
        return getReferences().size();
    }

    // Getters and setters

    public long getPcbItemId() {
        return pcbItemId;
    }

    public void setPcbItemId(long pcbItemId) {
        if (pcbItem != null && pcbItem.getId() != pcbItemId) {
            pcbItem = null;
        }
        this.pcbItemId = pcbItemId;
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

    public PcbItem getPcbItem() {
        if (pcbItem == null) {
            pcbItem = SearchManager.sm().findPcbItemById(pcbItemId);
        }
        return pcbItem;
    }

    public ProjectPcb getProjectPcb() {
        if (projectPcb == null) {
            projectPcb = SearchManager.sm().findProjectPcbById(projectPcbId);
        }
        return projectPcb;
    }

    public String getPcbSheetName() {
        if (pcbSheetName == null) {
            pcbSheetName = "";
        }
        return pcbSheetName;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public List<String> getReferences() {
        if (references == null) {
            references = new ArrayList<>();
        }
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public String getReferenceString() {
        StringBuilder refs = new StringBuilder();
        if (getReferences().size() > 0) {
            getReferences().sort(new ReferenceComparer());
            for (String r : getReferences()) {
                refs.append(r).append(", ");
            }
            refs.delete(refs.lastIndexOf(", "), refs.length());
        }
        return refs.toString();
    }

    private static class ReferenceComparer implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            String one = StringUtils.leftPad(o1, 5, "0");
            String two = StringUtils.leftPad(o2, 5, "0");
            return one.compareTo(two);
        }
    }










    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processedUsed) {
        this.processed = processedUsed;
    }
}
