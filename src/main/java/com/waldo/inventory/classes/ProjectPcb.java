package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.parser.PcbItemParser;
import com.waldo.inventory.Utils.parser.PcbParser;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectPcb extends ProjectObject {

    public static final String TABLE_NAME = "projectpcbs";

    // Variables
    private Date lastParsedDate; // Compare with pcb file's 'Last modified' date to check if should parse again
    private HashMap<String, List<PcbItem>> pcbItemMap;
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
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DbManager.OBJECT_UPDATE: {
                break;
            }
            case DbManager.OBJECT_DELETE: {
                List<ProjectPcb> list = db().getProjectPcbs();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
        db().notifyListeners(changedHow, this, db().onProjectPcbChangedListenerList);
    }

    private HashMap<String, List<PcbItem>> getPcbItemsFromParser(File fileToParse) {
        hasParsed = false;
        HashMap<String, List<PcbItem>> pcbItems = new HashMap<>();

        if (getParser() == null) {
            return pcbItems;
        }

        pcbItems = getParser().parse(fileToParse);

        // Update pcb items in database
        PcbItemParser.getInstance().updatePcbItemDb(pcbItems);

        // Update links with project
        PcbItemParser.getInstance().updatePcbItemProjectLinksDb(pcbItems, this);

        lastParsedDate = new Date(Calendar.getInstance().getTime().getTime());
        hasParsed = true;
        save();
        return pcbItems;
    }

    private HashMap<String, List<PcbItem>> getPcbItemsFromDb() {
        List<PcbItem> itemList = SearchManager.sm().findPcbItemsForProjectPcb(getId());
        HashMap<String, List<PcbItem>> itemMap = new HashMap<>();

        for (PcbItem item : itemList) {
            String sheet = item.getSheetName();
            if (!itemMap.containsKey(sheet)) {
                itemMap.put(sheet, new ArrayList<>());
            }
            itemMap.get(sheet).add(item);
        }

        return itemMap;
    }

    public boolean parseAgain() {
        boolean result = false;
        File file = new File(getDirectory());
        if (file.exists()) {
            HashMap<String, List<PcbItem>> newMap = getPcbItemsFromParser(file);
            if (newMap.size() > 0) {
                pcbItemMap = newMap;
                result = true;
            }
        }
        return result;
    }

    public HashMap<String, List<PcbItem>> getPcbItemMap() {
        if (pcbItemMap == null) {
            File file = new File(getDirectory());
            if (lastParsedDate != null) {
                if (file.exists()) {
                    Date fileLastModified = new Date(file.lastModified());
                    if (fileLastModified.after(lastParsedDate)) { // Parse again
                        pcbItemMap = getPcbItemsFromParser(file);
                    } else { // Get from db
                        pcbItemMap = getPcbItemsFromDb();
                    }
                } else { // Invalid file, can happen when opening app on different computer, try to get items from db
                    pcbItemMap = getPcbItemsFromDb();
                }
            } else { // Never parsed: try to parse if file exists
                if (file.exists()) {
                    pcbItemMap = getPcbItemsFromParser(file);
                } else { // Invalid file, can happen when opening app on different computer, try to get items from db
                    pcbItemMap = getPcbItemsFromDb();
                }
            }
        }
        return pcbItemMap;
    }

    public Date getLastParsedDate() {
        return lastParsedDate;
    }

    private void setLastParsedDate(Date lastParsedDate) {
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
}