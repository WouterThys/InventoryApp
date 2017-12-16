package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.database.DatabaseAccess;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class ProjectCode extends ProjectObject {

    public static final String TABLE_NAME = "projectcodes";

    // Variables
    private String language;

    public ProjectCode() {
        super(TABLE_NAME);
    }
    public ProjectCode(String name) {
        super(TABLE_NAME);
        setName(name);
    }
    public ProjectCode(long projectId) {
        super(TABLE_NAME);
        setProjectId(projectId);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = addBaseParameters(statement);

        // Add parameters
        statement.setString(ndx++, getLanguage());
        statement.setString(ndx++, getDirectory());
        statement.setLong(ndx++, getProjectId());
        statement.setLong(ndx++, getProjectIDEId());
        SerialBlob blob = FileUtils.fileToBlob(getRemarksFile());
        if (blob != null) {
            statement.setBlob(ndx++, blob);
        } else {
            statement.setString(ndx++, null);
        }

        return ndx;
    }

    @Override
    public ProjectCode createCopy(DbObject copyInto) {
        ProjectCode cpy = (ProjectCode) super.createCopy(copyInto);
        cpy.setLanguage(getLanguage());
        return cpy;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result =  super.equals(obj);
        if (result) {
            if (!(obj instanceof ProjectCode)) {
                return false;
            }
            if (!(((ProjectCode)obj).getLanguage().equals(getLanguage()))) return false;
            if (!(((ProjectCode)obj).getDirectory().equals(getDirectory()))) return false;
            if (!(((ProjectCode)obj).getRemarksFileName().equals(getRemarksFileName()))) return false;
            if (!(((ProjectCode)obj).getProjectId() == getProjectId())) return false;
            if (!(((ProjectCode)obj).getProjectIDEId() == getProjectIDEId())) return false;
        }
        return result;
    }

    @Override
    public ProjectCode createCopy() {
        return createCopy(new ProjectCode());
    }

    //
    // DatabaseAccess tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DatabaseAccess.OBJECT_INSERT: {
                List<ProjectCode> list = cache().getProjectCodes();
                if (!list.contains(this)) {
                    list.add(this);
                }
                break;
            }
            case DatabaseAccess.OBJECT_UPDATE: {
                break;
            }
            case DatabaseAccess.OBJECT_DELETE: {
                List<ProjectCode> list = cache().getProjectCodes();
                if (list.contains(this)) {
                    list.remove(this);
                }
                break;
            }
        }
    }

//    public static ProjectCode getUnknownProjectCode() {
//        ProjectCode u = new ProjectCode();
//        u.setNameTxt(UNKNOWN_NAME);
//        u.setId(UNKNOWN_ID);
//        u.setCanBeSaved(false);
//        return u;
//    }

    @Override
    public String createRemarksFileName() {
        return getId() + "_CodeRemarks_";
    }

    // Getters and setters

    public String getLanguage() {
        if (language == null) {
            language = "";
        }
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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