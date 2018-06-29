package com.waldo.inventory.database;

import com.waldo.inventory.Main;
import com.waldo.inventory.classes.dbclasses.DbHistory;
import com.waldo.inventory.classes.dbclasses.DbObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.waldo.inventory.Utils.Statics.QueryType.*;

public class DatabaseHelper {

    public static void insert(PreparedStatement stmt, DbObject dbo) throws SQLException {
        dbo.addParameters(stmt);
        stmt.execute();

        try (ResultSet rs = stmt.getGeneratedKeys()) {
            rs.next();
            dbo.setId(rs.getLong(1));
        }

        // Listeners
        dbo.tableChanged(Insert);

        // Log to db history
        if (Main.LOG_HISTORY && !(dbo instanceof DbHistory)) {
            try {
                DbHistory dbHistory = new DbHistory(Insert, dbo);
                dbHistory.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void update(PreparedStatement stmt, DbObject dbo) throws SQLException {
        int ndx = dbo.addParameters(stmt);
        if (ndx > 0) {
            stmt.setLong(ndx, dbo.getId());
            stmt.execute();
        }

        // Listeners
        dbo.tableChanged(Update);

        // Log to db history
        if (Main.LOG_HISTORY && !(dbo instanceof DbHistory)) {
            try {
                DbHistory dbHistory = new DbHistory(Update, dbo);
                dbHistory.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void delete(PreparedStatement stmt, DbObject dbo) throws SQLException {
        stmt.setLong(1, dbo.getId());
        stmt.execute();
        dbo.setDeleted(true);

        // Listeners
        dbo.tableChanged(Delete);

        // Log to db history
        if (Main.LOG_HISTORY && !(dbo instanceof DbHistory)) {
            try {
                DbHistory dbHistory = new DbHistory(Delete, dbo);
                dbHistory.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
