package com.waldo.inventory.classes;

import com.waldo.inventory.database.DbManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectOther extends DbObject {

    public static final String TABLE_NAME = "projectothers";

    // Variables


    public ProjectOther() {
        super(TABLE_NAME);
    }


    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        int ndx = 1;

        // Add parameters

        return ndx;
    }

    @Override
    public ProjectOther createCopy(DbObject copyInto) {
        ProjectOther cpy = (ProjectOther) copyInto;
        copyBaseFields(cpy);

        // Add variables

        return cpy;
    }

    @Override
    public ProjectOther createCopy() {
        return createCopy(new ProjectOther());
    }

    //
    // DbManager tells the object is updated
    //
    @Override
    public void tableChanged(int changedHow) {
        switch (changedHow) {
            case DbManager.OBJECT_INSERT: {

                break;
            }
            case DbManager.OBJECT_UPDATE: {

                break;
            }
            case DbManager.OBJECT_DELETE: {

                break;
            }
        }
    }

    public static ProjectOther getUnknownProjectOther() {
        ProjectOther u = new ProjectOther();
        u.setName(UNKNOWN_NAME);
        u.setId(UNKNOWN_ID);
        u.setCanBeSaved(false);
        return u;
    }

    // Getters and setters
}