package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
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
    private long pcbItemItemLinkId; // Link with matched item

    private PcbItem pcbItem;
    private ProjectPcb projectPcb;
    private PcbItemItemLink pcbItemItemLink;

    // Specific for this PCB
    private String value;
    private String pcbSheetName;
    private List<String> references;


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
        if (getPcbItemItemLinkId() < UNKNOWN_ID) {
            setPcbItemItemLinkId(UNKNOWN_ID);
        }

        statement.setLong(ndx++, getPcbItemId());
        statement.setLong(ndx++, getProjectPcbId());
        statement.setLong(ndx++, getPcbItemItemLinkId());

        statement.setString(ndx++, getValue());

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
                    ref.getPcbItemItemLinkId() == getPcbItemItemLinkId() &&
                    ref.getValue().equals(getValue()) &&
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
        cpy.setPcbItemItemLinkId(getPcbItemItemLinkId());
        cpy.setValue(getValue());
        cpy.setPcbSheetName(getPcbSheetName());
        cpy.setReferences(getReferences());

        return cpy;
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

    public int getNumberOfReferences() {
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

    public long getPcbItemItemLinkId() {
        return pcbItemItemLinkId;
    }

    public void setPcbItemItemLinkId(long pcbItemItemLinkId) {
        if (pcbItemItemLink != null && pcbItemItemLink.getId() != pcbItemItemLinkId) {
            pcbItemItemLink = null;
        }
        this.pcbItemItemLinkId = pcbItemItemLinkId;
    }

    public PcbItemItemLink getPcbItemItemLink() {
        if (pcbItemItemLink == null) {
            pcbItemItemLink = SearchManager.sm().findPcbItemItemLinkById(pcbItemItemLinkId);
        }
        return pcbItemItemLink;
    }

    public boolean hasMatchedItem() {
        return pcbItemItemLinkId > UNKNOWN_ID;
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
}
