package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.parser.KiCad.KiCadParser;
import com.waldo.inventory.Utils.parser.ProjectParser;
import com.waldo.inventory.classes.kicad.KcComponent;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import javafx.util.converter.TimeStringConverter;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ProjectPcb extends ProjectObject {

    public static final String TABLE_NAME = "projectpcbs";

    // Variables
    private Date lastParsedDate; // Compare with pcb file's 'Last modified' date to check if should parse again
    private List<KcComponent> pcbItemList;
    private KiCadParser parser;

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

    public static ProjectPcb getUnknownProjectPcbs() {
        ProjectPcb u = new ProjectPcb();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    private List<KcComponent> getPcbItemsFromParser(File fileToParse, ProjectParser<KcComponent> parser) {
        if (parser == null) {
            this.parser = (KiCadParser) parser;
        }
        List<KcComponent> pcbItems = new ArrayList<>();

        if (fileToParse != null && fileToParse.isFile()) {
            if (parser.isFileValid(fileToParse)) {
                parser.parse(fileToParse);
                pcbItems = parser.sortList(parser.getParsedData());
                matchItems(pcbItems);
            } else {
                if (FileUtils.getExtension(fileToParse).equals(parser.getFileExtension())) { // getFileExtension should be "pro"
                    getPcbItemsFromParser(fileToParse.getParentFile(), parser);
                } else {
//                    JOptionPane.showMessageDialog(
//                            PcbItemPanel.this,
//                            "The file cannot be parsed with the KiCad parser..",
//                            "Invalid file",
//                            JOptionPane.ERROR_MESSAGE
//                    ); // TODO: log errors
                }
            }
        } else {
            // Search for file
            List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, parser.getFileExtension(), true);
            if (actualFiles != null && actualFiles.size() == 1) {
                parser.parse(actualFiles.get(0));
                pcbItems = parser.sortList(parser.getParsedData());
                matchItems(pcbItems);
            } else {
//                JOptionPane.showMessageDialog(
//                        PcbItemPanel.this,
//                        "Found no or too many files with extension " + kiCadParser.getFileExtension() + " ..",
//                        "File not found",
//                        JOptionPane.ERROR_MESSAGE
//                ); // TODO: log errors
            }
        }

        lastParsedDate = new Date(Calendar.getInstance().getTime().getTime());
        return pcbItems;
    }

    private void matchItems(List<KcComponent> pcbItems) {
        for (KcComponent component : pcbItems) {
            component.findMatchingItems();
        }
    }

    private List<KcComponent> getPcbItemsFromDb() {
        return SearchManager.sm().findPcbItemsForProjectPcb(getId());
    }

    public List<KcComponent> getPcbItemList() {
        if (pcbItemList == null) {
            File file = new File(getDirectory());
            if (lastParsedDate != null) {
                if (file.exists()) {
                    Date fileLastModified = new Date(file.lastModified());
                    if (fileLastModified.after(lastParsedDate)) { // Parse again
                        pcbItemList = getPcbItemsFromParser(file, getProjectIDE().getProjectParser());
                    } else { // Get from db
                        pcbItemList = getPcbItemsFromDb();
                    }
                } else { // Invalid file, can happen when opening app on different computer, try to get items from db
                    pcbItemList = getPcbItemsFromDb();
                }
            } else { // Never parsed: try to parse if file exists
                if (file.exists()) {
                    pcbItemList = getPcbItemsFromParser(file, getProjectIDE().getProjectParser());
                } else { // Try db anyway??
                    pcbItemList = getPcbItemsFromDb();
                }
            }
        }
        return pcbItemList;
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

    public KiCadParser getParser() {
        return parser;
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