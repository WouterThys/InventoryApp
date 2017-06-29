package com.waldo.inventory.classes;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectType extends DbObject {

    public static final String TABLE_NAME = "projecttypes";

    private String extension;

    @Override
    protected void insert(PreparedStatement statement) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, extension);
        statement.execute();
    }

    @Override
    protected void update(PreparedStatement statement) throws SQLException{
        statement.setString(1, name);
        statement.setString(2, iconPath);
        statement.setString(3, extension);
        statement.setLong(4, id); // WHERE id
        statement.execute();
    }

    public ProjectType() {
        super(TABLE_NAME);
    }

    public static ProjectType getUnknownProjectType() {
        ProjectType pt = new ProjectType();
        pt.setName(UNKNOWN_NAME);
        pt.setId(UNKNOWN_ID);
        pt.setCanBeSaved(false);
        return pt;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result) {
            if (!(obj instanceof ProjectType)) {
                return false;
            }
            if (!(((ProjectType)obj).getExtension().equals(getExtension()))) {
                return false;
            }
        }
        return result;
    }

    @Override
    public boolean hasMatch(String searchTerm) {
        if (super.hasMatch(searchTerm)) {
            return true;
        } else {
            return (getExtension().toUpperCase().contains(searchTerm));
        }
    }

    @Override
    public ProjectType createCopy() {
        ProjectType projectType = new ProjectType();
        copyBaseFields(projectType);
        projectType.setExtension(getExtension());
        return projectType;
    }

    public String getExtension() {
        if (extension == null) {
            extension = "";
        }
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
