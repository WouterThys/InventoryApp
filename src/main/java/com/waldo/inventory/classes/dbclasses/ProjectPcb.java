package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.managers.SearchManager;

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

        return ndx;
    }

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
        return cpy;
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<ProjectPcb> list = cache().getProjectPcbs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<ProjectPcb> list = cache().getProjectPcbs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

    public int numberOfComponents() {
        return getPcbItemMap().size();
    }

    private List<PcbItemProjectLink> getPcbItemsFromParser(File fileToParse) {
        hasParsed = false;
        List<PcbItemProjectLink> pcbItemLinks = new ArrayList<>();

        if (getParser() == null) {
            return pcbItemLinks;
        }

        HashMap<String, List<PcbItem>> pcbItems = getParser().parse(fileToParse);

        // Update pcb items in database
        pcbItemLinks.addAll(PcbItemParser.getInstance().updatePcbItemDb(this, pcbItems));

        // Update links with project
        PcbItemParser.getInstance().updatePcbItemProjectLinksDb(this, pcbItemLinks);

        // Update links with item
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
                pcbItemProjectLinks.clear();
                pcbItemProjectLinks.addAll(links);
                result = true;
            }
        }
        return result;
    }

    public List<PcbItemProjectLink> getPcbItemMap() {
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
            List<Order> planned = SearchManager.sm().findPlannedOrders();
            if (planned.size() > 0) {
                for (Order order : planned) {
                    for (OrderItem oi : order.getOrderItems()) {
                        for (PcbItemProjectLink link : projectLinks) {
                            if (link.getPcbItemItemLinkId() > UNKNOWN_ID) {
                                if (oi.getItemId() == link.getPcbItemItemLink().getItemId()) {
                                    link.getPcbItem().setOrderItem(oi);
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
        if (getPcbItemMap() != null && getPcbItemMap().size() > 0) {
            for (PcbItemProjectLink link : getPcbItemMap()) {
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
}