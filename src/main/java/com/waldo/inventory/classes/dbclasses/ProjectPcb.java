package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.DateUtils;
import com.waldo.utils.FileUtils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectPcb extends ProjectObject {

    public static final String TABLE_NAME = "projectpcbs";

    // Variables
    private Date lastParsedDate; // Compare with pcb file's 'Last modified' date to check if should parse again
    private List<PcbItemProjectLink> pcbItemProjectLinks;
    private boolean hasParsed;
    private int amount;

    private List<CreatedPcb> createdPcbs;

    public ProjectPcb() {
        super(TABLE_NAME);
    }

    public ProjectPcb(String name) {
        super(TABLE_NAME);
        setName(name);
    }

    public ProjectPcb(long projectId) {
        super(TABLE_NAME);
        setProjectId(projectId);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        // Add parameters
        statement.setString(ndx++, getDirectory());
        statement.setLong(ndx++, getProjectId());
        statement.setLong(ndx++, getProjectIDEId());
        SerialBlob blob = FileUtils.fileToBlob(getRemarksFile());
        if (blob != null) {
            statement.setBlob(ndx++, blob);
        } else {
            statement.setString(ndx++, null);
        }
        if (lastParsedDate != null) {
            statement.setTimestamp(ndx++, new Timestamp(lastParsedDate.getTime()));
        } else {
            statement.setTimestamp(ndx++, null);
        }
        statement.setString(ndx++, getDescription());
        statement.setInt(ndx++, getAmount());

        return ndx;
    }

//    @Override
//    public boolean equals(Object obj) {
//        boolean res = super.equals(obj);
//        if (res) {
//            if (obj instanceof ProjectPcb) {
//                ProjectPcb ref = (ProjectPcb) obj;
//
//                return ((ref.getProj))
//
//            } else {
//                res = false;
//            }
//        }
//        return res;
//    }

    @Override
    public String createRemarksFileName() {
        return getId() + "_PcbRemarks_";
    }

    @Override
    public ProjectPcb createCopy() {
        return createCopy(new ProjectPcb());
    }

    @Override
    public ProjectPcb createCopy(DbObject copyInto) {
        ProjectPcb cpy = (ProjectPcb) super.createCopy(copyInto);
        cpy.setLastParsedDate(getLastParsedDate());
        cpy.setAmount(getAmount());
        return cpy;
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

    public void updateOrderState() {

    }

    public int numberOfComponents() {
        return getPcbItemList().size();
    }

    private List<PcbItemProjectLink> getPcbItemsFromParser(File fileToParse) {
        hasParsed = false;
        List<PcbItemProjectLink> pcbItemLinks = new ArrayList<>();

        if (getParser() == null) {
            return pcbItemLinks;
        }

        // Parse the items from the file
        HashMap<String, List<PcbItem>> parsedItems = getParser().parse(fileToParse);

        // Update pcb items in database
        pcbItemLinks.addAll(PcbItemParser.getInstance().updatePcbItemDb(this, parsedItems));

        // Update links with project
        PcbItemParser.getInstance().updatePcbItemProjectLinksDb(this, pcbItemLinks);

        // Try to find (and update) item links
        PcbItemParser.getInstance().updatePcbItemItemLinks(pcbItemLinks);

        lastParsedDate = DateUtils.now();
        hasParsed = true;
        save();
        return pcbItemLinks;
    }

    private List<PcbItemProjectLink> getPcbItemsFromDb() {
        return SearchManager.sm().findPcbItemLinksWithProjectPcb(getId());
    }

    public boolean parseAgain() {
        boolean result = false;
        File file = new File(getDirectory());
        if (file.exists()) {
            List<PcbItemProjectLink> links = getPcbItemsFromParser(file);
            if (links.size() > 0) {
                for (PcbItemProjectLink link : pcbItemProjectLinks) {
                    link.delete();
                }
                pcbItemProjectLinks.clear();
                pcbItemProjectLinks.addAll(links);
                result = true;
            }
        }
        return result;
    }

    public List<PcbItemProjectLink> getPcbItemList() {
        if (pcbItemProjectLinks == null) {
            File file = new File(getDirectory());
            if (lastParsedDate != null) {
                if (file.exists()) {
                    Date fileLastModified = new Date(file.lastModified());
                    if (fileLastModified.after(lastParsedDate)) { // Parse again
                        pcbItemProjectLinks = getPcbItemsFromParser(file);
                    } else { // Get from db
                        pcbItemProjectLinks = getPcbItemsFromDb();
                    }
                } else { // Invalid file, can happen when opening app on different computer, try to get items from db
                    pcbItemProjectLinks = getPcbItemsFromDb();
                }
            } else { // Never parsed: try to parse if file exists
                if (file.exists()) {
                    pcbItemProjectLinks = getPcbItemsFromParser(file);
                } else { // Invalid file, can happen when opening app on different computer, try to get items from db
                    pcbItemProjectLinks = getPcbItemsFromDb();
                }
            }
            findKnownOrders(pcbItemProjectLinks);
        }
        return pcbItemProjectLinks;
    }

    private void findKnownOrders(List<PcbItemProjectLink> projectLinks) {
        if (projectLinks != null && projectLinks.size() > 0) {
            List<ItemOrder> planned = SearchManager.sm().findPlannedOrders(Statics.DistributorType.Items);
            if (planned.size() > 0) {
                for (ItemOrder itemOrder : planned) {
                    for (ItemOrderLine oi : itemOrder.getItemOrderLines()) {
                        for (PcbItemProjectLink link : projectLinks) {
                            if (link.getPcbItemItemLinkId() > UNKNOWN_ID) {
                                if (oi.getItemId() == link.getPcbItemItemLink().getItemId()) {
                                    link.getPcbItem().setOrderLine(oi);
                                    link.getPcbItem().setOrderAmount(oi.getAmount());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean hasLinkedItems() {
        if (getPcbItemList() != null && getPcbItemList().size() > 0) {
            for (PcbItemProjectLink link : getPcbItemList()) {
                if (link.hasMatchedItem()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Date getLastParsedDate() {
        return lastParsedDate;
    }

    public void setLastParsedDate(Date lastParsedDate) {
        this.lastParsedDate = lastParsedDate;
    }

    public void setLastParsedDate(Timestamp lastParsedDate) {
        if (lastParsedDate != null) {
            this.lastParsedDate = new Date(lastParsedDate.getTime());
        }
    }

    public PcbParser getParser() {
        if (getProjectIDEId() > UNKNOWN_ID) {
            return getProjectIDE().getPcbItemParser();
        }
        return null;
    }

    public boolean hasParsed() {
        return hasParsed;
    }

    @Override
    public String getDirectory() {
        return super.getDirectory();
    }

    @Override
    public void setDirectory(String directory) {
        super.setDirectory(directory);
    }

    @Override
    public long getProjectIDEId() {
        return super.getProjectIDEId();
    }

    @Override
    public void setProjectIDEId(long projectIDEId) {
        super.setProjectIDEId(projectIDEId);
    }

    @Override
    public long getProjectId() {
        return super.getProjectId();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            amount = 0;
        }
        this.amount = amount;
    }

    public List<CreatedPcb> getCreatedPcbs() {
        if (createdPcbs == null) {
            createdPcbs = SearchManager.sm().findCreatedPcbsForProjectPcb(getId());
        }
        return createdPcbs;
    }

    public void updateCreatedPcbs() {
        createdPcbs = null;
    }
}